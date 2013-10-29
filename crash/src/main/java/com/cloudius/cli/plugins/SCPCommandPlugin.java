package com.cloudius.cli.plugins;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.crsh.ssh.term.scp.CommandPlugin;

public class SCPCommandPlugin extends CommandPlugin {
  ScpCommandFactory _Factory = new ScpCommandFactory();

  @Override
  public Command createCommand(String command) {
    try {
      return _Factory.createCommand(command);
    } catch (IllegalArgumentException iae) {
      // Probably not a 'scp' command, return null to enable crash to search for a different plugin
      return null;
    }
  }
}
