
$(shell rm -f /tmp/evbtmodels.log)

ifeq ($(CLEAN),true)
$(shell rm *.pdf)
endif

EVBT?=evbt
DIRS=$(shell echo */ | tr -d '/')
PDFS=$(addsuffix .pdf,$(DIRS))
INFOS=$(addsuffix /project.info,$(DIRS))

all: $(PDFS) README.md
	@echo Generated pdf:s for all test models.

bsymb.sty:
	cp ../doc/bsymb.sty .

%.pdf: % bsymb.sty
	$(EVBT) docgen -q tex $<
	@echo "xelatex $<.tex"
	@xelatex -interaction=batchmode -halt-on-error $<.tex >> /tmp/evbtmodels.log 2>&1
	@echo "makeindex $<.idx"
	@makeindex $<.idx >> /tmp/evbtmodels.log 2>&1
	@echo "xelatex $<.tex"
	@xelatex -interaction=batchmode -halt-on-error $<.tex >> /tmp/evbtmodels.log 2>&1
	@echo "created $<.pdf"
	@rm $$(ls $<.* | grep -v pdf)
	@rm $<_template.tex

define SUMMARIZE
echo "## $1" >> README.md; \
if [ -f $1/project.info ]; then cat $1/project.info >> README.md; echo >> README.md; fi; \
echo >> README.md; \
echo "[$1.pdf]($1.pdf)" >> README.md; \
echo >> README.md;
endef

README.md: $(INFOS)
	@rm -f README.md
	@echo "# Models used for testing evbt." >> README.md
	@echo "" >> README.md
	@$(foreach d,$(DIRS),$(call SUMMARIZE,$d))
	@echo "Created README.md"

# The pdfs are recreated as part of running make test.
# This command resets them to the version in git.
reset_pdfs:
	git checkout $(PDFS)

reset:
	git checkout -f .
