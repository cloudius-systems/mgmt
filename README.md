# Intro

This is the OSv management layer implementation, its mainly implemented in Java and JRuby.

# Build

This project is built using Gradle, there is no need to install it just run the provided [wrapper](http://www.gradle.org/docs/current/userguide/gradle_wrapper.html).

Available tasks:

```bash 
 # Creating cli jar
 $ ./gradlew :cli:jar
 # Creating web jar
 $ ./gradlew :web:jar
 # Running the web application
 $ ./gradlew :web:runapp
```

# Adding new modules

 * mkdir mgmt/${new-module}/src/main/java/com/ -p
 * vi mgmt/settings.gradle (and the module name to the list)
 * create a new mgmt/${new-module}/build.gradle file under the new module see mgmt/sshd/build.gradle as an example (ask [me](https://github.com/narkisr) if you need more guidance).

# CRaSH

In order to execute current crash build locally, you must select a different "jline.Terminal" implementation. The
current terminal that was built for OSv (com.cloudius.cli.OSvTerminal) is using the stty JNI for sending commands to
ioctl.

So there are two ways to execute it:

1. Launch crash with a automatic terminal implementation discovery: java -Djline.terminal=auto -jar crash/build/libs/crash-1.0.jar
1. Copy stty.so (from the master project build) to /usr/lib/jni/stty.so, and launch crash: java -jar crash/build/libs/crash-1.0.jar

## CRaSH SSHD

After launching CRaSH, you will need to configure an SSH key pair for authentication.
