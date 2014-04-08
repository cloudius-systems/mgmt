quiet = $(if $V, $1, @echo " $2"; $1)

module: crash

crash:
	cd crash && mvn package

clean:
	$(call quiet, cd crash && mvn clean, MVN CLEAN)

.PHONY: crash clean module
