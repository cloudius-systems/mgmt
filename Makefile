quiet = $(if $V, $1, @echo " $2"; $1)

module: all

all: mk-gradle httpserver.so swagger-ui-lib

mk-gradle:
	./gradlew --daemon build

httpserver.so:
	make -C httpserver

swagger-ui-lib: swagger-ui/dist

swagger-ui/dist:
	git submodule update --init -f

clean: clean-httpserver clean-gradle

clean-httpserver:
	make -C httpserver clean

clean-gradle:
	$(call quiet, ./gradlew --daemon clean >> /dev/null, GRADLE CLEAN)

check:
	cd ../ && ./mgmt/tests/testhttpserver.py
