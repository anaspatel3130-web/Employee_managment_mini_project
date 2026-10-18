#!/bin/sh
DIR="$(cd "$(dirname "$0")" && pwd)"
if [ -f "$DIR/apache-maven-3.9.6/bin/mvn" ]; then
    exec "$DIR/apache-maven-3.9.6/bin/mvn" "$@"
else
    exec mvn "$@"
fi
