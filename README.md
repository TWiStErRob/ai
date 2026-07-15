# AI Setup

Personal, source-controlled configuration for Codex, Claude Code, OpenCode,
GitHub Copilot, and ChatGPT web.

## Layout

- `agents/` is the canonical cross-tool instruction and skill baseline.
- `.agents/skills/` contains intentionally active workflows for maintaining this
  repository itself.
- `mcps/` contains canonical MCP server implementations; registrations remain
  in each tool's native configuration.
- `codex/`, `claude/`, `opencode/`, and `copilot/` are tool-specific overlays;
  `chatgpt/` holds the future ChatGPT web package. Their plain names are
  intentional: native dot-directories would become active project configuration
  while working in this repository.
- Each locally installable view contains an `ai-setup.yml` manifest declaring
  the links managed within that view's resolved home.
- Mutable tool homes stay outside Git. On the current Windows machine the
  repository is `P:\config\ai` and runtime homes are under
  `P:\caches\ai\<tool>`.

Tracked configuration remains in this repository. Setup points tools at the
external runtime homes through environment variables and symlinks only the
managed entries back to their canonical repository sources. Shared Agent Skills
remain exposed at `~/.agents/skills`.

## Installation

### Windows

Enable [Windows Developer Mode](https://learn.microsoft.com/en-us/windows/advanced-settings/developer-mode),
then clone with Git symlink checkout enabled:

```powershell
git clone -c core.symlinks=true https://github.com/TWiStErRob/ai.git
Set-Location ai
git config --get core.symlinks # must print true
kotlin setup.main.kts                 # help and available tools
kotlin setup.main.kts --dry-run codex # validate without changing anything
kotlin setup.main.kts codex
```

Git records symlinks specially; with
[`core.symlinks=false`](https://git-scm.com/docs/git-config#Documentation/git-config.txt-coresymlinks)
they are checked out as small files containing only the target path.

Setup requires the
[Kotlin command-line compiler](https://kotlinlang.org/docs/command-line.html).
It validates the selected tool's `ai-setup.yml`, resolves its configured home
environment variable when set or its documented default otherwise, and then
creates the declared links. Setup never sets or persists environment variables.
Run it once for each tool you want; there is deliberately no `all` command.

### macOS and Linux

Git checks out symlinks normally:

```bash
git clone https://github.com/TWiStErRob/ai.git
cd ai
kotlin setup.main.kts                 # help and available tools
kotlin setup.main.kts --dry-run codex # validate without changing anything
kotlin setup.main.kts codex
```

The same Kotlin prerequisite and per-tool setup model apply. Machine-specific
runtime roots remain external and are selected through the tool environment
variables documented in each tool guide.

## Decisions

- Shared content has one canonical source; local installation never copies or
  builds configuration.
- Repository-internal symlinks are relative and committed.
- Machine-specific non-secret settings are committed. Credentials, sessions,
  logs, caches, and generated state are not.
- GitHub Actions validates Windows, macOS, and Linux checkouts, configuration
  formats, skill names, and effective tool discovery sets.

Folder-specific decisions and gotchas live in the corresponding
[`agents/`](agents/README.md), [`codex/`](codex/README.md),
[`claude/`](claude/README.md), [`opencode/`](opencode/README.md), and
[`copilot/`](copilot/README.md) guides. ChatGPT web work is reserved under
[`chatgpt/`](chatgpt/README.md).
