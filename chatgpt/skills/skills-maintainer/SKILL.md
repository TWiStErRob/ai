---
name: skills-maintainer
description: |
  Update and synchronize Agent Skills between a live ChatGPT installation and GitHub.
  Use when asked from ChatGPT web or mobile to
  * improve installed skills,
  * record ChatGPT edits in Git,
  * deploy the latest main branch into installed skills,
  * compare drift,
  * restore skills from Git.
  Do not use merely to run another skill.
---

# Skills Maintainer

Keep live ChatGPT skills useful immediately while preserving Git as their durable history and rebuild source.

## Required capabilities

- Read/write access to the configured GitHub repository.
- The ability to inspect and edit skills in the current ChatGPT workspace.

If either capability is unavailable, complete the safe portion only and report exactly which side remains unsynchronized.
Never claim that a skill was updated or recorded without reading it back from that surface.

Before changing an installed ChatGPT personal skill, load and follow the current `skill-creator` skill.
It defines the authoritative personal-skills checkout and save procedure. In ChatGPT Work, start with
`SKILLS_ROOT=/root/.codex/skills/remote-skills`; this directory is the checkout even though
`/root/.codex/skills` is not a Git repository and the Git metadata is stored separately.
Do not mistake the parent directory, a `skills.read` snapshot, or an ordinary scratch copy for the editable installation.

## Configured source

Use `TWiStErRob/ai` GitHub repository and its `main` branch by default.
Read `chatgpt/ai-setup.yml` from the latest commit on `main` to discover the managed skills:

- each key is the ChatGPT projection path;
- each value resolves relative to `chatgpt/ai-setup.yml` and identifies the canonical Git directory;
- the last key component is the live ChatGPT skill name;
- the last source-directory component is the canonical Git skill name.

Do not modify or synchronize skills absent from this allow-list unless the user explicitly asks to add them, or creating a new skill.

## Choose the direction

- A request to **improve, change, fix, or update** a live skill is live-first: update ChatGPT, then record that exact result in GitHub.
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
- For an explicit Git-to-live sync, the mapped Git tree is authoritative for every authored file,
  including `SKILL.md` name and description, `agents/openai.yaml`, assets, references, scripts, and deletions.
  Do not preserve a conflicting live name, alias, icon, or instruction merely because it was already installed.
- When a projection renames a skill, rewrite only the `SKILL.md` frontmatter `name` at the boundary.
  The live name matches the projection directory; the Git name matches the canonical source directory.
  Determine this transformation exclusively from the current allow-list mapping; never infer it from the installed name.
- Do not treat API-hosted skills and ChatGPT workspace skills as interchangeable resources.

After saving a personal skill, wait for reconciliation, fetch the personal-skills remote, and verify its resulting tree.
The current conversation's `skills.read` result and Skills UI may be cached and are not valid deployment read-backs.
Reconciliation can materialize platform-managed UI metadata or icons. Compare the reconciled tree with the intended
projection, distinguish those rewrites from the authored Git input, and never silently call a mismatch synchronized.
If reconciliation replaces an authored file, restore it and retry once; if it is replaced again, report the exact
remaining platform drift instead of repeatedly overwriting it.

## Live ChatGPT to Git

1. Read the latest `main` commit, the allow-list, and the mapped canonical skill tree.
2. Inspect the installed skill and establish whether it already differs from Git, if so offer to deploy first.
3. Apply the requested improvement using the available ChatGPT skill editor.
4. Read back and validate the complete installed skill tree.
5. Convert it to canonical form: omit generated `manifest.txt`, restore the canonical directory name, and restore only the `SKILL.md` frontmatter name when the projection uses an alias.
6. Preserve repository-only files unless the live edit intentionally deleted or replaced them; resolve ambiguity before deleting anything.
7. Recheck `main`. If it advanced, reload and reconcile rather than overwriting newer work.
8. Create a focused branch from current `main`, commit the exact canonicalized result, and open a pull request.
   Record the base commit and verified live snapshot.
9. Immediately tell the user the PR is ready for review and provide its link.
   This is a progress update, not completion.

Always use a pull request for ChatGPT-originated changes; never push them directly to `main`.
Reuse an open synchronization PR only when it represents the same live-to-Git stream.

### Watch and follow the merge

When GitHub CLI is available, first watch CI:

```bash
gh pr checks "<pull-request-url>" --watch
```

This watches checks, not the merge.
Afterwards, monitor `gh pr view "<pull-request-url>" --json state,mergedAt,baseRefOid,headRefOid,url`
with the platform's long-running wait mechanism until the PR is merged or closed.
Avoid rapid polling and do not pretend monitoring remains active if the surface cannot persist;
report **Awaiting merge** and resume next time.

- If closed without merge, preserve the live version and report **ChatGPT ahead**.
- If merged, fetch the then-latest `main`, not merely the PR's final commit.
- Compare every allow-listed skill between the recorded PR base and current `main`.
- Pull review edits, merge resolutions, and every other managed-skill change made while the PR was open into ChatGPT.
- If a live skill changed independently after the PR snapshot,
  preserve both versions and reconcile instead of overwriting it.
- Read back every updated live skill, verify it against current `main`, then fetch `main` again.
  Repeat if it advanced during synchronization.

If the live update succeeds but GitHub fails, do not roll it back automatically.
Preserve or return the complete exported bundle when possible and report **ChatGPT ahead** with the failed Git step.

## Latest main to live ChatGPT

1. Read current `main` and the allow-list.
2. Resolve the requested skill or managed skill set to canonical directories.
3. Inspect installed versions before replacement so the discarded drift can be reported, not preserved.
   An explicit request to sync, restore, deploy, or update from Git authorizes replacing that drift with Git.
4. Build the live projection from Git: preserve the complete tree, use the allow-list target directory name,
   rewrite only the frontmatter name when the allow-list actually aliases it, and exclude build output and authentication material.
5. Validate and save each projected skill through the personal-skills checkout, one skill operation at a time.
6. Wait for reconciliation, fetch the personal-skills remote, and compare the resulting complete tree with the projection.
   Retry an authored-file rewrite once, then report persistent platform drift precisely.
7. Report the exact source commit and whether the reconciled installed skill matches it after documented transformations.

Handle multiple skills independently so one failure does not obscure the others.

## Status and reconciliation

For a status request, compare current `main` with the live skill after normalizing only documented name transformations and generated-file exclusions.
Report skill, Git commit, state, and material differences.

When both sides changed,
 * preserve both versions,
 * summarize structural and semantic differences,
 * combine non-conflicting edits,
 * and ask which behavior should win only for genuine conflicts.
 * Never use timestamps alone to choose a winner.

## Git consistency

The Git tree must represent the exact verified live skill after reversing only documented packaging transformations.
Do not describe an unmerged branch as already present on `main`.

## Completion

Report each affected skill with one of these states:

- **Synchronized** — verified equivalent in ChatGPT and Git.
- **Awaiting merge** — the live update and review pull request exist, but the change is not yet on `main`.
- **ChatGPT ahead** — live update succeeded, but Git recording is pending or failed.
- **Git ahead** — Git contains the intended version, but the live update is pending or failed.
- **Conflict** — both sides changed and user direction is required.
- **Unchanged** — verified equivalent before and after the request.

Include the Git commit or pull-request link when one exists
and state whether a new ChatGPT conversation or reload is needed for an updated maintainer skill to take effect.
