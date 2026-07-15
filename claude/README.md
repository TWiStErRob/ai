# Claude Code configuration

Claude's documented personal skill root is [`~/.claude/skills`](https://code.claude.com/docs/en/skills#where-skills-live),
it does not touch `~/.agents/skills` at all.
`claude/skills/` therefore includes all skills from `agents/skills/` via directory symlinks;
Claude-only skills live directly in the same overlay.

Other shared config: 
 * [`settings.json`](https://code.claude.com/docs/en/configuration) contains durable personal settings and hook definitions;
 * `hooks/` contains scripts referenced by those settings.
 * Claude also discovers personal [`rules/` and `output-styles/`](https://code.claude.com/docs/en/claude-directory).
 * User-scoped MCP registrations live in `~/.claude.json`, alongside mutable account and project state ([MCP scopes](https://code.claude.com/docs/en/mcp#mcp-installation-scopes)).
   Do not symlink or commit that file.

Keep server implementations in `mcps/` and use the repository's `register-mcp` skill to register them safely.

## Relocation

It is possible to set [`CLAUDE_CONFIG_DIR`](https://code.claude.com/docs/en/env-vars#environment-variables).
An example could be `P:\caches\ai\claude`.

The variable relocates settings, sessions, plugins, and Windows credentials,
so the directory stays outside this repository.
