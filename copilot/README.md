# GitHub Copilot configuration

## Installation decision

On Windows, set
[`COPILOT_HOME`](https://docs.github.com/en/copilot/reference/copilot-cli-reference/cli-config-dir-reference#changing-the-location-of-the-configuration-directory)
to `P:\caches\ai\copilot`. It replaces the whole `~/.copilot` directory,
including configuration, sessions, plugins, permissions, and logs, so it stays
outside this repository. Link durable entries such as `settings.json`,
instructions, agents, skills, hooks, and MCP definitions back to this overlay.

[Copilot documents](https://docs.github.com/en/copilot/concepts/agents/about-agent-skills#about-agent-skills)
both `~/.agents/skills` and `~/.copilot/skills` as personal skill roots. Shared
skills are visible through the first; only Copilot-specific skills belong in
`copilot/skills`. Do not install the same shared skill into both roots.
