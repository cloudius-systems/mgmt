package com.cloudius.cli.command;

import org.crsh.cli.impl.bootstrap.CommandProvider;

public class CRaSHProvider implements CommandProvider {
  public Class<?> getCommandClass() {
    return CRaSH.class;
  }
}
