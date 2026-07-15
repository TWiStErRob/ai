# Shared agent surface

This directory is the canonical cross-tool baseline:

- `instructions/global.md` is referenced or linked from each tool's global instruction entrypoint.
- `skills/` contains portable skills following the [Agent Skills specification](https://agentskills.io/specification).
- A tool-specific skill belongs in that tool's `skills/` overlay.

Automatic discovery:
 * `~/.agents/skills` is automatically discovered by these:
   * [Codex](https://learn.chatgpt.com/docs/build-skills)
   * [OpenCode](https://opencode.ai/docs/skills#place-files)
   * [GitHub Copilot](https://docs.github.com/en/copilot/concepts/agents/about-agent-skills#about-agent-skills)

Claude [does not discover `~/.agents/skills`](https://code.claude.com/docs/en/skills#where-skills-live),
so [claude/skills/](../claude/skills) mirrors every shared skill through a relative symlink.
Claude-only skills live directly in the same overlay.
