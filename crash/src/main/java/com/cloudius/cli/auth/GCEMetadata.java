package com.cloudius.cli.auth;

import org.apache.commons.io.IOUtils;
import org.crsh.auth.AuthenticationPlugin;
import org.crsh.plugin.PropertyDescriptor;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class GCEMetadata extends CloudMetadataAuthPlugin {

  // A base URL to query for public keys of the instance
  public static final PropertyDescriptor<String> PUBLIC_KEYS_URL = PropertyDescriptor.create(
      "auth.gce_metadata.public_keys_url",
      "http://169.254.169.254/computeMetadata/v1/project/attributes/sshKeys",
      "URL to look for public keys");

  private int connectTimeout = 1000;

  @Override
  protected Iterable<PropertyDescriptor<?>> createConfigurationCapabilities() {
    return Arrays.<PropertyDescriptor<?>>asList(PUBLIC_KEYS_URL);
  }

  @Override
  public AuthenticationPlugin getImplementation() {
    return this;
  }

  @Override
  public String getName() {
    return "gce_metadata";
  }

  @Override
  protected ArrayList<String> getPublicKeys() throws IOException {
    String publicKeysURL = getContext().getProperty(PUBLIC_KEYS_URL);
    if (publicKeysURL == null) {
      publicKeysURL = PUBLIC_KEYS_URL.getDefaultValue();
    }

    try {
      ArrayList<String> ret = new ArrayList<>(1);

      URLConnection conn = new URL(publicKeysURL).openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setRequestProperty("X-Google-Metadata-Request", "True");

      for (String line : IOUtils.readLines(conn.getInputStream())) {
        ret.add(line);
      }

      return ret;
    } catch (IOException e) {
      log.log(Level.WARNING, "Failed to read URL: " + publicKeysURL + ", perhaps the meta-data API is not available" +
          " (not GCE)?", e);
    }

    return null;
  }
}
