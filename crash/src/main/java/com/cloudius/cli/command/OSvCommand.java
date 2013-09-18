package com.cloudius.cli.command;

import groovy.lang.Closure;
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

  /**
   * Decides whether to daemonize a closure or not
   *
   * @param argv The command line arguments, if it ends with a & then closure will be executed in a different thread.
   * @param closure The closure to call, receives back the command line arguments (argv).
   *                If closure was executed in a different thread (i.e. daemonized), receives argv without &.
   */
  public void daemonizeIfNeeded(final String argv, final Closure closure) {
    final boolean daemonize = argv.lastIndexOf('&') == argv.length() - 1;

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        closure.call(daemonize ? argv.substring(0, argv.lastIndexOf('&') - 1) : argv);
      }
    };

    if (daemonize) {
      new Thread(runnable, argv.substring(0, argv.indexOf(' '))).start();
    } else {
      runnable.run();
    }
  }
}
