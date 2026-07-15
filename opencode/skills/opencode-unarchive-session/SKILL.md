---
name: opencode-unarchive-session
description: Restore archived opencode sessions when a user accidentally archived the wrong session, wants to recover a session they thought was lost, or needs to continue a session that was auto-archived
license: MIT
compatibility: opencode
metadata:
  scope: local
---

## Overview

Opencode stores session metadata in a SQLite database at `~/.local/share/opencode/opencode.db`. Archiving a session sets its `time_archived` column; unarchiving clears it back to `NULL`.

Use this skill when:
- A session was accidentally archived and needs to be restored
- You need to find a session by title to unarchive it

## How to use

### 1. List archived sessions

Run this to see all archived sessions (id, title, and when they were archived):

```bash
sqlite3 "$HOME/.local/share/opencode/opencode.db" \
  "SELECT id, title, time_archived FROM session WHERE time_archived IS NOT NULL ORDER BY time_archived DESC;"
```

### 2. Find a session by keyword

If you know a keyword in the title:

```bash
sqlite3 "$HOME/.local/share/opencode/opencode.db" \
  "SELECT id, title, time_archived FROM session WHERE time_archived IS NOT NULL AND title LIKE '%KEYWORD%' ORDER BY time_archived DESC;"
```

### 3. Unarchive a specific session

Once you have the session ID (e.g. `ses_0aebd263cffe...`):

```bash
sqlite3 "$HOME/.local/share/opencode/opencode.db" \
  "UPDATE session SET time_archived = NULL WHERE id = 'SESSION_ID';"
```

### 4. Verify it worked

```bash
sqlite3 "$HOME/.local/share/opencode/opencode.db" \
  "SELECT id, title, time_archived FROM session WHERE id = 'SESSION_ID';"
```

A `NULL` in the `time_archived` column means the session is now active.

## Edge cases

- **Database is locked**: If opencode is running, the DB may be locked. Quit opencode first, or run the queries before starting a session.
- **Wrong session ID**: Always verify by listing first. The `id` column is the primary key — unarchiving the wrong session is easy to fix by re-archiving: `UPDATE session SET time_archived = strftime('%s','now') || '000' WHERE id = 'SESSION_ID';`
- **Path differences on Windows**: `$HOME/.local/share/opencode/` works in Git Bash; on Windows native, use the full path `C:\Users\<User>\.local\share\opencode\opencode.db`.
- **Already unarchived**: If `time_archived` is already `NULL`, the session was never archived or has already been restored.
