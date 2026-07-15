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
