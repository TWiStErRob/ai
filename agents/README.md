# Shared agent surface

This directory is the canonical cross-tool baseline:

- `instructions/global.md` is referenced or linked from each tool's global
  instruction entrypoint.
- `skills/` contains portable skills following the
  [Agent Skills specification](https://agentskills.io/specification). A
  tool-specific skill belongs in that tool's `skills/` overlay.

[Codex](https://learn.chatgpt.com/docs/build-skills),
[OpenCode](https://opencode.ai/docs/skills#place-files), and
[GitHub Copilot](https://docs.github.com/en/copilot/concepts/agents/about-agent-skills#about-agent-skills)
discover personal skills from `~/.agents/skills`. Claude documents only
[`~/.claude/skills`](https://code.claude.com/docs/en/skills#where-skills-live),
so `claude/skills/` contains relative symlinks to the shared skills selected for
Claude.

CI rejects duplicate skill names within a tool's effective discovery set unless
every occurrence is the same canonical skill.
