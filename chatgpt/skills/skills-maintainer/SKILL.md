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
- For deployment work, the ability to inspect and edit skills in the current ChatGPT workspace. A PR-only change requires GitHub access, not an installed-skill write.

If either capability is unavailable, complete the safe portion only and report exactly which side remains unsynchronized.
Never claim that a skill was updated or recorded without reading it back from that surface.

For installed-skill edits, load and follow the current `skill-creator` skill, including its save and verification workflow.

## Configured source

Use `TWiStErRob/ai` GitHub repository and its `main` branch by default.
Read `chatgpt/ai-setup.yml` from the latest commit on `main` to discover the managed skills:

- each key is the ChatGPT projection path;
- each value resolves relative to `chatgpt/ai-setup.yml` and identifies the canonical Git directory;
- the last key component is the live ChatGPT skill name;
- the last source-directory component is the canonical Git skill name.

Do not modify or synchronize skills absent from this allow-list unless the user explicitly asks to add them, or creating a new skill.

## Choose the direction

- A request to **create/open a PR**, or make changes **through/in a PR**, is PR-only unless the user also explicitly requests deployment. This takes precedence over general words such as improve, change, fix or update: prepare the changes on a repository branch without editing, saving or pushing the installed ChatGPT skill.
- A request to **improve, change, fix, or update** a live skill without a PR-only instruction is live-first: update ChatGPT, then record that exact result in GitHub.
- A request to **pull, refresh, restore, or sync from main** is Git-first: read the latest `main`, then update ChatGPT.
- A request to **compare, inspect, or show status** is read-only.
- Requested **PR review changes** update that PR branch. Deploy after merge only when deployment is within the user's authorized scope.

## Invariants

- Treat the installed ChatGPT skill as the live deployment and Git as the durable, rebuildable record.
- Preserve the complete skill directory, including instructions, references, scripts, assets, and `agents/openai.yaml`.
- Never commit credentials, tokens, generated authentication state, downloaded archives, or ChatGPT-generated `manifest.txt` files.
- Treat conversation history, connected records, and user-specific examples as private unless the user explicitly intends to publish them.
- Publish only information necessary to reproduce the skill; replace incidental names, addresses, contact details, and sensitive examples with generic wording.
- Validate `SKILL.md` frontmatter and internal relative references before updating either side.
- Preserve unrelated files and unrelated skills.
- For authorized synchronization, apply intentional deletions on both sides; otherwise Git would not reproduce the live skill. A PR-only change must leave the installed tree untouched.
- When a projection renames a skill, rewrite only the `SKILL.md` frontmatter `name` at the boundary.
  The live name matches the projection directory; the Git name matches the canonical source directory.
- Do not treat API-hosted skills and ChatGPT workspace skills as interchangeable resources.

Compare the verified saved tree with the intended result. Report any platform rewrite as remaining drift;
retry only when there is evidence that a changed approach can succeed.

## Live ChatGPT to Git

For PR-only requests, use the same repository review and PR steps below, but prepare and validate the proposed changes in the repository branch instead of applying steps 3–5 to the installed skill; the proposed repository tree is already in canonical form. Read the installed version only when useful for comparison; do not edit it, create an installed-skill commit, or attempt deployment as part of validation. Preserve unrelated differences without folding them into the PR. Record the repository base and proposed revision rather than claiming a verified deployment snapshot.

1. Read the latest `main` commit, the allow-list, and the mapped canonical skill tree.
2. For live-first work, compare the installed skill with Git and identify the live changes to record;
   reconcile independently changed Git content without replacing the live source. For PR-only work, compare the repository base and requested proposal; inspect installed differences only if relevant.
3. Apply any requested improvement through `skill-creator`; skip editing when only recording existing live changes.
4. Read back and validate the complete installed skill tree.
5. Convert it to canonical form:
   * omit generated `manifest.txt`,
   * restore the canonical directory name,
   * and restore only the `SKILL.md` frontmatter name when the projection uses an alias.
6. Preserve repository-only files unless the live edit intentionally deleted or replaced them;
   resolve ambiguity before deleting anything.
7. Recheck `main`. If it advanced, reload and reconcile rather than overwriting newer work.
8. Review the exact GitHub-bound content, including files, branch name, commit message, PR title, and PR body. Keep personal context and private source records out of explanatory examples.
9. Create a focused branch from current `main`, commit the resulting changes, and open a pull request.
   Record the base commit and, for live-first work, the verified live snapshot in the PR so later reconciliation can distinguish concurrent edits. For PR-only work, record the proposed revision and state that it is not deployed.
10. Immediately tell the user the PR is ready for review and provide its link.
   For a PR-only request, check CI and merge state, report **Awaiting merge — not deployed**, and complete the request. Do not wait for merge or deploy unless the user requested that follow-up. For a deployment-inclusive workflow, continue below.

If a write encounters a privacy or security block, do not route around it.
Review and minimize the intended disclosure, and proceed only through an allowed path with any required approval.

Always use a pull request for ChatGPT-originated changes; never push them directly to `main`.
Reuse an open synchronization PR only when it represents the same live-to-Git stream.

### Watch and follow the merge

Check CI and merge state through the available GitHub integration.
Use a persistent wait mechanism when available; otherwise report **Awaiting merge** and resume next time.
Do not imply that monitoring continues after the turn without such a mechanism.

- If closed without merge, preserve the live version and report any live changes still absent from `main`.
- If merged, fetch the then-latest `main`, not merely the PR's final commit. Merge alone does not authorize deployment after a PR-only request; continue into deployment steps only when deployment was also requested.
- Compare every allow-listed skill between the recorded PR base and current `main`.
- Deploy affected skills through **Latest main to live ChatGPT**, including review edits and merge resolutions.
  Use the recorded live snapshot to distinguish the PR's superseded content from new live edits;
  preserve and reconcile the latter. For other affected skills, use a verified synchronization baseline when available.
- Read back every updated live skill, verify it against current `main`, then fetch `main` again.
  Repeat if it advanced during synchronization.

If the live update succeeds but GitHub fails, do not roll it back automatically.
Preserve or return the complete exported bundle when possible and report **ChatGPT ahead** with the failed Git step.

## Latest main to live ChatGPT

1. Read current `main` and the allow-list.
2. Resolve the requested skill or managed skill set to canonical directories.
3. Inspect installed versions before replacing anything.
   Compare against a verified prior synchronization snapshot when available:
   an unchanged older deployment is behind Git, not an independent live edit.
4. If a live skill contains independent changes absent from Git,
   report the drift and request direction unless the user explicitly asked to discard it.
   If the origin of a difference cannot be established, do not assume it is safe to overwrite.
5. From the checked-out source commit's `chatgpt/` directory, run `bash package.sh <output-directory>`.
   Extract the selected ZIPs and use their complete skill directories as the deployment input;
   do not reimplement packaging or apply additional name transformations.
6. Validate, save, and verify through `skill-creator`,
   then compare the saved tree with the extracted package, excluding generated `manifest.txt`.
7. Report the exact source commit and any remaining differences.

Handle multiple skills independently so one failure does not obscure the others.

## Status and reconciliation

For a status request, compare current `main` with the live skill after normalizing only documented name transformations and generated-file exclusions.
Report skill, Git commit, state, and material differences without reconciling or saving either side.
A difference alone does not establish which side changed; report uncertainty when no verified baseline is available.

For live-to-Git updates when both sides changed, preserve both versions and combine non-conflicting edits in the PR;
ask which behavior should win for genuine conflicts.
For Git-to-live updates, follow the drift safeguard above.
If the user chooses to retain live changes, record them through the live-to-Git workflow before deploying the merged result.
Never use timestamps alone to choose a winner.

## Git consistency

For live-to-Git updates, record the verified live changes after reversing documented packaging transformations;
preserve repository-only content as described above.
A deployment is synchronized only when its complete saved tree matches the packaged Git source, excluding generated `manifest.txt`.
Do not describe an unmerged branch as already present on `main`.

## Completion

Report each affected skill with one of these states:

- **Synchronized** — verified equivalent in ChatGPT and the reported `main` commit.
- **Awaiting merge** — the intended change is in an open PR, not yet on `main`; state whether it is deployed.
- **ChatGPT ahead** — live update succeeded, but Git recording is pending or failed.
- **Git ahead** — Git contains the intended version, but the live update is pending or failed.
- **Conflict** — user direction is required to resolve or discard differences.
- **Diverged** — verified differences are reported without reconciliation, as requested.
- **Unchanged** — no change was needed; verified equivalent.

If comparison or verification is incomplete, report that limitation instead of asserting one of these states.

Include the Git commit or pull-request link when one exists
and state whether a new ChatGPT conversation or reload is needed for a deployed maintainer update to take effect. For PR-only changes, state that the installed skill was not changed; do not imply a reload will apply the unmerged proposal.
