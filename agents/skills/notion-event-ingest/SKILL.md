---
name: notion-event-ingest
description: |
  Trigger when user asks to save/add/record/research/remember
  an event/performance/experience/show/festival/walk/trip/activity/etc.
  Use the skill to save or update a page in the user's Notion Go-list, even if they don't explicitly mention it.
  The input will usually be a user-provided link/URL/description of an event,
  or an attached poster/flyer/photo/screenshot depicting/describing an event.
---

# Go-list Event Research

Use this skill when the user gives a link and asks to add an event, experience, attraction, show, festival, exhibition, walk, trip idea, or similar item to their **Go-list**.

The goal is not merely to save the link. Research the item properly, map all supported details to the existing Go-list schema, avoid duplicates, preserve the user's existing conventions, and verify the result.

## Required tools

- Web access for researching the supplied link and official sources.
- Notion access for reading and writing the user's Go-list and Places databases.

If Notion is unavailable, do not pretend the event was added. Research the event and report that the database write could not be completed.

## Known workspace targets

These identifiers are stable hints, but always fetch the database before writing in case the schema changes.

- Go-list data source: `collection://9416746a-e6fc-4695-9731-baa1388d7652`
- Places data source: `collection://2a8bb323-2a84-4312-b49b-9ae08c2d207a`
- User person page, used for `Idea of` when the user supplies the idea:
  `https://app.notion.com/p/851c86ea26cb461db182901e558f70fd`

## Goal

Create (or update) a new page in the Go-list database, filling in all relevant properties:
- URL
- Type
- Length
- Price
- Availability
- Hours
- Area
- Place
- Location
- Idea of
- Inspired by
- Multiple
- Tickets
- Trailer
- Map / Guide

## Workflow

1. Open the supplied link and identify the exact item.
2. Research beyond the supplied page when useful.
   - Prefer primary sources: official organiser, official event site, official venue, official ticket page.
   - Treat Secret London, Time Out, social posts, blogs, aggregators, newsletters, etc. as discovery sources.
3. Establish the best current facts:
   - official name
   - event/run dates
   - opening/event/session hours
   - normal visit/performance duration
   - venue and full useful address/postcode
   - price
   - event type
   - canonical official URL
   - official trailer/promo video if clearly relevant
   - whether the item is singular or repeatable
4. If the supplied source and official source disagree, prefer the current official source and mention the discrepancy in the final response.
5. Fetch the current Go-list schema before writing.
6. Search the Go-list for likely duplicates by:
   - event name
   - canonical URL
   - supplied URL
   - obvious same-event identity
   If there's ambiguity, ask if user wants to update an existing entry instead of creating a duplicate.
7. Search the Places database for an appropriate existing Place relation.
8. Map only facts that are genuinely supported. Do not fill a field merely because it exists.
9. Create or update the Go-list page.
10. Fetch the resulting page after the write and verify the stored properties.
11. Report concisely what was stored, modified and any meaningful field intentionally left blank.

## Property conventions

### Out of scope

#### Structural relation fields
Do not invent values for:
- `Agenda`
- `Agenda Items`
- `Schedule`
- `Previous`
- `Next`
- `Related`

Only set them when the conversation or existing database structure makes the relationship explicit.

#### Read-only/computed fields

Never write formula/rollup fields such as:
- `Anytime`
- `End Time`
- `Ended`
- `Ending soon`
- `Running now`
- `Starting in (days)`
- `Starting soon`
- `State`
- `Place (Region)`

Let Notion compute them.

### What?

#### `Name`
Use the concise official event/experience name.

Include the year when:
- it is part of the official name, or
- it distinguishes an annual edition from other editions.

#### `Type`
Choose the best existing `Type` option from the current schema.

Examples seen in the database include: Festival, Theatre, Exhibition, Light show, Interactive, Trip, Cycling, Walk, Relaxation, Eating out, City break, Friends, Immersive, Concert, Open House, Shopping, Dance, Conference.

The live schema is authoritative.

Never create a new Type option as part of this workflow. If none is a reasonable fit, leave `Type` empty and say so.

#### `URL`
The Notion property may appear through tools as `userDefined:URL`.

Store the best canonical **official** event/experience page.
This could be a Meetup/Eventbrite link if no official website exists.

Use the supplied link as `URL` only when:
- it is itself official, or
- no better official canonical page can be found

#### `Price`
This is a numeric GBP property.

Rules:
- clearly free → `0`
- clear standard/headline price → numeric GBP amount
- meaningful "from" price may be used when that is how the experience is sold
- convert non-GBP price to GBP, round to nearest whole pound; price is informative only.
- When the exact price is unavailable for a multi-priced event, estimate a mid-range ticket and disclose the assumption.

Do not guess when:
- it is donation-based without a normal amount
- only membership pricing is shown

If left blank, mention useful pricing details in the final response.

#### `Status`
For a link the user simply asks to save, default to `Idea`.

Only use another status when the user has supplied facts supporting it:
- `Planning`
- `Planned`
- `Went`
- `Could not go`
- `Dismissed`

Do not infer planning status from the existence of event dates.

If users asks for Google Calendar event too, set it to Planning, and mark calendar event as Tentative.

### Where?

`Area` is a broad location for grouping. `Place` is a relation to the Places database. `Location` is the detailed venue/location text.

#### `Area`
Use existing select options only, do not create a new Area option.

Current convention:
- London event → `London`
- elsewhere in the UK → `UK`
- outside the UK → `Abroad`
- `Home` only when clearly appropriate from existing convention

#### `Place`
- Prefer the most narrow place that exists in the Place database
- Use an existing broader Place such as the city/town/region, if no narrow place exists.
- For London events, normally use the existing **London** Place while `Location` contains the exact venue/address.
- Do **not** create a Place merely because the exact venue is not already a Place.
- **Never create a new Place record without asking the user first.**
- If a genuinely new Place is required:
  1. stop before creating it;
  2. tell the user the proposed Place name and type;
  3. ask permission;
  4. continue only after approval.

Do not ask for approval if a suitable existing broader Place is already correct.

#### `Location`
Use detailed, practical location text:
- venue name
- street/address
- area (~nearest tube in London)
- postcode when available

Example style:
`Westminster Cathedral, Victoria Street, Victoria, London SW1P 1LT`

This field is deliberately more specific than `Place`.

### When?
- `Availability` is when it is possible to visit.
- `Hours` breaks down what times during the `Availability` window it's possible to go.
- `Length` describes what's the expected time spent at the venue
- `Visit` will be filled in after the user went.
- Prefer date-only `Availability` plus `Hours` for normal event schedules.
- Use datetime values for `Visit` only when the user's actual/planned visit time is known.

#### `Availability`
This is the **event/experience availability window**, not the user's personal visit.
For exhibitions/plays research when the opening night was and backdate if necessary.

Examples:
- a one-day festival → that event date
- a theatre production → first-to-last run dates
- a long-running exhibition → opening date through known closing date

Prefer date-only values for event run dates, even for a one-day event.
For one-day events, don't use a date range, just a date.
Do not put ordinary opening/session hours into `Availability` when `Hours` can represent them, except when there's actually only a single performance of something.

Date/time rules
- Preserve the event's local timezone.
- For London events, interpret local published times in `Europe/London`.
- Avoid UTC conversion mistakes that shift the calendar date.

#### `Hours`
Human-readable public opening/event/session hours.
Notice patterns and break it down by days.
For scheduled performance-like events put the starting time only, `Length` implies end time.
Include last entry if known at the end: `... Last entry 1 hour before closing.`
For weekday names use Mon/Tue/Wed/Thu/Fri/Sat/Sun/Bank (Bank is Bank Holidays)
Always use 24 hour clock. Omit :00 in time ranges, but include when it's just a starting time.

Follow the style already present in the database, for example:
- `Mon-Thu 10-19, Fri 10-21, Sat 9-21, Sun 9-19, Bank 9-20` for varying opening times
- `Wed/Thu/Fri 19:30, Sat 17:00/20:30, Sun 14:00/17:30` for varying schedule
- `10–19` for same every day
- `Wed–Sat evenings; 21:00 until mid-September, then 19:30`
- `same as venue (...)` for hosted exhibitions, e.g. in Kew Gardens

Include relevant exceptions.

#### `Length`
The normal duration of one visit, performance, session, route, or experience.

Examples of expected styles:
- `45–90 minutes`
- `1-2 hours`
- `2 hours, 15 minute interval`
- `~50 minutes; arrive 30 minutes early`
- `1 day` for festivals/conferences
- `14km, 4-5h, 400m` for a planned walk
- `12 km; moving 2:54:07; elapsed 5:16:41; elevation 311 m` for a completed walk

Prefer a source-stated duration.

Derive duration from fixed start/end times only when it is unambiguous and useful.

#### `Visit`
Only set when the user explicitly says:
- when they plan to go,
- they booked a specific performance/session,
- or when they actually went.

Use the specific visit/performance datetime range when known.

**Never copy `Availability` into `Visit` just because the event occurs on one day.**

#### `Multiple`
Interpret this as whether the entry represents a repeatable/selectable experience that materially differs on subsequent visits, not simply whether an event lasts more than one day.

Use `YES` when:
- attending multiple times would give a materially different experience, for example Standup Comedy ensemble events where the headliners change every time.
- Generic events like games ("Escape Room", "Bowling", "Activate"), sports ("Padel")

Use `NO` when:
- there are multiple selectable performances/sessions/dates for essentially the same experience
- it is just an ongoing attraction
- it is a singular event/edition, even if that event spans multiple days

If ambiguous, assume NO, and call out it end report.


### Metadata

#### `Idea of`
Who proposed to go, it could be someone who doesn't necessarily attend in the end: like a friend can suggest that the user goes.
Do not treat `Idea of` as `Attendees`.
Never create a new `People` record automatically. If one appears necessary, ask first.

#### `Inspired by`
When the user supplied a secondary discovery source such as:
- Secret London / Time Out
- Instagram / Facebook / TikTok
- another article/blog/guide

Add a link to that source in `Inspired by`.

When the user supplies photo/image of a poster:
Upload the file, if the Notion connector supports writing the property's existing file/source format.
If the connector cannot safely write `Inspired by`, do not fabricate a file value, call it out in final summary.

### `Trailer`
Add only a clearly official or directly relevant trailer/promo video.
Do not add generic reviews, unrelated clips, or random coverage videos.

### `Map / Guide`
Use only when there is a directly relevant:
- event map
- festival guide
- route map
- official guide PDF
- route/activity source fitting the existing field convention

Try to upload as image/PDF file, only link to external sources as last resort, call out if cannot upload.
Do not genearte an artificial attachment, only from official sources.

#### `Discounts`
Use an existing matching discount relation only when it clearly applies.
Never create a Discount record just to fill this property.


### Booking details

Personal/planning fields: do not infer. These fields describe the user's plans/history, not public event facts.

#### `Attendees`
Only set people the user explicitly says are attending or planned to attend.
Do not infer attendees from `Idea of`.

#### `Tickets`
Only actual user ticket files/artifacts belong here.
QR code as image file attachment or PDF. A link to external page showing actual ticket. Multiple is fine.
A public ticket-sales URL does not belong in `Tickets`.

#### `Trip`
Only relate when the conversation explicitly ties the item to an existing trip.
Do not create a new `Trip` unless explicitly asked.


## Quality checks before finishing

Confirm all of the following:

- The supplied link was actually opened/researched.
- Primary/official sources were checked where useful.
- A duplicate search was performed.
- The current Go-list schema was fetched.
- `Availability` and `Visit` were not conflated.
- `Hours` contains event/opening hours when known.
- `Location` is detailed while `Place` uses an appropriate existing broader relation.
- No new Place was created without explicit user approval.
- No new select options were invented.
- Read-only/formula fields were not written.
- `URL` is canonical/official when available.
- Secondary discovery source was preserved in `Inspired by` when feasible.
- The final page was fetched after writing and the stored properties were verified.
- After any unintended write, overwrite, update or partially incorrect creation, STOP IMMEDIATELY, and avoid any further destructive mutations and tell the user exactly what changed.

## Final response

Keep the completion response concise.

State:
- whether an entry was created or updated,
- a table of all the fields
  - their values and potential remarks
  - show original value when entry was updated
  - show new value when entry was updated or created
  - any important field left blank because the source was ambiguous or the connector could not safely represent it.

Do not claim a property was stored unless the post-write fetch confirms it.

Propose any improvements to this skill based on learnings from the conversation/research conducted.
