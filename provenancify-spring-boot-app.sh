#!/bin/sh

if [ $# = 0 ]; then
  echo "Must specify the app jar." >&2
  exit 1
fi

APPJARPATH=$(realpath "$1")
PROVAGENTJARPATH=$(realpath target/provenance-agent.jar)
EXPLODEDIR=$(mktemp -d)

echo

# Explode the provenance agent jar
( cd "$EXPLODEDIR" && jar xf "$PROVAGENTJARPATH" && jar uf "$APPJARPATH" * )

#rm -r "$EXPLODEDIR"
