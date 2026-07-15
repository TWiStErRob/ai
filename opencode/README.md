# OpenCode configuration

Global instructions use OpenCode's [`AGENTS.md`](https://opencode.ai/docs/rules#global) entrypoint.

Runtime settings remain in `opencode.jsonc`;
TUI settings use the separate [`tui.jsonc`](https://opencode.ai/docs/config/#tui).
The other linked directories are OpenCode's [documented](https://opencode.ai/docs/config/#locations)
`commands/`, `modes/`, `plugins/`, `skills/`, `themes/`, and `tools/` roots.

OpenCode [merges personal skills](https://opencode.ai/docs/skills#place-files)
discovered from `~/.config/opencode/skills`, `~/.claude/skills`, and `~/.agents/skills`.
Shared skills stay in `agents/skills`; OpenCode-only skills stay in `opencode/skills`.

Set [`OPENCODE_DISABLE_CLAUDE_CODE_SKILLS=true`](https://opencode.ai/docs/rules#claude-code-compatibility)
so OpenCode ignores `.claude/skills` while continuing to discover shared skills from `~/.agents/skills`.
This is an environment variable; there is no equivalent property in the
[live JSON configuration schema](https://opencode.ai/config.json).

## Relocation

It is possible to set [`OPENCODE_CONFIG_DIR`](https://opencode.ai/docs/cli#environment-variables).
An example could be `P:\caches\ai\opencode`.
