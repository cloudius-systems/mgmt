package com.cloudius.cli.auth;

import org.crsh.auth.AuthenticationPlugin;
import org.crsh.plugin.CRaSHPlugin;
import org.crsh.plugin.PropertyDescriptor;

import java.io.File;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;

public class OpenSSHAuthorizedKeys extends CRaSHPlugin<AuthenticationPlugin> implements AuthenticationPlugin<PublicKey> {

  private OpenSSHKeyDecoder openSSHKeyDecoder = new OpenSSHKeyDecoder();

  /** The SSH authorized keys path. */
  public static final PropertyDescriptor<String> AUTHORIZED_KEYS_PATH = PropertyDescriptor.create(
      "auth.openssh.authorized_keys",
      "/etc/ssh/authorized_keys",
      "The path to the authorized keys file");

  @Override
  public AuthenticationPlugin getImplementation() {
    return this;
  }

  @Override
  public String getName() {
    return "openssh";
  }

  @Override
  public Class<PublicKey> getCredentialType() {
    return PublicKey.class;
  }

  @Override
  protected Iterable<PropertyDescriptor<?>> createConfigurationCapabilities() {
    return Arrays.<PropertyDescriptor<?>>asList(AUTHORIZED_KEYS_PATH);
  }

  @Override
  public boolean authenticate(String username, PublicKey credential) throws Exception {
    String authorizedKeysPath = getContext().getProperty(AUTHORIZED_KEYS_PATH);
    if (authorizedKeysPath == null) {
      authorizedKeysPath = AUTHORIZED_KEYS_PATH.getDefaultValue();
    }

    File authorizedKeysFile = new File(authorizedKeysPath);
    if (authorizedKeysFile.exists()) {
      Scanner scanner = new Scanner(authorizedKeysFile).useDelimiter("\n");
      while (scanner.hasNext()) {
        String line = scanner.next();
        try {
          if (credential.equals(openSSHKeyDecoder.decodePublicKey(line))) {
            scanner.close();
            return true;
          }
        } catch (Exception e) {
          log.log(Level.WARNING, "Exception while trying to decode public key: " + line, e);
        }
      }
      scanner.close();
    } else {
      log.log(Level.WARNING, "File '" + authorizedKeysFile.getAbsolutePath() + "' not found.");
    }

    return false;
  }
}
