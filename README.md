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

