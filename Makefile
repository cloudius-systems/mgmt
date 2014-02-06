all: httpserver.so swagger-ui-lib

httpserver.so:
	make -C httpserver

swagger-ui-lib: swagger-ui/dist

swagger-ui/dist:
	git submodule update --init -f
