package com.cloudius.cli.command;

import groovy.lang.Closure;
import org.crsh.cli.descriptor.ParameterDescriptor;
import org.crsh.cli.spi.Completion;
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
   * @param unmatched The command line arguments, if it ends with a & then closure will be executed in a different thread.
   * @param closure The closure to call, receives back the command line arguments (argv).
   *                If closure was executed in a different thread (i.e. daemonized), receives argv without &.
   */
  public void daemonizeIfNeeded(final String unmatched, final Closure closure) {
    final boolean daemonize = unmatched.length() > 0 && unmatched.lastIndexOf('&') == unmatched.length() - 1;

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        closure.call(daemonize ? unmatched.substring(0, Math.max(unmatched.lastIndexOf('&') - 1, 0)) : unmatched);
      }
    };

    if (daemonize) {
      new Thread(runnable).start();
    } else {
      runnable.run();
    }
  }

  /**
   * Builds a Completion.Builder object from a given list of strings, using the provided prefix. This structure
   * basically repeated itself in completion methods.
   *
   * @param options The options to build the list from.
   * @param parameter The parameter which is the completion target, currently not used in this method.
   * @param prefix The provided prefix.
   * @return Completion.Builder instance which includes all of the matching entries.
   */
  public static Completion.Builder complete(ArrayList<String> options, ParameterDescriptor parameter, String prefix) {
    Completion.Builder builder = Completion.builder(prefix);
    for (String option : options) {
      if (option.startsWith(prefix)) {
        builder.add(option.substring(prefix.length()), true);
      }
    }
    return builder;
  }
}
