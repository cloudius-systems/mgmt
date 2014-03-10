package com.cloudius.cli.command;

import org.crsh.cli.Argument;
import org.crsh.cli.Man;
import org.crsh.cli.Usage;
import org.crsh.cli.completers.SystemPropertyNameCompleter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PropertyOptions {
  @Retention(RetentionPolicy.RUNTIME)
  @Usage("the property name")
  @Man("The name of the property")
  @Argument(name = "name", completer = SystemPropertyNameCompleter.class)
  @interface PropertyName { }

  @Retention(RetentionPolicy.RUNTIME)
  @Usage("the property value")
  @Man("The value of the property")
  @Argument(name = "value")
  @interface PropertyValue { }
}
