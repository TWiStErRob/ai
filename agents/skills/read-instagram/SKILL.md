---
name: read-instagram
description: >-
  Inspect user-supplied Instagram post, carousel, or reel links and extract
  relevant visible content before requesting screenshots. Use directly or
  alongside another skill that needs an Instagram source read; this skill
  retrieves evidence and does not save records or modify Instagram.
---

# Read Instagram

Read the supplied source before asking the user to reproduce it. Keep source retrieval separate from any downstream task, such as saving an event, article or film to Notion.

## Workflow

1. Preserve the supplied URL and any requested carousel slide (`img_index`). Try web retrieval and a focused search using the post identifier or available caption details. Distinguish a tool failure such as `DisabledError` from an observed restriction on the site; neither a failed fetch nor an empty search proves that every access route is blocked.
2. When a successful search does not expose the necessary content and browser use is permitted, open the supplied Instagram URL in the available browser. Use the current browser instructions and prefer a sufficient service plugin when available. Do not treat a search/retrieval error as authorization to use a browser route prohibited by those instructions.
3. Dismiss cookie and optional sign-up prompts when possible without signing in. An optional sign-up prompt with a working close button is not a login wall. If authentication is actually required, follow the browser's authentication guidance; never assume access to the user's phone app or an existing signed-in session.
4. Inspect the caption and relevant slides. Verify the selected slide visually rather than assuming the query parameter was honoured. Use screenshots or other permitted visual inspection to read poster text missing from the accessibility tree. Navigate the carousel with its visible controls when needed. For reels, extract only what the available tools can actually observe; do not claim to have heard audio or read a transcript that was not available.
5. Extract the exact identity and task-relevant facts, such as names, dates, venue, links or booking instructions. Separate visible facts from inference, flag missing years or ambiguous dates, and corroborate details with official sources when the downstream task requires it. Do not infer the event year from the post date alone.
6. If access remains blocked after the permitted attempts, describe the specific observed blocker and routes actually tried. Preserve useful partial evidence. Only then ask for the minimum missing material, such as the relevant slide or caption. Do not claim that Instagram is generally inaccessible. Respect access controls and the browser's recovery limits; do not bypass bot checks, private-account restrictions or login requirements.

## Handoff

Return the supplied source URL, creator when verified, relevant extracted facts and meaningful uncertainties to the calling workflow. Identify whether evidence came from search, the caption, a visible slide or another source. Keep the Instagram discovery link even when an official destination supplies the canonical URL.

Leave classification, duplicate checks, database writes, uploads, ticket handling and attendance decisions to the calling skill. Do not like, follow, comment, message or save on Instagram as part of this read-only workflow.
