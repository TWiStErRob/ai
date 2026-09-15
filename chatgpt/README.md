# ChatGPT web

`chatgpt/ai-setup.yml` is the allow-list for skills managed in ChatGPT web.
Each `skills/<name>` link points at the canonical self-contained skill directory.
Packaging writes them to the ignored `chatgpt/dist/` directory.
The source skill contents are preserved as-is except when a link renames a skill;
then the packaged `SKILL.md` frontmatter name is changed to match its exported directory.
Exported `manifest.txt` is neither tracked nor generated.

_Agent Plugin archives may contain multiple skills,
but the current ChatGPT replacement flow rejects force-updating a multi-skill upload when any contained skill already exists,
so deployment deliberately uses one ZIP per skill._

## Deployment

The release artifacts can be downloaded from each CI run (artifacts) or published release (attachments).
These zip files are ready to be uploaded to ChatGPT > Plugins > Skills.
