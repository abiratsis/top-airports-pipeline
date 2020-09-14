#!/bin/sh
java=java
if test -n "$JAVA_HOME"; then
    java="$JAVA_HOME/bin/java"
fi
exec "$java" -cp tapipe.jar com.abiratsis.airport.pipeline.Main "$@"
exit 1