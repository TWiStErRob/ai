---
name: skills-maintainer
description: Update and synchronize Agent Skills between a live ChatGPT installation and GitHub. Use when asked from ChatGPT web or mobile to improve installed skills immediately, record ChatGPT edits in Git, pull the latest main branch into installed skills, compare drift, or restore skills from Git. Do not use merely to run another skill.
---

# Skills Maintainer

Keep live ChatGPT skills useful immediately while preserving Git as their durable history and rebuild source.

## Required capabilities

- Read/write access to the configured GitHub repository.
- The ability to inspect and edit skills in the current ChatGPT workspace.w

If either capability is unavailable, complete the safe portion only and report exactly which side remains unsynchronized.
Never claim that a skill was updated or recorded without reading it back from that surface.

## Configured source

Use `TWiStErRob/ai` GitHub repository and its `main` branch by default.
Read `chatgpt/ai-setup.yml` from the latest commit on `main` to discover the managed skills:

- each key is the ChatGPT projection path;
- each value resolves relative to `chatgpt/ai-setup.yml` and identifies the canonical Git directory;
- the final key component is the live ChatGPT skill name;
- the final source-directory component is the canonical Git skill name.

Do not modify or synchronize skills absent from this allow-list unless the user explicitly asks to add them.

## Choose the direction

- A request to **improve, change, fix, or update** a live skill is live-first: update ChatGPT, then record that exact result in Git.
- A request to **pull, refresh, restore, or sync from main** is Git-first: read the latest `main`, then update ChatGPT.
- A request to **compare, inspect, or show status** is read-only.
- If both sides changed independently, reconcile explicitly. Do not choose a winner silently.

## Invariants

- Treat the installed ChatGPT skill as the live deployment and Git as the durable, rebuildable record.
- Preserve the complete skill directory, including instructions, references, scripts, assets, and `agents/openai.yaml`.
- Never commit credentials, tokens, generated authentication state, downloaded archives, or ChatGPT-generated `manifest.txt` files.
- Validate `SKILL.md` frontmatter and internal relative references before updating either side.
- Preserve unrelated files and unrelated skills.
- Apply intentional deletions on both sides; otherwise Git would not reproduce the live skill.
- When a projection renames a skill, rewrite only the `SKILL.md` frontmatter `name` at the boundary. The live name matches the projection directory; the Git name matches the canonical source directory.
- Do not treat API-hosted skills and ChatGPT workspace skills as interchangeable resources.

## Live ChatGPT to Git

1. Read the latest `main` commit, the allow-list, and the mapped canonical skill tree.
2. Inspect the installed skill and establish whether it already differs from Git.
3. Apply the requested improvement using the available ChatGPT skill editor.
4. Read back and validate the complete installed skill tree.
5. Convert it to canonical form: omit generated `manifest.txt`, restore the canonical directory name, and restore only the `SKILL.md` frontmatter name when the projection uses an alias.
6. Preserve repository-only files unless the live edit intentionally deleted or replaced them; resolve ambiguity before deleting anything.
7. Recheck `main`. If it advanced, reload and reconcile rather than overwriting newer work.
8. Create a focused branch from current `main`, commit the exact canonicalized result, and open a pull request. Record the base commit and verified live snapshot.
9. Immediately tell the user the PR is ready for review and provide its link. This is a progress update, not completion.

Always use a pull request for ChatGPT-originated changes; never push them directly to `main`. Reuse an open synchronization PR only when it represents the same live-to-Git stream.

### Watch and follow the merge

When GitHub CLI is available, first watch CI:

```bash
gh pr checks "<pull-request-url>" --watch
```

This watches checks, not the merge. Afterwards, monitor `gh pr view "<pull-request-url>" --json state,mergedAt,baseRefOid,headRefOid,url` with the platform's long-running wait mechanism until the PR is merged or closed. Avoid rapid polling and do not pretend monitoring remains active if the surface cannot persist; report **Awaiting merge** and resume next time.

- If closed without merge, preserve the live version and report **ChatGPT ahead**.
- If merged, fetch the then-latest `main`, not merely the PR's final commit.
- Compare every allow-listed skill between the recorded PR base and current `main`.
- Pull review edits, merge resolutions, and every other managed-skill change made while the PR was open into ChatGPT.
- If a live skill changed independently after the PR snapshot, preserve both versions and reconcile instead of overwriting it.
- Read back every updated live skill, verify it against current `main`, then fetch `main` again. Repeat if it advanced during synchronization.

If the live update succeeds but GitHub fails, do not roll it back automatically. Preserve or return the complete exported bundle when possible and report **ChatGPT ahead** with the failed Git step.

## Latest main to live ChatGPT

1. Read current `main` and the allow-list.
2. Resolve the requested skill or managed skill set to canonical directories.
3. Inspect installed versions before replacing anything.
4. If a live skill contains changes absent from Git, report the drift and request direction unless the user explicitly asked to discard it.
5. Build the live projection from Git: preserve the complete tree, use the allow-list target directory name, rewrite only the frontmatter name when aliased, and exclude build output and authentication material.
6. Validate the projection, update it in ChatGPT, and read it back.
7. Report the exact source commit and whether the installed skill matches it after documented projection transformations.

Handle multiple skills independently so one failure does not obscure the others. Update `skills-maintainer` itself last because the current conversation may retain its previous instructions until reloaded.

## Status and reconciliation

For a status request, compare current `main` with the live skill after normalizing only documented name transformations and generated-file exclusions. Report skill, Git commit, state, and material differences.

When both sides changed, preserve both versions, summarize structural and semantic differences, combine non-conflicting edits, and ask which behavior should win only for genuine conflicts. Never use timestamps alone to choose a winner.

## Git consistency

The Git tree must represent the exact verified live skill after reversing only documented packaging transformations. Do not describe an unmerged branch as already present on `main`.

## Completion

Report each affected skill with one of these states:

- **Synchronized** — verified equivalent in ChatGPT and Git.
- **Awaiting merge** — the live update and review pull request exist, but the change is not yet on `main`.
- **ChatGPT ahead** — live update succeeded, but Git recording is pending or failed.
- **Git ahead** — Git contains the intended version, but the live update is pending or failed.
- **Conflict** — both sides changed and user direction is required.
- **Unchanged** — verified equivalent before and after the request.

Include the Git commit or pull-request link when one exists and state whether a new ChatGPT conversation or reload is needed for an updated maintainer skill to take effect.
