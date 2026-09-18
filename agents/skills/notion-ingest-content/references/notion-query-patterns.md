# Notion Query Patterns

These are templates, not literal copy-paste payloads. Substitute the current URL variants, candidate names, and fetched schema.

## Duplicate lookup

Use a single parameterized SQL query containing all exact and normalized URL variants:

```sql
SELECT url, "Name", "Online Content"
FROM "collection://f828684e-63ab-4552-885a-ed8b0ffaa6c0"
WHERE "Online Content" IN (?, ?, ...)
```

Perform additional local normalization on returned values before deciding that an item is new.

## Topic candidates

```sql
SELECT url, "Name"
FROM "collection://7fdc83ab-d28c-49ee-8a43-e53c20e95d43"
WHERE lower("Name") IN (?, ?, ...)
```

Pass lowercase candidate names. The returned page URL is the relation target.

## Author candidates

```sql
SELECT url, "Name"
FROM "collection://1cd38976-a4cf-4a69-a231-642154e6bc12"
WHERE lower("Name") IN (?, ?, ...)
```

Do not use fuzzy name matching for people without user approval.

## Post-write verification

```sql
SELECT url, "Name", "Online Content", "Type", "date:Date:start"
FROM "collection://f828684e-63ab-4552-885a-ed8b0ffaa6c0"
WHERE "Online Content" IN (?, ?, ...)
```
