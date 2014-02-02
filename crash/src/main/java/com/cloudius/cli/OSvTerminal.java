package com.cloudius.cli;

import jline.TerminalSupport;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A minimal terminal implementation for JLine
 */
public class OSvTerminal extends TerminalSupport {
  private final Logger log = Logger.getLogger(getClass().getName());

  public Class<?> sttyClass = null;
  public Object stty = null;

  public OSvTerminal() {
    // Call super() with supported = true
    super(true);

    setAnsiSupported(true);
    try {
      if (stty == null) {
        sttyClass = Class.forName("com.cloudius.util.Stty");
        stty = sttyClass.newInstance();
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Failed to load com.cloudius.util.Stty", e);
    }
  }

  @Override
  public void init() throws Exception {
    super.init();

    if (stty != null) {
      sttyClass.getMethod("jlineMode").invoke(stty);
    }
  }

  @Override
  public void restore() throws Exception {
    if (stty != null) {
      sttyClass.getMethod("reset").invoke(stty);
    }
    super.restore();

    // Newline in end of restore like in jline.UnixTermianl
    System.out.println();
  }
}
