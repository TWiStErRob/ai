# Repository guidance

## Purpose

This repository is the source of truth for personal AI-tool configuration on
Windows, macOS, and Linux. It contains shared instructions, skills, agents, MCP
definitions, and native configuration for tools such as Codex, OpenCode, Claude,
and GitHub Copilot.

Prefer a strong global setup plus small project-local instruction files and
project-specific skills. Do not copy global content into individual projects.

## Source and tool views

- Keep each shared instruction, skill, prompt, or other reusable artifact in one
  canonical location.
- Tool directories are installable views. Use committed relative symlinks from
  those directories to canonical shared artifacts when the tool can consume the
  same file unchanged.
- Keep genuinely tool-specific TOML, YAML, JSON, or JSONC configuration in the
  corresponding tool directory. Native adapters are expected; duplicated shared
  prose is not.
- Local installation must expose repository files through symlinks. Do not add a
  build or copy step for local configuration.

Do not link an entire tool home directory unless it is documented to contain
configuration only. Tool homes may also contain credentials, sessions, logs,
caches, downloaded plugins, or other mutable state. Link the specific managed
files and subdirectories instead.

### Avoid accidental project configuration

Do not name installable source directories `.codex`, `.claude`, `.opencode`,
`.copilot`, or another tool's native project-discovery path. While an agent is
working in this repository, those directories would become active project
configuration rather than inert sources for the global installation.

Use non-discoverable directory names such as `codex/`, `claude/`, `opencode/`,
and `copilot/` for global tool views. Setup scripts map their managed contents
to native home paths. The root `AGENTS.md` is intentional repository guidance;
other native project configuration should be added only when it is meant to
affect work on this repository itself.

Global instruction entrypoints need extra care because names such as
`AGENTS.md` and `CLAUDE.md` can also be discovered below the repository root.
Prefer linking the installed entrypoint directly to a canonical instruction
file rather than keeping another specially named copy inside a tool view.

## Symlink conventions

- Commit repository-internal symlinks with relative targets so the checkout can
  move between machines.
- Store symlink targets with forward slashes for Windows/macOS/Linux portability.
- Keep symlink targets inside this repository unless an external target is an
  intentional installation link created by a setup script.
- Never replace a shared symlink with a copied file to work around a local
  checkout problem. Repair the checkout or setup instead.
- When moving or renaming canonical content, update and validate every link that
  points to it.

### Windows checkout requirements

Git records a symlink as mode `120000` plus its target text. If
`core.symlinks=false`, Git for Windows checks it out as a small regular file
instead of a live link.

- Enable Windows Developer Mode so non-elevated Git and setup processes can
  create symbolic links.
- Clone with `git clone -c core.symlinks=true ...` as documented in `README.md`.
- Confirm `git config --get core.symlinks` returns `true` before diagnosing
  broken repository links.
- A file whose contents are only a relative target path may be a symlink that was
  checked out incorrectly.
- If a clean checkout used the wrong setting, prefer recloning correctly. Do not
  run a destructive checkout command over uncommitted work.

macOS and Linux normally check out Git symlinks directly, but CI must still
validate that committed links resolve on all three supported operating systems.

## Machine-specific configuration

- Commit non-secret machine profiles and tool options when their history is
  useful.
- Express differing roots and executable locations through environment variables
  or explicitly named machine profiles.
- Never commit credentials, access tokens, private keys, session state, or
  generated authentication files. Reference secrets through environment
  variables or the platform credential store.

### Environment-directed runtime homes

Some tools support relocating their native home or configuration directory
through an environment variable. On the current Windows machine, mutable homes
belong under `P:\caches\ai\<tool>`, outside the `P:\config\ai` Git worktree.
Other machines must choose an explicit external runtime root. This replaces a
top-level home symlink; it is not a content generation step.

- Document whether a variable redirects configuration only or the tool's entire
  mutable home before adopting it.
- Prefer config-only variables. A broad home variable may also redirect
  credentials, sessions, logs, caches, downloaded plugins, databases, and
  machine-local permissions.
- Never put a broad home inside this repository, even when ignored. Git cleanup,
  worktree replacement, or a future tracked path must not be able to remove
  sessions and caches.
- Keep durable configuration canonical in this repository. Setup links
  individual managed entries from the external tool home back to their sources
  in the corresponding tool view; generated state remains ordinary external
  files.
- Prefer OS credential storage and keep defense-in-depth secret scanning in CI.
- Setup must create the external target directory first when required, use the
  absolute runtime root declared by the machine profile, and verify which
  location the tool resolved.
- Do not assume that changing a tool-specific home also relocates shared
  `~/.agents` discovery.

## Documentation

- Keep a `README.md` in each top-level shared surface and tool view.
- Use these READMEs for discovery rules, install mappings, compatibility traps,
  and links to authoritative public documentation.
- When public documentation and observed source code differ, link both, describe
  the exact discrepancy, and identify the behaviour assumed by setup or CI.
- Do not use an active filename such as `AGENTS.md`, `CLAUDE.md`, or `SKILL.md`
  for passive explanatory notes.

## Verification

Changes should be validated on `windows-2025`, `macos-26`, and `ubuntu-24.04`
in GitHub Actions.

At minimum, validation should check:

- committed symlinks retain Git mode `120000` and resolve to existing targets;
- skills follow the Agent Skills `SKILL.md` structure and required frontmatter;
- skill names do not collide within any tool's effective discovery set, except
  for aliases that resolve to the same canonical skill;
- JSON, JSONC, YAML, and TOML configuration parses successfully;
- setup does not copy canonical configuration or capture secrets.
