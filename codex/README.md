# Codex configuration

## Installation decision

On Windows, set
[`CODEX_HOME`](https://learn.chatgpt.com/docs/config-file/environment-variables)
to `P:\caches\ai\codex`. It is the root for Codex configuration and mutable
state, so it must remain outside this repository and exist before Codex starts.

Personal skills remain under the documented
[`~/.agents/skills`](https://learn.chatgpt.com/docs/build-skills) root. Do not put
another `AGENTS.md` in this source view: Codex discovers project instructions by
walking the repository, while the global entrypoint belongs directly under
`CODEX_HOME` ([instruction discovery](https://learn.chatgpt.com/docs/agent-configuration/agents-md)).

Durable settings and MCP registrations belong in
[`config.toml`](https://learn.chatgpt.com/docs/extend/mcp#connect-codex-to-an-mcp-server).
Codex also discovers personal command rules from
[`rules/`](https://learn.chatgpt.com/docs/agent-configuration/rules). Merge the
existing machine's `config.toml` into this overlay before running setup; do not
move authentication, sessions, plugin installations, or generated app state.
