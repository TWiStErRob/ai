# Codex configuration

Personal skills remain under the documented [`~/.agents/skills`](https://learn.chatgpt.com/docs/build-skills) root.

Durable settings and MCP registrations belong in [`config.toml`](https://learn.chatgpt.com/docs/extend/mcp#connect-codex-to-an-mcp-server).
Codex also discovers personal command rules from [`rules/`](https://learn.chatgpt.com/docs/agent-configuration/rules).

Beware that a local `config.toml` contains a lot of other things,
like: authentication, sessions, plugin installations, or generated app state.

## Relocation

It is possible to set [`CODEX_HOME`](https://learn.chatgpt.com/docs/config-file/environment-variables).
An example could be `P:\caches\ai\codex`.

It is the root for Codex configuration and mutable state,
so it must remain outside this repository and exist before Codex starts.
