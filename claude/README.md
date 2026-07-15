# Claude Code configuration

## Installation decision

On Windows, set
[`CLAUDE_CONFIG_DIR`](https://code.claude.com/docs/en/env-vars#environment-variables)
to `P:\caches\ai\claude`. The variable relocates settings, sessions, plugins,
and Windows credentials, so the directory stays outside this repository.

Claude's documented personal skill root is
[`~/.claude/skills`](https://code.claude.com/docs/en/skills#where-skills-live).
`claude/skills/` therefore selects shared skills with relative directory
symlinks to `agents/skills/`;
[Claude follows and deduplicates such symlinks](https://code.claude.com/docs/en/skills#where-skills-live).
Claude-only skills live directly in the same overlay.

[`settings.json`](https://code.claude.com/docs/en/configuration) contains durable
personal settings and hook definitions; `hooks/` contains scripts referenced by
those settings. Claude also discovers personal
[`rules/` and `output-styles/`](https://code.claude.com/docs/en/claude-directory).

User-scoped MCP registrations live in `~/.claude.json`, alongside mutable
account and project state ([MCP scopes](https://code.claude.com/docs/en/mcp#mcp-installation-scopes)).
Do not symlink or commit that file. Keep server implementations in `mcps/` and
use the repository's `register-mcp` skill to register them safely.
