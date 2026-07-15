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
