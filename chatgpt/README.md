# ChatGPT web

`chatgpt/ai-setup.yml` is the allow-list for skills managed in ChatGPT web.
Each `skills/<name>` link points at the canonical self-contained skill directory.
Packaging writes them to the ignored `chatgpt/dist/` directory.
The source skill contents are preserved as-is except when a link renames a skill;
then the packaged `SKILL.md` frontmatter name is changed to match its exported directory.
Exported `manifest.txt` is neither tracked nor generated.

## Deployment

### Current

CI automates packaging and publishes the resulting ZIPs as workflow artifacts or release attachments,
but it does not install them into ChatGPT. Personal-skill uploads do not have a supported API at the
moment; they can only be done through ChatGPT web's internal authenticated skill-management flow.
That flow relies on an unsupported private interface, so we cannot use it for CI deployment.

Download the artifacts and upload the desired ZIPs manually under ChatGPT > Plugins > Skills.

### Future

The documented [Skills API](https://developers.openai.com/api/reference/python/resources/skills/methods/list)
is scoped to an API project rather than this personal ChatGPT installation. Revisit deployment automation
if a supported personal-skill interface becomes available. Keep any future remote API adapter isolated
from packaging, and publish artifacts before attempting deployment so failures leave inspectable output.

## Agent Plugins

[Agent Plugins](https://learn.chatgpt.com/docs/build-plugins) can bundle one or more skills
and optionally an MCP server. ChatGPT recognizes such archives and discovers their contained skills,
but its personal-skill replacement flow cannot force-update a multi-skill upload when any contained
skill already exists. This repository therefore packages each allow-listed skill as a standalone ZIP
so it can be created or replaced independently.
