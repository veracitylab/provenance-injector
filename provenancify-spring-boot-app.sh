#!/bin/sh

if [ $# = 0 ]; then
  echo "Must specify the app jar." >&2
  exit 1
fi

APPJARPATH=$(realpath "$1")
PROVAGENTJARPATH=$(realpath target/provenance-agent.jar)
EXPLODEDIR=$(mktemp -d)

echo "APPJARPATH=$APPJARPATH"
echo "PROVAGENTJARPATH=$PROVAGENTJARPATH"
echo "EXPLODEDIR=$EXPLODEDIR"

# Explode the provenance agent jar
(
  cd "$EXPLODEDIR" &&
  jar xf "$PROVAGENTJARPATH" &&
  rm -f META-INF/MANIFEST.MF &&
  jar xf "$APPJARPATH" META-INF/MANIFEST.MF &&    # jar ...M should make this step unnecessary, but it doesn't
  jar ufM "$APPJARPATH" * &&
  (
    echo "Can-Redefine-Classes: true" &&
    echo "Can-Retransform-Classes: true" &&
    echo "Launcher-Agent-Class: nz.ac.wgtn.veracity.provenance.injector.instrument" &&
    echo " ation.ProvenanceAgent"
  ) > extra-manifest-entries &&
  jar ufm "$APPJARPATH" extra-manifest-entries
) &&
rm -r "$EXPLODEDIR" &&
echo DONE
