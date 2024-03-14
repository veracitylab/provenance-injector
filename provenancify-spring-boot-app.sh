#!/bin/sh

if [ $# = 0 ] || [ "$1" = "--help" ]; then
  echo "Usage: $0 path/to/my-spring-boot-app.war" >&2
  exit 1
fi

APPJARPATH=$(realpath "$1")
PROVAGENTJARPATH=$(realpath target/provenance-agent.jar)
EXPLODEDIR=$(mktemp -d)

echo "APPJARPATH=$APPJARPATH"
echo "PROVAGENTJARPATH=$PROVAGENTJARPATH"
echo "EXPLODEDIR=$EXPLODEDIR"

(
  cd "$EXPLODEDIR" &&
  jar xf "$PROVAGENTJARPATH" &&
  rm -f META-INF/MANIFEST.MF &&
  jar xf "$APPJARPATH" META-INF/MANIFEST.MF &&
  jar ufM "$APPJARPATH" * &&    # jar ...M should make the previous step unnecessary, but it doesn't
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
