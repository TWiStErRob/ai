---
name: notion-ingest-content
description: |
  Add URLs to Notion Contents / Reading List / Bookmarks database.
  Research titles, dates, types, Topics, and Authors;
  show a readable review table; prevent duplicates;
  then prefer one consolidated verified write.
---

# Notion Content Ingest

Use this skill when the user asks to add one or more URLs to their Notion **Contents / Reading List / Bookmarks** database.

## Scope

Create only the requested content records. For each URL:

- Extract the real page title from HTML.
- Infer an exact publication/session date when reliably available.
- Set the most appropriate existing `Type` option.
- Link relevant `Topics` and `Speaker(s)`/Authors only when those records already exist.
- Never create Topic or Author records.
- Never create duplicate Contents records.
- Do not set or modify unrelated properties.

## Stable Notion targets

Use these cached identifiers to avoid workspace-wide discovery calls:

- Contents data source: `collection://f828684e-63ab-4552-885a-ed8b0ffaa6c0`
- Contents database page: `d6d80a47-65fe-470d-bae0-6fd5cd3d3f41`
- Topics data source: `collection://7fdc83ab-d28c-49ee-8a43-e53c20e95d43`
- Authors data source: `collection://1cd38976-a4cf-4a69-a231-642154e6bc12`

Fetch the Contents database/data source once at the start of a run, because the Notion connector requires the current schema before writes. If the fetch fails or the schema differs from the expected properties below, stop and report the mismatch rather than altering the schema.

Expected writable properties:

- `Name` — title
- `Online Content` — URL
- `Date` — date
- `Type` — select
- `Topics` — relation to Topics
- `Speaker(s)` — relation to Authors

Do not set `State`, `Abstract`, `Rating`, `Related`, `Event`, `Slides`, `Track`, or other properties unless the user explicitly requests them.

## Workflow optimized for one approval

The user's preferred interaction model is: perform all safe read-only research and planning first, show the resolved results in a compact human-readable table, then request approval for **one consolidated Notion write call**. Minimize approval-gated calls rather than optimizing primarily for payload size. Do not split a valid create operation pre-emptively merely because several pages are involved. The user should review the table, not raw tool-call JSON.

Use additional write calls only when:

- the connector rejects or truncates the consolidated payload,
- the payload exceeds a documented tool limit,
- a previous write has an ambiguous outcome and idempotent recovery requires missing-only retries, or
- the user explicitly requests staged batches.

When splitting becomes necessary, explain the reason once and use the fewest additional calls possible.

## Execution workflow

### 1. Parse and normalize the request locally

Preserve the exact submitted URL for `Online Content`, except remove obvious accidental whitespace.

For duplicate comparison, generate normalized variants locally:

- exact URL
- URL without `#fragment`
- URL without a trailing slash
- URL without common tracking parameters (`utm_*`, `fbclid`, `gclid`)
- canonical URL from fetched HTML, when present

Treat variants as equivalent for duplicate detection, but store the submitted URL unless a redirect/canonical URL clearly identifies the same page and the user requested canonicalization.

### 2. Complete metadata research before requesting write approval

Fetch source metadata in as few batched web requests as practical. Finish title/date/type/topic/author resolution for the full input set before issuing any Notion write. Read-only work should not be interleaved with several incremental create calls.

When possible, fetch metadata in one batched web request.

Open all source URLs together. For each page, determine:

#### Title priority

1. Clear article/session title in the document (`<h1>` or structured article heading).
2. `og:title` or `twitter:title`.
3. HTML `<title>` — always check this when other extraction is absent or suspicious.
4. Structured data (`headline`, `name`) when it clearly represents the page.
5. A title explicitly supplied or approved by the user.

Strip only obvious site suffixes such as ` | Site Name` or ` - Site Name`. Preserve punctuation, Unicode, capitalization, currency symbols, and product names. Do not synthesize a title from the URL unless the user approves it.

#### Date priority

Use the first reliable exact date:

1. JSON-LD `datePublished` or equivalent structured publication date.
2. `article:published_time` or other publication metadata.
3. `<time datetime="...">` associated with the article/session.
4. A clearly labelled publication date in the page content.
5. Date encoded in the URL path, when it is consistent with the page.
6. A date explicitly supplied or approved by the user.

Do not invent a day from a month/year or year-only value. Leave `Date` unset when only a partial date is known or sources conflict.

For Reddit, prefer the post's actual creation date, not the fetch date or a relative-age label.

### 3. Classify `Type`

Use only an option already present in the Contents schema:

- `Blog post` — articles, essays, news posts, ordinary Reddit discussions
- `Listicle` — explicitly numbered/list-based articles or tip collections
- `Guide / Tutorial` — procedural walkthroughs, workflows, technical tutorials
- `Podcast` — podcast episodes
- `Training` — courses and learning paths
- `Documentation` — reference/product documentation
- `Talk` — recorded conference/session videos
- `Presentation / Slides` — slide decks or presentation pages primarily providing slides
- `Demo` — product/app demonstration pages when that is the page's main purpose
- `Book` — books
- `Scientific Paper` — research papers
- `Report / Brochure` — formal reports, white papers, brochures
- `Reference / Cheat Sheet` — concise reference material
- `Design document` — design/specification documents
- Other schema options only when clearly applicable

If two types are genuinely plausible and the user has not indicated a preference, include the uncertainty in a preview and ask before writing. Do not create new select options.

### 4. Check duplicates in one Notion query

Before any creation, issue one parameterized query against Contents using all exact and normalized URL variants. Return at least:

- Notion page URL
- `Name`
- `Online Content`

Skip any requested item matching an existing normalized URL. Do not modify the existing record unless explicitly requested.

Repeat a compact duplicate query immediately before retrying any failed or ambiguous batch.

### 5. Resolve Topics and Authors using candidate queries

Infer a small set of candidate topic names from the page content, including obvious canonical variants such as abbreviations and expanded names.
Query all candidates in one parameterized Topics SQL call.
Link only exact existing records returned by the query; the Topics database is authoritative.

Extract author/speaker names from bylines or structured metadata. Query all names in one Authors SQL call. Use exact name matches only; tolerate ordinary Unicode/case normalization but do not match different people based only on a domain or organisation. Leave `Speaker(s)` unset when no exact existing Author record is found.

Never create missing Topics or Authors.

### 6. Always show a research review table before writing

After metadata research, duplicate detection, and relation resolution are complete, show a compact table and wait for the user's approval before issuing any Notion write call:

| Status | Title | Date | Type | Existing Topics | Existing Author | URL | Note |

Table rules:

- Include every submitted URL in original order.
- Use `Create` for new records and `Skip — duplicate` for existing ones.
- Use `—` for properties that will remain unset.
- Show only existing Topic and Author names that will actually be linked.
- Put unresolved or inferred details in `Note`, including partial dates, inaccessible pages, redirects, or classification uncertainty.
- Keep cells concise and readable; do not expose relation page IDs, SQL, or create-call JSON.
- When the list is large, use one table rather than several unless the UI makes a single table unreadable.

End the preview with a one-line summary such as: `Ready to create 8 records; 2 duplicates will be skipped.` Then wait for explicit approval.

The approval applies to the exact reviewed rows. If later research materially changes a title, date, type, Topic, Author, URL, or create/skip status, show the changed rows again before writing.

### 7. Prefer one consolidated atomic write

After the user approves the review table, create only non-duplicates. Put all ready records into **one `create-pages` call** so the user normally approves one write action. The connector supports up to 100 pages per call; stay within that documented limit.

Do not split the request just because relation-rich payloads failed in earlier sessions. First attempt the single consolidated write with compact properties and no page content. If that call is rejected or its result is ambiguous, follow the idempotent recovery rules below and split only as much as necessary.

For each page set only:

- `Name`
- `Online Content`
- `Type`
- `date:Date:start` and `date:Date:is_datetime = 0`, when an exact date exists
- `Topics`, when confirmed existing relations exist
- `Speaker(s)`, when confirmed existing relations exist

Relations must be JSON arrays of full Notion page URLs encoded in the property's SQLite string value, for example:

```json
"Topics": "[\"https://app.notion.com/<topic-page-id>\"]"
```

Do not include empty relation arrays; omit the property.

### 8. Handle write failures idempotently

- A validation rejection before persistence normally means nothing was created, but confirm with a duplicate query before retrying.
- If the result is ambiguous, timed out, truncated, or may be partial, query every URL from that batch before retrying.
- Retry only missing records.
- If a relation payload causes failure, first verify that nothing was created, then retry using the fewest larger sub-batches likely to succeed; do not silently drop confirmed relations unless the user approves.
- Never claim success based solely on submitting a create request. Require returned created pages or a verification query.

### 9. Report only the requested outcome

After successful creation, state:

- number created
- number skipped as duplicates
- whether any dates were left unset
- whether Authors/Topics were omitted because exact existing records were absent

Do not add recommendations, summaries of the articles, database changes, comments, icons, covers, or other content unless requested.

## Hard constraints

- DO NOT create duplicate Contents.
- DO NOT create Topics or Authors.
- DO NOT add new Type options.
- DO NOT guess exact dates.
- DO NOT invent titles without explicit approval.
- DO NOT alter existing records unless explicitly requested.
- DO NOT modify the database schema or views.
- DO NOT perform unrelated Notion actions.
- DO NOT create incrementally when one consolidated write can complete the request.
- DO NOT issue a Notion write before showing the resolved review table and receiving approval.
- DO NOT spend extra approval-gated calls merely to make internal batching more convenient.
