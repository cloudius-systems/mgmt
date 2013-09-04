# Intro 

An example OSv application packaging, the application is packaged as a zip file.

Each zip file has to contain:

 * All the jars required to run it.
 * A json file that describes how to run the application.

The Json file has a simple layout:

```json
{
 "name" : "An example OSv application", 
 "args" : "-jar example-1.0.jar"
}
```

The json file should be called named after the zip file containing it, so for app-1.0.zip should contains app-1.0.json.

# Build

In order to package this example just run:

```groovy
 $ gradle zip
 $ ls build/libs/example-1.0.jar
```

