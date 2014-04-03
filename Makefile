quiet = $(if $V, $1, @echo " $2"; $1)

module: all

all:
	./gradlew --daemon build

clean:
	$(call quiet, ./gradlew --daemon clean >> /dev/null, GRADLE CLEAN)
