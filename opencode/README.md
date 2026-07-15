# OpenCode configuration

## Installation decision

On Windows, set
[`OPENCODE_CONFIG_DIR`](https://opencode.ai/docs/cli#environment-variables) to
`P:\caches\ai\opencode`.

Global instructions use OpenCode's native
[`AGENTS.md`](https://opencode.ai/docs/rules#global) entrypoint. Runtime settings
and MCP registrations remain in `opencode.jsonc`; TUI settings use the separate
[`tui.jsonc`](https://opencode.ai/docs/config/#tui). The other linked directories
are OpenCode's documented
[`commands/`, `modes/`, `plugins/`, `skills/`, `themes/`, and `tools/` roots](https://opencode.ai/docs/config/#locations).

OpenCode merges personal skills discovered from
[`~/.config/opencode/skills`, `~/.claude/skills`, and `~/.agents/skills`](https://opencode.ai/docs/skills#place-files).
Shared skills stay in `agents/skills`; OpenCode-only skills stay in
`opencode/skills`. CI rejects conflicting names across that merged inventory.

## Gotcha: disabling Claude skills

Do not set `OPENCODE_DISABLE_CLAUDE_CODE_SKILLS`. The
[documentation](https://opencode.ai/docs/rules#claude-code-compatibility) says it
disables only `.claude/skills`, but the current
[flag implementation](https://github.com/anomalyco/opencode/blob/dev/packages/opencode/src/flag/flag.ts#L30-L53)
also maps it to `OPENCODE_DISABLE_EXTERNAL_SKILLS`, suppressing `.agents` skills.
There is no equivalent property in the
[live JSON configuration schema](https://opencode.ai/config.json).
