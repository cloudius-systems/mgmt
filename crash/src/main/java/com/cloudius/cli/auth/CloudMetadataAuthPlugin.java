package com.cloudius.cli.auth;

import org.crsh.auth.AuthenticationPlugin;
import org.crsh.plugin.CRaSHPlugin;

import java.security.PublicKey;
import java.util.ArrayList;

public abstract class CloudMetadataAuthPlugin extends CRaSHPlugin<AuthenticationPlugin> implements AuthenticationPlugin<PublicKey> {

  // To decode the public key
  private OpenSSHKeyDecoder openSSHKeyDecoder = new OpenSSHKeyDecoder();

  @Override
  public abstract AuthenticationPlugin getImplementation();

  @Override
  public abstract String getName();

  @Override
  public Class<PublicKey> getCredentialType() {
    return PublicKey.class;
  }

  @Override
  public boolean authenticate(String username, PublicKey credential) throws Exception {
    // Username is not important for this implementation
    ArrayList<String> publicKeys = getPublicKeys();

    if (publicKeys != null) {
      for (String keyLine : publicKeys) {
        if (credential.equals(openSSHKeyDecoder.decodePublicKey(keyLine))) {
          return true;
        }
      }
    }

    return false;
  }

  protected abstract ArrayList<String> getPublicKeys() throws Exception;
}
