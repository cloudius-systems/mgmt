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


