# Intro 

An example OSv application packaging, the application is packaged as a zip file.

Each zip file has to contain:

 * All the jars required to run it.
 * A json file that describes how to run the application.

The Json file has a simple layout:

```json
{
 "name" : "An example OSv application",
 "main" : "com.cloudius.example.Main",
 "args" : []
}
```

The json file should be called named after the zip file containing it, so for app-1.0.zip should contains app-1.0.json.

# Main Vs Runnable

The json specifies a main class from which the application will be spawned of, main classes that implement the java.lang.Runnable interface are expected to implement a stop method as well:

```java
public class Runner implements Runnable {

  private boolean run = true;

  public static void main(String [] args) {
    new Runner().run();
  }

  public void run(){
    int i =0;
    while(run){
	try{
	  Thread.sleep(1000);
	  System.out.println(i++);
	} catch (Exception e) {
	  System.err.println(e);
	}
    }
  }

  public void stop(){
    run = false;
  }

}
``` 

Applications with main classes that follow this contract will be stoppable, classes with static main will only allow start.

# Build

Two examples are packaged:

```groovy
 $ gradle zip
 $ ls build/distributions/
   main-1.0.zip  runner-1.0.zip
```

The first one is based off a main method entry point, the second one implmenets the Runnable interface. 
