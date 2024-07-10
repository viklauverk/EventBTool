# Copyright (C) 2021-2024 Viklauverk AB
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

ifeq ($(wildcard build/spec.mk),)
   $(error Please run configure!)
endif

include build/spec.mk

# Create a version number based on the latest git tag.
COMMIT_HASH?=$(shell git log --pretty=format:'%H' -n 1)
TAG?=$(shell git describe --tags)
BRANCH?=$(shell git rev-parse --abbrev-ref HEAD)
CHANGES?=$(shell git status -s | grep -v '?? ')

# Prefix with any development branch.
ifeq ($(BRANCH),main)
  BRANCH:=
else
  BRANCH:=$(BRANCH)_
endif

# The version is the git tag or tag-N-hash if there are N commits after the tag.
VERSION:=$(BRANCH)$(TAG)

ifneq ($(strip $(CHANGES)),)
  # There are local non-committed changes! Add this to the version string as well!
  VERSION:=$(VERSION) with local changes
  COMMIT_HASH:=$(COMMIT_HASH) with local changes
endif

VERSION_FILE:=$(GEN_SRC)/version/com/viklauverk/eventbtools/core/Version.java
$(shell mkdir -p $(dir $(VERSION_FILE)))
$(shell echo "package com.viklauverk.eventbtools.core;" > $(VERSION_FILE))
$(shell echo "public class Version {" >> $(VERSION_FILE))
$(shell echo "    public static String version = \"$(VERSION)\"; " >> $(VERSION_FILE))
$(shell echo "    public static String commit = \"$(COMMIT_HASH)\"; " >> $(VERSION_FILE))
$(shell echo "}" >> $(VERSION_FILE))

$(info Building $(VERSION))

AT=@
DROP_ROOT=$(subst $(GIT_ROOT)/,./,$1)

# You have to perform the mvn build first to have the project deps installed.
mvn: $(BUILD_MVN_BIN)/evbt
	@rm -f evbt
	@ln -s $(BUILD_MVN_BIN)/evbt
	@echo Use ./evbt

# Now you can perform a javac compile with a slightly quicker rebuild time.
# It cannot rebuild the generated classes from antlr, so if you change the grammar
# then you have to rerun "make mvn"
javac: $(BUILD_JAVAC_BIN)/evbt
	@rm -f evbt
	@ln -s $(BUILD_JAVAC_BIN)/evbt
	@echo Use ./evbt

# Or a native compile, with a much much longer rebuild time.
# Do this when you have a build that passes the test suite.
graal: $(BUILD_GRAAL_BIN)/evbt
	@rm -f evbt
	@ln -s $(BUILD_GRAAL_BIN)/evbt
	@echo Use ./evbt

# Make sure the output directories exist.
$(shell mkdir -p $(BUILD) $(PROJECT_DEPS) $(GEN_ANTLR4))

# Locate the jar dependencies automatically downloaded by maven.
JARS:=$(shell find $(PROJECT_DEPS)/ -name "*.jar" | tr '\n' ':')
# Find all java sources.
SOURCES:=$(shell find src/main/java/ -type f -name "*.java")
# Find all antlr generated sources.
ANTLR_SOURCES:=$(shell find $(GEN_ANTLR4) -type f -name "*.java")
# Find the version source.
VERSION_SOURCES:=$(shell find $(GEN_SRC)/version -type f -name "*.java")
# Templates contains tex templates for document generation included from the templates directory.
TEMPLATES_JAVA:=src/main/java/com/viklauverk/eventbtools/core/Templates.java

pom.xml: pom.xmq
	xmq pom.xmq to-xml > $@

$(BUILD_MVN_BIN)/evbt: pom.xml scripts/run.sh $(TEMPLATES_JAVA) $(PROJECT_DEPS)/updated.timestamp $(SOURCES) $(ANTLR_SOURCES) $(VERSION_SOURCES) $(LOGMODULES_SOURCE)
	@echo Compiling using maven
	@mkdir -p $(BUILD_MVN_BIN) $(GEN_ANTLR4)
	$(AT)mvn -B -q package
	$(AT)cat scripts/run.sh $(BUILD)/EventBTool-1.0-SNAPSHOT.jar > $@
	@chmod a+x $@
	@echo Generated $(call DROP_ROOT,$@)

$(BUILD_JAVAC_BIN)/evbt: scripts/evbt.sh $(TEMPLATES_JAVA) $(PROJECT_DEPS)/updated.timestamp $(SOURCES) $(ANTLR_SOURCES) $(VERSION_SOURCES) $(LOGMODULES_SOURCE)
	@echo Compiling using javac
	@mkdir -p $(BUILD_JAVAC_BIN)
	$(AT)javac -cp $(JARS) -Xlint:all,-this-escape -d $(BUILD)/classes -sourcepath src/main/java:$(BUILD)/generated-sources/antlr4:$(BUILD)/generated-sources/version $(filter %.java,$?) $(SOURCES)
	$(AT)sed 's|replaced_with_build_dir|$(BUILD)|' < scripts/evbt.sh > $@
	$(AT)chmod a+x $@
	@echo Generated $(call DROP_ROOT,$@)

# To trace neeed resources for runtime, use -agentlib:native-image-agent=config-output-dir=myrun/config-dir/
# Then compare the contents of myrun/config-dir with make/native-image-config-dir
#--no-fallback
$(BUILD_GRAAL_BIN)/evbt: mvn
	@echo Compiling using graal
	@if [ "$$(java -version 2>&1 | grep -m 1 -o Graal)" != "Graal" ]; then \
           echo You have to use the Graal JVM to generate native code!; exit 1; fi
	@mkdir -p $(BUILD_GRAAL_BIN)
# --verbose --allow-incomplete-classpath --link-at-build-time
	$(AT)native-image --install-exit-handlers -ea --no-fallback --initialize-at-build-time=org.dom4j.dom.DOMDocument --initialize-at-build-time=org.dom4j.dom.DOMDocumentFactory -H:TraceClassInitialization=true -H:ResourceConfigurationFiles=make/native-image-config-dir/resource-config.json -H:+ReportExceptionStackTraces -cp $(BUILD)/classes:$(JARS) com.viklauverk.eventbtools.Main $@
	@echo Generated $(call DROP_ROOT,$@)

# The mvn tree command generates lines like this:
# [INFO] \- org.jsoup:jsoup:jar:1.11.3:compile
# from this info build the path:
# ~/.m2/repository/org/jsoup/jsoup/1.11.3/jsoup-1.11.3.jar
$(PROJECT_DEPS)/updated.timestamp: pom.xml
	@rm -rf $(PROJECT_DEPS)
	@mkdir -p $(PROJECT_DEPS)
	@echo Downloading dependencies into .m2 ...
	@mvn -q dependency:go-offline
	@echo Initializeing...
	@mvn -q initialize
	@echo Storing java dependencies into $(PROJECT_DEPS)
	@DEPS=`mvn dependency:tree | grep INFO | grep compile | grep -oE '[^ ]+$$'` ; \
    sleep 2 ; \
    for DEP in $$DEPS ; do \
        GROU=$$(echo $$DEP | cut -f 1 -d ':' | sed 's|\.|/|g') ; \
        ARTI=$$(echo $$DEP | cut -f 2 -d ':') ; \
        SUFF=$$(echo $$DEP | cut -f 3 -d ':') ; \
        VERS=$$(echo $$DEP | cut -f 4 -d ':') ; \
        JAR="$$GROU/$$ARTI/$$VERS/$$ARTI-$$VERS.$$SUFF" ; \
        echo Stored $$JAR ; \
        cp ~/.m2/repository/$$JAR $(PROJECT_DEPS) ; \
    done
	@touch $(PROJECT_DEPS)/updated.timestamp

LOGMODULENAMES_JAVA=src/main/java/com/viklauverk/eventbtools/core/LogModuleNames.java

logmodulenames:
	@echo "// Copyright Viklauverk AB 2021-2023" > $(LOGMODULENAMES_JAVA)
	@echo "// Generated by \"make logmodules\"" >> $(LOGMODULENAMES_JAVA)
	@echo "package com.viklauverk.eventbtools.core;" >> $(LOGMODULENAMES_JAVA)
	@echo "public enum LogModuleNames" >> $(LOGMODULENAMES_JAVA)
	@echo "{" >> $(LOGMODULENAMES_JAVA)
	@find src/main/java/com/viklauverk/eventbtools/ -name "*.java" \
			-exec grep -o "LogModule.lookup.*" \{\} \; | sort -u | grep -v String \
			| sed 's/.*("//' | sed 's/");/,/' >> $(LOGMODULENAMES_JAVA)
	@echo "}" >> $(LOGMODULENAMES_JAVA)
	@echo Created LogModuleNames.java

$(foreach templ,$(wildcard templates/*),$(eval $(TEMPLATES_JAVA):$(templ)))

$(TEMPLATES_JAVA):
	@echo Rebuilding templates.
	@echo "// Copyright Viklauverk AB 2021-2023" > $(TEMPLATES_JAVA)
	@echo "// Generated by \"make templates\"" >> $(TEMPLATES_JAVA)
	@echo "package com.viklauverk.eventbtools.core;" >> $(TEMPLATES_JAVA)
	@echo "public class Templates {" >> $(TEMPLATES_JAVA)
	@echo "public static String empty = \"\";" >> $(TEMPLATES_JAVA);
	@$(foreach templ,\
$(wildcard templates/*),\
echo "public static String $(notdir $(templ)) =" >> $(TEMPLATES_JAVA); \
cat $(templ) | sed 's/\\/\\\\/g' | sed 's/"/\\"/g' | sed 's/\(.*\)/"\1\\n"+/' >> $(TEMPLATES_JAVA); \
echo "\"\";" >> $(TEMPLATES_JAVA); \
)
	@echo "public static final String[] templates = {" >> $(TEMPLATES_JAVA)
	@$(foreach templ,\
$(wildcard templates/*),\
echo -n "\"$(notdir $(templ))\",$(notdir $(templ))," >> $(TEMPLATES_JAVA); \
)
	@echo "\"empty\",empty};" >> $(TEMPLATES_JAVA)
	@echo "}" >> $(TEMPLATES_JAVA)

templates: $(TEMPLATES_JAVA)

ifeq ($(MOST_RECENT),mvn)
test: testm
endif
ifeq ($(MOST_RECENT),javac)
test: testj
endif
ifeq ($(MOST_RECENT),graal)
test: testg
endif

testinternals:
	@echo "Testing $(call DROP_ROOT,$(BUILD)/$(MOST_RECENT)_bin/evbt)"
	@echo "    dir $(call DROP_ROOT,$(BUILD)/test_$(MOST_RECENT))"
	@java -ea -cp $(BUILD)/classes:$(BUILD)/generated-sources:$(JARS) com.viklauverk.eventbtools.TestInternals
	@echo "OK Tested internals."
	@java -ea -cp $(BUILD)/classes:$(BUILD)/generated-sources:$(JARS) com.viklauverk.eventbtools.core.TestInternals
	@echo "OK Tested core internals."


testm:
	@./tests/test.sh $(BUILD)/mvn_bin/evbt mvn
	@(cd models; make EVBT=$(BUILD)/mvn_bin/evbt CLEAN=true && make reset_pdfs)

testj: testinternals
	@./tests/test.sh $(BUILD)/javac_bin/evbt javac
	@(cd models; make EVBT=$(BUILD)/javac_bin/evbt CLEAN=true && make reset_pdfs)

testg: testinternals
	@./tests/test.sh $(BUILD)/graal_bin/evbt graal
	@(cd models; make EVBT=$(BUILD)/graal_bin/evbt CLEAN=true && make reset_pdfs)

PREFIX=/usr/local

install:
	@rm -f $(PREFIX)/bin/evbt*
	@if [ -x $(BUILD_MVN_BIN)/evbt ]; then cp $(BUILD_MVN_BIN)/evbt $(PREFIX)/bin/evbt.mvn ; fi
	@if [ -x $(BUILD_JAVAC_BIN)/evbt ]; then cp $(BUILD_JAVAC_BIN)/evbt $(PREFIX)/bin/evbt.javac ; fi
	@if [ -x $(BUILD_GRAAL_BIN)/evbt ]; then cp $(BUILD_GRAAL_BIN)/evbt $(PREFIX)/bin/evbt.graal ; fi
	@chmod a+x $(PREFIX)/bin/evbt*
	@ln -s $(PREFIX)/bin/evbt.$(MOST_RECENT) $(PREFIX)/bin/evbt
	@echo "Installed evbt.$(MOST_RECENT) as $(PREFIX)/bin/evbt"
	@rm -f $(PREFIX)/share/man/man1/evbt.1.gz
	@mkdir -p $(PREFIX)/share/man/man1
	@gzip -c doc/evbt.1 > $(PREFIX)/share/man/man1/evbt.1.gz
	@echo Installed evbt man page into $(PREFIX)/share/man/man1
	@(export TEXMF=`kpsewhich -var-value=TEXMFHOME` ; \
     export BSYMB=`kpsewhich bsymb.sty` ; \
     if [ "$$BSYMB" = "" ]; then echo "Installed bsymb.sty in $${TEXMF}" ; mkdir -p "$${TEXMF}/tex/latex/local/" ; cp doc/bsymb.sty "$${TEXMF}/tex/latex/local/" ; chown -R $$USER:$$USER "$${TEXMF}" ; else echo "bsymb.sty alread installed" ; fi)

clean:
	@echo -n "Removing build directory..."
	@rm -rf build
	@echo "done."

clean-tests:
	@echo -n "Removing $$(echo build/test_*) ..."
	@rm -rf build/test_*
	@echo "done."

# Rodin touchs bps and some other files when a workspace is opened.
# To prevent cluttering the git history, you can easily revert such changes.
reset-models:
	(cd models; make reset)

doc:
	@mkdir -p $(BUILD)/doc
	$(AT)evbt docmod tex doc/article.tex $(BUILD)/doc/art.tex
	$(AT)(cd $(BUILD)/doc/ ; xelatex art.tex && mv art.pdf article.pdf)

.PHONY: doc logmodulenames templates install clean doc

MAKEFLAGS += --no-builtin-rules
