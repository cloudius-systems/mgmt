package com.cloudius.cli.plugins;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.crsh.ssh.term.subsystem.SubsystemFactoryPlugin;

public class SftpSubsystemPlugin extends SubsystemFactoryPlugin {
  @Override
  public NamedFactory<Command> getFactory() {
    return new SftpSubsystem.Factory();
  }
}
