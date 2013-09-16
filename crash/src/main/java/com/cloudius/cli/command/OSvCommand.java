package com.cloudius.cli.command;

import org.crsh.command.CRaSHCommand;

import java.util.ArrayList;

public class OSvCommand extends CRaSHCommand {
  public String itemsToColumns(ArrayList<String> items, Integer width) {
    Integer maxWidth = 0;
    for (String item : items) {
      if (item.length() > maxWidth) maxWidth = item.length();
    }

    maxWidth += 3; // For padding

    StringBuilder ret = new StringBuilder();
    Integer currentWidth = 1;
    for (String item : items) {
      ret.append(String.format("%-" + maxWidth + "s", item));
      currentWidth += maxWidth;

      if (width < currentWidth + maxWidth) {
        ret.append('\n');
        currentWidth = 1;
      }
    }

    return ret.toString();
  }
}

