# GitHub Copilot configuration

## Installation decision

On Windows, set
[`COPILOT_HOME`](https://docs.github.com/en/copilot/reference/copilot-cli-reference/cli-config-dir-reference#changing-the-location-of-the-configuration-directory)
to `P:\caches\ai\copilot`. It replaces the whole `~/.copilot` directory,
including configuration, sessions, plugins, permissions, and logs, so it stays
outside this repository. Link durable entries such as `settings.json`,
instructions, agents, skills, hooks, and MCP definitions back to this overlay.
The complete distinction between user-editable and automatically managed files
is documented in the
[`~/.copilot` directory reference](https://docs.github.com/en/copilot/reference/copilot-cli-reference/cli-config-dir-reference).

Personal MCP registrations belong in
[`mcp-config.json`](https://docs.github.com/en/copilot/reference/copilot-cli-reference/cli-command-reference#mcp-server-configuration).
Keep `config.json`, permissions, OAuth material, secrets, installed plugins, and
session state external. Merge the existing machine's `settings.json` into this
overlay before running setup.

[Copilot documents](https://docs.github.com/en/copilot/concepts/agents/about-agent-skills#about-agent-skills)
both `~/.agents/skills` and `~/.copilot/skills` as personal skill roots. Shared
skills are visible through the first; only Copilot-specific skills belong in
`copilot/skills`. Do not install the same shared skill into both roots.
