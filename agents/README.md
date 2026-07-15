# Shared agent surface

This directory is the canonical cross-tool baseline:

- `instructions/global.md` is linked directly to each tool's global instruction entrypoint;
  specially named intermediate copies would risk becoming active repository configuration.
- `skills/` contains portable skills following the [Agent Skills specification](https://agentskills.io/specification).
- A tool-specific skill belongs in that tool's `skills/` overlay.

Automatic discovery:
 * `~/.agents/skills` is automatically discovered by these:
   * [Codex](https://learn.chatgpt.com/docs/build-skills)
   * [OpenCode](https://opencode.ai/docs/skills#place-files)
   * [GitHub Copilot](https://docs.github.com/en/copilot/concepts/agents/about-agent-skills#about-agent-skills)

Relocating a tool-specific home does not relocate this shared `~/.agents` root.

Claude [does not discover `~/.agents/skills`](https://code.claude.com/docs/en/skills#where-skills-live),
so [claude/skills/](../claude/skills) mirrors every shared skill through a relative symlink.
Claude-only skills live directly in the same overlay.
