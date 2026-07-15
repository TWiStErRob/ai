# AI Setup

Personal, source-controlled configuration for Codex, Claude Code, OpenCode, GitHub Copilot, and ChatGPT web.

## Layout

- `agents/` is the canonical cross-tool instruction and skill baseline.
- `.agents/skills/` contains intentionally active workflows for maintaining this repository itself.
- `mcps/` contains canonical MCP server implementations;
  registrations remain in each tool's native configuration.
- `codex/`, `claude/`, `opencode/`, and `copilot/` are tool-specific overlays;
  `chatgpt/` holds the future ChatGPT web package. Their plain names are
  intentional: native dot-directories would become active project configuration
  while working in this repository.
- The root `AGENTS.md` and `.agents/skills/` are the only intentionally active AI-tool configuration for this repository;
  installable views otherwise remain inert.
- Each locally installable view contains an `ai-setup.yml` manifest
  declaring the links managed within that view's resolved home.
- Mutable tool homes stay outside Git.
  On the current Windows machine the repository is `P:\config\ai`
  and runtime homes might be relocated under `P:\caches\ai\<tool>`.

Tracked configuration remains in this repository.
Setup points tools at the external runtime homes through environment variables
and symlinks only the managed entries back to their canonical repository sources.
Shared Agent Skills remain exposed at `~/.agents/skills`.

## Installation

### Windows

Enable [Windows Developer Mode](https://learn.microsoft.com/en-us/windows/advanced-settings/developer-mode),
then clone with Git symlink checkout enabled:

```shell
git clone -c core.symlinks=true https://github.com/TWiStErRob/ai.git
git config --get core.symlinks # must print true
```

Git records symlinks specially, as small files containing only the target path.
With [`core.symlinks=false`](https://git-scm.com/docs/git-config#Documentation/git-config.txt-coresymlinks) (default),
the symlinks would be checked out raw instead of actually linked.
A regular file containing only a relative target path is therefore usually a mischecked-out symlink;
clone again with `core.symlinks=true` rather than copying its intended target.

### macOS and Linux

```bash
git clone https://github.com/TWiStErRob/ai.git
```

### Setup

```shell
kotlin setup.main.kts                 # help and available tools
kotlin setup.main.kts --dry-run codex # validate without changing anything
kotlin setup.main.kts codex
```

Setup requires the [Kotlin command-line compiler](https://kotlinlang.org/docs/command-line.html).
It validates the selected tool's `ai-setup.yml`,
resolves its configured home environment variable when set or its documented default otherwise,
and then creates the declared links.
Setup never sets or persists environment variables.
Run it once for each tool you want; there is deliberately no `all` command.

## Decisions

- Shared content has one canonical source; local installation never copies or builds configuration.
- The global baseline should be strong enough that project repositories need only project-specific instructions and skills.
- Repository-internal symlinks are committed with relative, forward-slash targets that remain inside the repository.
- Only individually managed entries are linked into external tool homes;
  mutable homes are never placed or linked wholesale inside this worktree.
- Machine-specific non-secret settings are committed. Credentials, sessions, logs, caches, and generated state are not.
- GitHub Actions validates Windows, macOS, and Linux checkouts,
  configuration formats, skill names, and effective tool discovery sets.

Folder-specific decisions and gotchas live in the corresponding
[`agents/`](agents/README.md),
[`codex/`](codex/README.md),
[`claude/`](claude/README.md),
[`opencode/`](opencode/README.md),
and
[`copilot/`](copilot/README.md)
guides.
ChatGPT web work is reserved under [`chatgpt/`](chatgpt/README.md).
