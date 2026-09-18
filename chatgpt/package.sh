#!/usr/bin/env bash
set -euo pipefail

# Package the ChatGPT skills allow-listed by ai-setup.yml.
#
# Run from the chatgpt/ directory with one output-directory argument.
# Expected input structure:
#
#   chatgpt/
#   ├── ai-setup.yml
#   └── package.sh
#   agents/skills/<source-name>/
#   ├── SKILL.md
#   ├── agents/openai.yaml        # optional
#   ├── assets/                   # optional
#   └── references/               # optional
#
# Each ai-setup.yml link maps an exported target such as skills/<name> to a self-contained source directory.
# Relative source paths are resolved from chatgpt/.
# Symlinks inside the source are dereferenced while staging.
#
# Expected output structure for each mapping:
#
#   <output-directory>/chatgpt-<name>.zip
#   └── <name>/
#       ├── SKILL.md
#       └── ...                   # remaining source contents
#
# The archive's top-level directory and SKILL.md frontmatter name use the exported <name>.
# Existing ZIPs with the same name are replaced unconditionally.
# No manifest.txt is generated inside the output zip files.

if (( $# != 1 )) || [[ -z "${1}" ]]; then
  echo "Usage: $0 <output-directory>" >&2
  exit 2
fi

dist="$(realpath -m -- "${1}")"
mkdir -p "${dist}"

staging="$(mktemp -d)"
trap 'rm -rf -- "${staging}"' EXIT

while IFS=$'\t' read -r target source_relative; do
  name="$(basename "${target}")"
  source="$(realpath -e "${source_relative}")"

  package="${staging}/${name}"
  cp --archive --dereference -- "${source}" "${package}"
  sed -i "0,/^name: .*/s//name: ${name}/" "${package}/SKILL.md"
  output="${dist}/chatgpt-${name}.zip"
  rm -f "${output}"
  # zip -X=--no-extra -r=--recurse-paths
  env --chdir="${staging}" zip -X -r "${output}" "${name}"
  rm -rf -- "${package}"
  echo "Packaged ${name}: ${output}"
done < <(yq -r '.links | to_entries[] | [.key, .value] | @tsv' ai-setup.yml)
