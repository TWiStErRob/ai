#!/usr/bin/env bash

set -euo pipefail

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
