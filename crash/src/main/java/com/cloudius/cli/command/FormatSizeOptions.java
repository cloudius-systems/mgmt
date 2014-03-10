package com.cloudius.cli.command;

import org.crsh.cli.Option;
import org.crsh.cli.Usage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FormatSizeOptions {
  @Retention(RetentionPolicy.RUNTIME)
  @Usage("Display in bytes")
  @Option(names={"b"})
  @interface B { }

  @Retention(RetentionPolicy.RUNTIME)
  @Usage("Display in kilobytes")
  @Option(names={"b"})
  @interface Kb { }

  @Retention(RetentionPolicy.RUNTIME)
  @Usage("Display in megabytes")
  @Option(names={"b"})
  @interface Mb { }

  @Retention(RetentionPolicy.RUNTIME)
  @Usage("Display in gigabytes")
  @Option(names={"b"})
  @interface Gb { }

  @Retention(RetentionPolicy.RUNTIME)
  @Usage("Human readable, scale to shortest unit")
  @Option(names={"b"})
  @interface Human { }
}
