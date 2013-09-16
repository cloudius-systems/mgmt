package com.cloudius.cli.completers;

import org.crsh.cli.completers.FileCompleter;

import java.io.File;

public class PathCompleter extends FileCompleter {
  private File currentPath;
  public PathCompleter(File currentPath) {
    this.currentPath = currentPath;
  }

  @Override
  protected String getCurrentPath() throws Exception {
    return this.currentPath.getPath();
  }
}
