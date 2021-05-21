#!/usr/bin/env bash

BUILD=replaced_with_build_dir

JARS=$(find $BUILD/project-deps/ -name "*.jar" | tr '\n' ':')

AGENTLIB=

if [[ "$(java -version 2>&1)" =~ "Graal" ]]
then
    AGENTLIB=-agentlib:native-image-agent=config-output-dir=config-dir
fi

java -ea $AGENTLIB -cp $BUILD/classes:$JARS com.viklauverk.eventbtools.Main "$@"
