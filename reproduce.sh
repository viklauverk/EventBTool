#!/bin/sh

if [ ! -f ./build/project-deps/jline-3.22.0.jar ]
then
    mkdir -p build/project-deps
    echo "Installing jars"
    for i in jline-3.22.0.jar antlr4-runtime-4.13.1.jar jna-5.13.0.jar dom4j-2.1.3.jar antlr4-4.13.1.jar slf4j-api-2.0.6.jar jansi-native-1.8.jar plexus-build-api-0.0.7.jar slf4j-simple-2.0.6.jar antlr4-maven-plugin-4.13.1.jar hawtjni-runtime-1.16.jar jaxen-1.2.0.jar ST4-4.3.4.jar antlr-runtime-3.5.3.jar plexus-compiler-api-2.12.1.jar plexus-utils-3.4.2.jar commons-lang3-3.12.0.jar org.abego.treelayout.core-1.0.3.jar icu4j-72.1.jar
    do
        cp $(find ~/.m2 -name $i) build/project-deps
        echo "Copied $i"
    done
fi

java -ea -cp ./build/classes:./build/project-deps/jline-3.22.0.jar:./build/project-deps/antlr4-runtime-4.13.1.jar:./build/project-deps/jna-5.13.0.jar:./build/project-deps/dom4j-2.1.3.jar:./build/project-deps/antlr4-4.13.1.jar:./build/project-deps/slf4j-api-2.0.6.jar:./build/project-deps/jansi-native-1.8.jar:./build/project-deps/plexus-build-api-0.0.7.jar:./build/project-deps/slf4j-simple-2.0.6.jar:./build/project-deps/antlr4-maven-plugin-4.13.1.jar:./build/project-deps/hawtjni-runtime-1.16.jar:./build/project-deps/jaxen-1.2.0.jar:./build/project-deps/ST4-4.3.4.jar:./build/project-deps/antlr-runtime-3.5.3.jar:./build/project-deps/plexus-compiler-api-2.12.1.jar:./build/project-deps/plexus-utils-3.4.2.jar:./build/project-deps/commons-lang3-3.12.0.jar:./build/project-deps/org.abego.treelayout.core-1.0.3.jar:./build/project-deps/icu4j-72.1.jar: com.viklauverk.eventbtools.Main "$@"
