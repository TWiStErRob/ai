---
name: register-mcp
description: |
  Register an MCP server implemented under this repository's mcps/ directory with all supported tools.
  Use when adding, removing, renaming, enabling, disabling, or repairing an MCP server registration,
  or when changing its command, arguments, environment, transport, or tool filters.
---

# Register MCP

Keep each MCP server implementation canonical under `mcps/`
and express its registration in each tool's native configuration.

## Workflow

1. Read the root `AGENTS.md`, `mcps/AGENTS.md`, and the selected server's files.
2. Determine the server name, transport, launch command, arguments, required environment variable names, and supported
   tools.
3. Never read or record secret values.
4. Inspect every existing registration before editing.
   Preserve unrelated settings and existing comments.
5. Update the canonical repository configuration for every requested tool.
6. Use `kotlin` plus the canonical `.main.kts` path for a Kotlin STDIO server.
   Resolve paths for the target machine;
   do not assume the repository is on the same drive or under the same home directory on every computer.
   Do not commit a machine-specific absolute path until the repository's portability strategy for that registration is
   explicit.
7. Keep credentials out of Git.
   Store only environment variable names or secret placeholders supported by the tool.
8. Validate
    * JSON, JSONC, and TOML syntax.
    * When the corresponding CLI is installed, inspect the resolved registrations with a validation command.
    * Do not start the MCP server merely to validate syntax unless the user requests an end-to-end check.
9. Do not run `setup.main.kts`, mutate external tool homes, commit, or push unless the user explicitly requests that
   action.

| Tool        | Registration source                               | Validation command  |
|-------------|---------------------------------------------------|---------------------|
| ChatGPT Web | Not possible without hosting somewhere.           |                     |
| Claude Code | See the Claude limitation below                   | `claude mcp list`   |
| Codex       | `codex/config.toml`, under `[mcp_servers.<name>]` | `codex mcp list`    |
| Copilot CLI | `copilot/mcp-config.json`                         | `copilot mcp list`  |
| OpenCode    | `opencode/opencode.jsonc`, under `mcp.<name>`     | `opencode mcp list` |

## Registration syntax

For example, assume a local STDIO server named `example` whose entrypoint is `<repo>/mcps/example/server.main.kts`.
Treat these as fragments to merge into existing files,
and replace `<repo>` with the resolved absolute repository path using forward slashes.

### Codex `config.toml`

```toml
[mcp_servers.example]
command = "kotlin"
args = ["<repo>/mcps/example/server.main.kts"]
```

### OpenCode `opencode.jsonc`

```json
{
	"mcp": {
		"example": {
			"type": "local",
			"command": ["kotlin", "<repo>/mcps/example/server.main.kts"],
		}
	}
}
```

### Copilot CLI `mcp-config.json`

```json
{
	"mcpServers": {
		"example": {
			"type": "stdio",
			"command": "kotlin",
			"args": ["<repo>/mcps/example/server.main.kts"],
			"tools": ["*"]
		}
	}
}
```

### Claude Code user registration

```shell
claude mcp add --transport stdio --scope user example -- kotlin "<repo>/mcps/example/server.main.kts"
```

## Registration rules

- Treat `mcps/` as implementation, not registration.
  Do not make tools discover that directory as if it were a native configuration root.
- Keep tool-specific registration formats even when they repeat the same server name and launch command.
  Do not invent a lowest-common-denominator MCP schema.
- Use STDIO for a local `.main.kts` server. Keep stdout protocol-clean; diagnostics must go to stderr.
- Preserve explicit tool filters, startup timeouts, and enabled/disabled state.
- Fail clearly when a tool cannot represent a required server option.

## Claude Code limitation

Claude Code stores user-scoped MCP registrations in `~/.claude.json`,
alongside mutable account and project state.
Never symlink or commit that whole file.

When Claude registration is requested,
first check whether a version-controlled plugin MCP configuration is appropriate.
Otherwise show the exact `claude mcp add --scope user` command and obtain confirmation before running it,
because it mutates external user state.
Keep the canonical server implementation under `mcps/` either way.
