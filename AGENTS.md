# Repository guidance

This repository is the source of truth for personal AI-tool configuration.
Consult the root and nearest directory `README.md` before changing a surface.

## Canonical sources and tool views

- Keep reusable instructions, skills, prompts, and other shared artifacts in one canonical location.
- Treat `codex/`, `claude/`, `opencode/`, and `copilot/` as installable tool views.
- Link shared content into them when a tool needs its own projection.
- Keep genuinely tool-specific configuration and skills directly in the corresponding tool view.
- Do not copy shared content or introduce a build step for local configuration.
- Link only managed entries into a tool home;
  never replace the whole home, because it may also contain credentials, sessions, plugins, logs, and caches.

## Active repository configuration

- Do not create `.codex/`, `.claude/`, `.opencode/`, `.copilot/`,
  or another native project-discovery directory unless it is intentionally configuration for this repository.
- The root `AGENTS.md` is intentionally active repository guidance.
- Keep only repository-maintenance skills under `.agents/skills/`.
  Globally reusable skills belong under the inert `agents/skills/` source.
- Avoid specially discovered filenames such as `AGENTS.md`, `CLAUDE.md`, and `SKILL.md` for passive documentation.

## Links and setup

- Commit repository-internal symlinks with relative, forward-slash targets.
- Keep repository-internal symlink targets inside this repository.
- Never replace a symlink with a copied file to work around checkout problems.
- Update every projection when moving or renaming canonical content.
- Installation must expose repository sources through links declared in the relevant `ai-setup.yml`;
  do not add copying or generated configuration.
- Keep mutable tool homes outside this worktree, including ignored paths.

## Safety and documentation

- Never commit credentials, tokens, private keys, session state, generated authentication files, or other secrets.
- Commit non-secret settings when their history is useful;
  express machine differences through environment variables or explicit profiles.
- Put discovery rules, installation mappings, compatibility notes, and public documentation links in the relevant `README.md`.
