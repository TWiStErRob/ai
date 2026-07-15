@file:DependsOn("org.snakeyaml:snakeyaml-engine:3.0.1")
@file:DependsOn("dev.harrel:json-schema:1.9.1")
@file:DependsOn("com.fasterxml.jackson.core:jackson-databind:2.22.1")

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dev.harrel.jsonschema.ValidatorFactory
import dev.harrel.jsonschema.providers.JacksonNode
import org.snakeyaml.engine.v2.api.Load
import org.snakeyaml.engine.v2.api.LoadSettings
import org.snakeyaml.engine.v2.schema.JsonSchema
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.system.exitProcess

private val MANIFEST_NAME = "ai-setup.yml"
private val SCHEMA_NAME = "ai-setup.schema.json"

private enum class LinkAction { CREATE, KEEP }

private data class LinkPlan(
	val target: Path,
	val source: Path,
	val action: LinkAction,
)

private data class SetupPlan(
	val tool: String,
	val home: Path,
	val links: List<LinkPlan>,
)

private fun fail(message: String): Nothing = throw IllegalArgumentException(message)

private fun discoverTools(root: Path): List<String> =
	Files.list(root).use { children ->
		children
			.filter { Files.isRegularFile(it.resolve(MANIFEST_NAME)) }
			.map { it.name }
			.sorted()
			.toList()
	}

private fun printHelp(tools: List<String>) {
	println(
		"""
			Usage: setup.main.kts [--dry-run] <tool>
			
			Validate and install one tool's managed links.
			
			Options:
			  --dry-run  Validate and show the complete link plan without changing anything.
			  --help     Show this help.
			
			Tools: ${tools.joinToString(", ")}
		""".trimIndent()
	)
}

private fun parseYaml(manifest: Path): Any? {
	val settings = LoadSettings.builder()
		.setLabel(manifest.toString())
		.setAllowDuplicateKeys(false)
		.setSchema(JsonSchema())
		.build()
	return Load(settings).loadFromString(Files.readString(manifest))
}

private fun validateSchema(root: Path, manifest: Path, yaml: Any?) {
	val mapper = ObjectMapper()
	val nodes = JacksonNode.Factory(mapper)
	val schema = nodes.create(Files.readString(root.resolve(SCHEMA_NAME)))
	val instance = nodes.wrap(mapper.valueToTree<JsonNode>(yaml))
	val result = ValidatorFactory()
		.withJsonNodeFactory(nodes)
		.validate(schema, instance)

	if (!result.isValid) {
		val details = result.errors.joinToString("\n") { error ->
			val location = error.instanceLocation.ifEmpty { "/" }
			"  ${location}: ${error.error}"
		}
		fail("${manifest}: schema validation failed:\n${details}")
	}
}

private fun resolveHome(raw: String, source: String): Path {
	val userHome = Path.of(System.getProperty("user.home"))
	val expanded = when {
		raw == "~" -> userHome
		raw.startsWith("~/") -> userHome.resolve(raw.removePrefix("~/"))
		raw.startsWith("~") -> fail("${source} uses unsupported home syntax: ${raw}")
		else -> Path.of(raw)
	}
	if (!expanded.isAbsolute) {
		fail("${source} must resolve to an absolute path: ${raw}")
	}
	return expanded.normalize()
}

private fun resolvesTo(link: Path, source: Path): Boolean {
	if (!Files.isSymbolicLink(link)) {
		return false
	}
	val destination = Files.readSymbolicLink(link).let { raw ->
		if (raw.isAbsolute) raw else link.parent.resolve(raw)
	}.toAbsolutePath().normalize()
	return try {
		destination.toRealPath() == source.toRealPath()
	} catch (_: Exception) {
		destination == source
	}
}

private fun requireDirectoryAncestors(home: Path, target: Path) {
	var current: Path? = target.parent
	while (current != null && current.startsWith(home)) {
		if (Files.exists(current, NOFOLLOW_LINKS) && !Files.isDirectory(current)) {
			fail("Target parent exists but is not a directory: ${current}")
		}
		if (current == home) break
		current = current.parent
	}
}

@Suppress("UNCHECKED_CAST")
private fun createPlan(root: Path, tool: String): SetupPlan {
	val toolRoot = root.resolve(tool)
	val manifest = toolRoot.resolve(MANIFEST_NAME)
	val yaml = try {
		parseYaml(manifest)
	} catch (ex: Exception) {
		fail("${manifest}: invalid YAML: ${ex.message ?: ex.javaClass.simpleName}")
	}
	validateSchema(root, manifest, yaml)

	val config = yaml as Map<String, Any>
	val homeConfig = config.getValue("home") as Map<String, String>
	val defaultHome = homeConfig.getValue("default")
	val envName = homeConfig["env"]
	val configuredHome = envName?.let { name ->
		System.getenv(name)?.also { value ->
			if (value.isBlank()) fail("Environment variable ${name} is set but empty.")
		}
	}
	val homeSource = if (configuredHome != null) "Environment variable ${envName}" else "home.default"
	val home = resolveHome(configuredHome ?: defaultHome, homeSource)

	if (Files.exists(home, NOFOLLOW_LINKS) && !Files.isDirectory(home)) {
		fail("Home exists but is not a directory: ${home}")
	}

	val links = (config.getValue("links") as Map<String, String>).map { (targetRaw, sourceRaw) ->
		val target = home.resolve(Path.of(targetRaw)).normalize()
		val source = toolRoot.resolve(Path.of(sourceRaw)).normalize().toAbsolutePath()

		if (!target.startsWith(home)) fail("Link target escapes its home: ${targetRaw}")
		if (!source.startsWith(root)) fail("Link source escapes the repository: ${sourceRaw}")
		if (!Files.exists(source)) fail("Link source does not exist: ${source}")

		requireDirectoryAncestors(home, target)
		val action = when {
			!Files.exists(target, NOFOLLOW_LINKS) -> LinkAction.CREATE
			resolvesTo(target, source) -> LinkAction.KEEP
			else -> fail("Link target already exists and points elsewhere: ${target}")
		}
		LinkPlan(target, source, action)
	}

	links.forEachIndexed { index, link ->
		links.drop(index + 1).forEach { other ->
			if (link.target.startsWith(other.target) || other.target.startsWith(link.target)) {
				fail("Link targets overlap: ${link.target} and ${other.target}")
			}
		}
	}

	return SetupPlan(tool, home, links)
}

private fun execute(plan: SetupPlan, dryRun: Boolean) {
	println(if (dryRun) "Dry run: ${plan.tool}" else "Installing: ${plan.tool}")
	println("Home: ${plan.home}")
	plan.links.forEach { link ->
		val action = when (link.action) {
			LinkAction.CREATE -> if (dryRun) "would create" else "create"
			LinkAction.KEEP -> "already linked"
		}
		println("  ${action}: ${link.target} -> ${link.source}")
	}

	if (dryRun) {
		println("Validated: ${plan.tool}")
		return
	}

	Files.createDirectories(plan.home)
	plan.links.filter { it.action == LinkAction.CREATE }.forEach { link ->
		Files.createDirectories(link.target.parent)
		Files.createSymbolicLink(link.target, link.source)
	}
	println("Installed: ${plan.tool}")
}

try {
	val root = Path.of("").toAbsolutePath().normalize()
	if (!Files.isRegularFile(root.resolve(SCHEMA_NAME))) {
		fail("Run setup from the repository root; ${SCHEMA_NAME} was not found in ${root}.")
	}
	val tools = discoverTools(root)

	if (args.isEmpty() || args.singleOrNull() == "--help") {
		printHelp(tools)
	} else {
		val unknownOptions = args.filter { it.startsWith("-") && it != "--dry-run" }
		if (unknownOptions.isNotEmpty()) fail("Unknown option: ${unknownOptions.first()}")
		if (args.count { it == "--dry-run" } > 1) fail("Option --dry-run may be specified only once.")

		val toolArguments = args.filterNot { it == "--dry-run" }
		if (toolArguments.size != 1) {
			fail("Specify exactly one tool. Available tools: ${tools.joinToString(", ")}")
		}
		val tool = toolArguments.single()
		if (tool !in tools) {
			fail("Unknown tool '${tool}'. Available tools: ${tools.joinToString(", ")}")
		}
		execute(createPlan(root, tool), dryRun = "--dry-run" in args)
	}
} catch (ex: Exception) {
	System.err.println("error: ${ex.message ?: ex.javaClass.simpleName}")
	exitProcess(1)
}
