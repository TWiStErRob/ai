---
name: notion-watchlist-ingest
description: |
  Save or log films, series, and cinema screenings in the user's Notion Watchlist
  from titles, links, screenshots, or booking emails such as Cineworld tickets.
  Research the exact item, preserve Watchlist conventions,
  and update an existing unwatched entry when appropriate.
  Use for watchlist and watched-film requests;
  use Go-list ingest for live events and Contents ingest for articles or learning resources.
---

# Notion Watchlist Ingest

Record the requested viewing idea, booking, or completed viewing in the existing Watchlist. Follow the user's current instructions over cached conventions.

## Workspace targets

- Watchlist database: `bc001170-0a2c-423c-8eab-7ec43d1e251f`.
- Watchlist data source: `collection://582a305b-9de9-491d-9bdf-2e451949dcab`.
- People data source for `who`: `collection://e51b5cb4-3f83-4ceb-af79-0727aaa45309`.
- User person page: `https://app.notion.com/p/851c86ea26cb461db182901e558f70fd`.

Fetch the current Watchlist schema before writing. Treat cached field names and options below as hints, not permission to change the schema. Before content search, fetch Notion `self` and choose the search tool according to current access. Prefer focused queries; SQL can have a quota. View mode is an alternative for browsing existing views.

## Research and duplicate handling

1. Identify the exact work and, when applicable, the screening. Use title, year, edition, cinema and session date to distinguish remakes, sequels, rereleases and repeat visits.
2. Read the source itself. When the request refers to an email, locate the matching message using the cinema sender and film-title variants. Determine whether it is a booking confirmation, cancellation, marketing, or feedback message before extracting booking details. Do not alter mailbox state.
3. Inspect a few comparable Watchlist rows when the relevant convention is not established. Fetch a representative page to distinguish properties from page-body content. Existing cinema records normally have blank bodies, no icons or covers, and personal metadata in properties.
4. Query duplicates independently of a limited recent-row sample. Search normalized title variants and identifiers/URLs, then compare screening dates and locations. The same film on a different date can be a legitimate repeat viewing.
5. If exactly one matching unwatched, undated idea exists and the request is to log that film's booking or viewing, fetch it and fill its relevant fields. Preserve the release date, existing relations, notes and other metadata. Explain that the existing item is being completed. If the same screening is already recorded, skip or fill only requested missing details. Ask only when multiple candidates or conflicting viewing histories prevent a clear choice.
6. Research and populate IMDb, Release Date and Trailer for film imports, including ticket imports. Prefer the UK theatrical release date, falling back to the US theatrical release date only when a UK date cannot be established. Prefer current official film/distributor/cinema sources over older announcements. Get the trailer URL linked or embedded on the exact Cineworld film page. Email remains authoritative for the user's booking details. If a metadata field cannot be verified, leave it unset and report the gap rather than guessing.
7. Show a compact preview of create/update/skip, title, local date/time, venue, ticket/price and meaningful uncertainty. A direct request to save or log the item authorizes the corresponding write; do not impose a second confirmation unless a material ambiguity remains or the user requested review first.
8. Consolidate new pages into one create call when practical. Use property-only updates for existing rows. Fetch the result and verify the stored values, including timezone conversion and preserved fields.

## Property conventions

| Property | Mapping |
|---|---|
| `Name` | Concise film/programme title, preserving meaningful punctuation. Add year or edition only to disambiguate or when part of the title. |
| `Type` | Existing option: Movie, Short, Series, Subscription, Performer, Needs Triage, Podcast. A cinema presentation of a series can remain Series. |
| `Watched` | False for ideas and future bookings. True for user-confirmed viewing; a matching post-visit message may support an explicitly disclosed inference. A past booking or expired date alone does not prove attendance. Preserve an existing true value unless correcting it is requested. |
| `Date` | The user's viewing/session date, with advertised booking start when supplied. Never the email's sent time or release date. |
| `Release Date` | Verified UK theatrical release date, falling back to US when UK is unavailable. Research this for film imports; correct an existing US date when a verified UK date differs. Prefer general release over festival premieres. Disclose US fallback. Do not manufacture a day from a partial date. |
| `Location` | Exact existing venue/platform option. Examples: Cineworld: London Leicester Square, Home, Online: Netflix. If a branch is absent, use the broader Cineworld option when appropriate and disclose the limitation. |
| `Room` | Screen number as text, e.g. `7`, without a Screen prefix. |
| `Seat` | Exact seat labels, e.g. `B6`; multiple seats use `G9, G10`. Preserve user-recorded changes such as `B6→B4`. |
| `Ticket Type` | Existing multi-select options reflecting the ticket actually used. See the subscription rules below. |
| `Price (UK)` / `Price (HU)` | Actual booking charge in GBP/HUF, as a number. Use zero for an explicitly zero-cost ticket; omit unknown costs. Do not duplicate a charge across currencies. |
| `Screening Type` | Existing multi-select options: IMAX, 4DX, 3D, Live, Matine, Unlimited Screening, SuperScreen. Leave ordinary 2D blank; do not create a 2D option. |
| `who` | For a solo cinema record, use the User person relation. Preserve existing relations and add others only when identity and relevance are established. |
| `userDefined:URL` | Film/programme/watch page when useful; do not repurpose it as a private booking-email link. Recent cinema logs commonly leave it empty. |
| `IMDB` | Research the exact film and store its canonical `https://www.imdb.com/title/tt…/` URL. Verify title/year; omit tracking parameters and search-result URLs. |
| `Trailer` | Research the trailer linked or embedded on the exact Cineworld film page; store the direct video URL, typically YouTube, rather than the cinema page. Inspect its video link or structured media data when needed. If unavailable, report the gap; do not silently substitute a different trailer. |
| `Franchise` | Existing multi-select options: CollegeHumor, Marvel, DC, Disney. Use only supported classifications. Many existing film logs leave this empty; do not require retrospective enrichment. |
| `Reason` | The user's motivation, recommendation or originating related item, when known. Do not invent a motive from an email. |
| `Summary` | Concise premise only when requested or supported and useful; do not invent the user's assessment. |
| `Comment`, `Rating` | Preserve personal notes and ratings. Never infer a star rating from attendance, genre or promotional language. |

Do not write computed `State`, Created time, Last edited time or internal identifiers. Never add schema options or create People records as part of ingestion. Do not create Go-list entries, calendar events, reminders, purchases or messages unless separately requested.

### Dates and durations

Interpret UK screening times in `Europe/London`, including daylight saving. Write an explicit offset: 14 September 2026 at 19:50 is `2026-09-14T19:50:00+01:00` (18:50 UTC). Verify the stored instant rather than copying a displayed UTC time as local time.

Use date-only when only a day is known. For ordinary cinema screenings with a known scheduled start and verified runtime, populate the end using the user's convention: **scheduled start + 25 minutes of ads/trailers + film runtime**. Prefer runtime from the booking or exact Cineworld listing. Label the calculated finish as an estimate in the preview/report; the 25 minutes is a user convention, not a guaranteed cinema policy. A user-supplied or remembered finish takes precedence over this estimate; preserve its stated uncertainty and mention any discrepancy. For example, 19:50 + 25 + 145 minutes = 22:40; if the user recalls 22:35, store 22:35. Handle midnight rollover and local daylight saving correctly. Leave the end unset if start/runtime is unknown, and do not apply cinema ads to home/streaming viewings or special events without support.

### Cineworld ticket conventions

For the user's admission, use `Unlimited Premium (Black)` with `Price (UK)` = 0 and the User person relation when supported by the booking or current comparable records. A generic Unlimited Ticket in the booking email can map to that label when current comparable records still support it. Do not apply this membership tier to another person or infer it from a zero price alone.

Use `Unlimited Uplift` for an evidenced premium surcharge according to comparable rows. Do not classify all subscription admissions as Free or all Unlimited tickets as Unlimited Screening: the latter is a special screening format. Preserve multiple ticket labels when explicitly supported by the booking.

Extract the actual totals and amounts charged. Ignore generic boilerplate fees when the booking explicitly charged zero. Strip HTML comments, scripts and styles before extracting visible email text: Cineworld templates may contain commented-out subtitled warnings, dummy membership fields and unused prices. Do not treat these as facts. Avoid storing ticket QR codes, membership numbers, payment details or entire emails in Notion.

### API representations and recovery

Use expanded dates (`date:Date:start`, optional `date:Date:end`, `date:Date:is_datetime`) and the analogous Release Date keys. When adding or changing an end, include the fetched existing start and is_datetime in the same update; an end-only update is rejected. Use numeric prices and `__YES__` / `__NO__` checkboxes. Follow the current tool schema for multi-selects and relations; arrays of option names and existing person-page URLs are supported by the current write tools. Do not confuse a Notion workspace user ID with a People relation page.

SQL rich-text projections can lose links or mentions. Fetch the page or use rows mode before interpreting or changing rich-text fields. Leave page content blank for ordinary property-only cinema logs, matching existing entries. Read the Notion Markdown specification before authoring body content.

After ambiguous failures, query/fetch the target before retrying. Retry only missing changes; never blindly create again. Stop and report a persistent error after one corrected retry. Report the actual result with the Notion link, local screening details, and important omitted or inferred fields. Never claim creation if an existing row was updated.
