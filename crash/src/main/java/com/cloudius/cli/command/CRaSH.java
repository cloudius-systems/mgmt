package com.cloudius.cli.command;

import jline.NoInterruptUnixTerminal;
import jline.Terminal;
import jline.TerminalFactory;
import org.crsh.cli.Command;
import org.crsh.processor.jline.JLineProcessor;
import org.crsh.shell.Shell;
import org.crsh.shell.ShellFactory;
import org.crsh.standalone.Bootstrap;
import org.crsh.vfs.Path;
import org.fusesource.jansi.AnsiConsole;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

public class CRaSH {

  private static Logger log = Logger.getLogger(CRaSH.class.getName());

  @Command
  public void main() throws Exception {

    //
    final Bootstrap bootstrap = new Bootstrap(Thread.currentThread().getContextClassLoader());

    //
    bootstrap.addToCmdPath(Path.get("/crash/commands/cloudius/"));
    bootstrap.addToConfPath(Path.get("/crash/"));

    // Do bootstrap
    bootstrap.bootstrap();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        bootstrap.shutdown();
      }
    });

    //
    ShellFactory factory = bootstrap.getContext().getPlugin(ShellFactory.class);
    Shell shell = factory.create(null);

    // Start crash for this command line
    jline.TerminalFactory.registerFlavor(jline.TerminalFactory.Flavor.UNIX, NoInterruptUnixTerminal.class);
    final Terminal term = TerminalFactory.create();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          term.restore();
        } catch (Exception ignore) {
        }
      }
    });

    // Use AnsiConsole only if term doesn't support Ansi
    PrintStream out = System.out;
    PrintStream err = System.err;
    if (!term.isAnsiSupported()) {
      out = AnsiConsole.out;
      err = AnsiConsole.err;
    }

    //
    FileInputStream in = new FileInputStream(FileDescriptor.in);
    final JLineProcessor processor = new JLineProcessor(shell, in, out, err, term);

    //
    try {
      processor.run();
    } catch (Throwable t) {
      t.printStackTrace();
    } finally {
      // Force exit
      System.exit(0);
    }
  }
}