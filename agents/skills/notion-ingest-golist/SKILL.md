---
name: notion-ingest-golist
description: |
  Trigger when user asks to save/add/record/research/remember
  an event/performance/experience/show/festival/walk/trip/activity/etc.
  Use the skill to save or update a page in the user's Notion Go-list, even if they don't explicitly mention it.
  The input will usually be a user-provided link/URL/description of an event,
  or an attached poster/flyer/photo/screenshot depicting/describing an event.
---

# Notion Go-list Ingest

Use this skill when the user asks to add or update an event, experience, attraction, show, festival, exhibition, walk, trip idea, or similar item in their **Go-list**, using a link, description, messages, photos, or other evidence.

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
- Topics
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
- Previous / Next / Related

## Workflow

1. Identify the exact item from the supplied link, description, or evidence, and inspect relevant sources.
2. Research or corroborate beyond the supplied material when useful.
   - Prefer primary sources: official organiser, official event site, official venue, official ticket page
   - For past events: use the user's relevant connected calendar, e-mail, messages, location history, tickets, receipts, statements.
   - Treat Secret London, Time Out, social posts, blogs, aggregators, newsletters, etc. as discovery sources.
3. Establish the best-supported facts:
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
   - a specific planned or completed Visit, when supported
   - any relevant trip or information source
4. Resolve discrepancies by the kind of fact: prefer current official sources for public details, and contemporaneous personal records for an individual's visit. Explain material uncertainty.
5. Fetch the current Go-list database schema before writing.
6. Search the Go-list for likely duplicates by:
   - event name
   - canonical URL
   - supplied URL
   - obvious same-event identity
   If there's ambiguity, ask if user wants to update an existing entry instead of creating a duplicate or incorrectly updating the wrong record.
7. Resolve relations:
   - Search the Places database for an appropriate existing Place relation
   - Check for a matching Trip in Holidays database where relevant,
   - Useful existing Topics pages.
   - Search for earlier/later editions and meaningfully related experiences. Fetch candidate pages to confirm their identity and current relations before linking; distinguish editions of one series from separate seasonal or similarly named events.
8. Map only facts that are genuinely supported. Do not fill a field merely because it exists.
9. Create, or update the Go-list page while preserving unrelated existing details.
10. Fetch the resulting page after the write and verify the stored properties.
11. Report concisely what was stored, modified and any meaningful field intentionally left blank.

## Property conventions

### Out of scope

#### Structural relation fields
Do not invent values for:
- `Agenda`
- `Agenda Items`
- `Schedule`

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

#### `Topics`
Connect the entry to existing interests or knowledge hubs when useful beyond `Type`.
Leave this relation empty when Type provides enough classification; not every event needs a Topic.

- Read the relation target from the Go-list schema and reuse the current Topic catalogue.
- Classify the defining activity or subject. Do not infer Topics from incidental movement, companions, venue, or organiser.
- Add multiple Topics when each describes a substantial aspect of the entry.
  Do not automatically add broad Topics such as Health to physical activities or Spirituality to yoga.
- Preserve existing relations and add supported matches without duplicates.
  Do not create Topics automatically; propose a genuinely useful gap and obtain approval.

Apply these distinctions using the live vocabulary:
- `Games`: board games, quizzes, scavenger hunts, escape rooms, VR games and physical challenge games.
  Physical exertion alone does not make an activity a sport; open-ended sensory interaction alone does not make it a game.
- `Comedy`: stand-up and comic improvisation, including entries typed Theatre or Festival.
- `Social`: gatherings centred on conversation and companionship, such as picnics and dinners designed to meet new people.
  Dedicated game or sport sessions do not acquire Social merely because people attend together.
- `Dating`: explicit dating events and dating-related talks; do not infer it for every outing with a date.
- `Travel`: trips and destination exploration; do not propagate it to every attraction or restaurant within a trip.

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

Do not infer planning status from event dates.
Use `Went` when the user reports participation,
not merely because a booking was made (`Planned`).

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
Do not label information source in this field, only physical location is relevant.

### When?
- `Availability` is when it is possible to visit.
- `Hours` breaks down what times during the `Availability` window it's possible to go.
- `Length` describes what's the expected time spent at the venue
- `Visit` records a specific planned or actual attendance, not public event availability.
- Prefer date-only `Availability` plus `Hours` for normal event schedules.
- Use datetime values for `Visit` only when the user's actual/planned visit time is known.

#### `Availability`
This is the **event/experience availability window**, not the user's personal visit.
Don't assume an experience was available only on the user's visit date.
Research its availability separately; leave it blank if unknown.
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
Do not extrapolate public hours from a personal booking.
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

Use a specific visit/performance datetime range when known; otherwise use date-only.
Corroborate via relevant connected sources when available, especially on request.

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

#### `Previous` / `Next`
Use these directional relations for successive editions of the same event or experience series.

- Link each edition to the nearest confirmed existing earlier/later edition, in chronological order. Set the reciprocal relation too: A.Next = B and B.Previous = A. Inspect whether Notion maintains the reciprocal side automatically before writing it again.
- Keep different seasonal programmes or formats in separate chains, even at the same venue or with the same organiser. A shared theme or perceived spiritual succession alone does not establish series continuity; use `Related` instead. Follow an explicit user choice to group them differently.
- Do not fabricate missing editions or create placeholders for cancelled or unannounced editions merely to complete a chain. Research and create an absent historical edition only within the user's requested scope; do not copy another edition's dates, hours, or attendance.
- Preserve unrelated links. When inserting an edition into an existing chain, replace only the superseded adjacency so the sequence stays consistent.

#### `Related`
Use this non-directional relation for a meaningful connection between distinct experiences, including separate seasonal series or a thematic successor whose continuity is unconfirmed.

- Link the relevant entries across series without merging their `Previous`/`Next` chains. Do not link every edition indiscriminately or infer a relationship solely from a shared venue or organiser.
- Preserve existing unrelated relations, avoid duplicates and self-links, and verify both sides of the connection, allowing for Notion's reciprocal updates.
- When correcting edition links stored in `Related`, establish and verify the appropriate `Previous`/`Next` links first, then remove only the migrated links from `Related`. Retain genuine cross-series connections there.

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
Only relate when the conversation or reliable records link the experience to an existing trip.
Verify the dates and itinerary, so another journey to the same place is not confused with it.
Do not create a new `Trip` unless explicitly asked.


## Quality checks before finishing

Confirm all of the following:

- Relevant supplied links, descriptions and evidence were inspected.
- Suitable public or personal sources were checked where useful.
- A duplicate search was performed.
- Edition identity, chronological adjacency and reciprocal `Previous`/`Next` links were verified; distinct series use `Related`, and unrelated relations were preserved.
- The current Go-list schema was fetched.
- `Availability` and `Visit` were not conflated; dates and course ranges were reconciled with their sources.
- `Hours` contains event/opening hours when known.
- `Location` is as precise as the evidence permits, while `Place` uses an existing broader relation.
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
