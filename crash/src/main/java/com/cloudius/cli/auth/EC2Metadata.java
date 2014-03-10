package com.cloudius.cli.auth;

import org.crsh.auth.AuthenticationPlugin;
import org.crsh.plugin.PropertyDescriptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class EC2Metadata extends CloudMetadataAuthPlugin {

  // A base URL to query for public keys of the instance
  public static final PropertyDescriptor<String> PUBLIC_KEYS_URL = PropertyDescriptor.create(
      "auth.ec2_metadata.public_keys_url",
      "http://169.254.169.254/2012-01-12/meta-data/public-keys/",
      "URL to look for public keys");

  // A default timeout to wait for the meta-data API to answer
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
    return "ec2_metadata";
  }

  protected ArrayList<String> getPublicKeys() throws MalformedURLException {
    String publicKeysURL = getContext().getProperty(PUBLIC_KEYS_URL);
    if (publicKeysURL == null) {
      publicKeysURL = PUBLIC_KEYS_URL.getDefaultValue();
    }

    ArrayList<String> availableKeys = getURL(new URL(publicKeysURL));
    ArrayList<String> ret = new ArrayList<>(availableKeys.size());

    for (String line : availableKeys) {
      if (line.contains("=")) {
        String[] keySpec = line.split("=");
        List<String> keyData = getURL(new URL(publicKeysURL + keySpec[0] + "/openssh-key"));
        for (String keyDataLine : keyData) {
          ret.add(keyDataLine);
        }
      }
    }

    return ret;
  }

  private ArrayList<String> getURL(URL url) {
    ArrayList<String> ret = new ArrayList<>();

    try {
      URLConnection urlConnection = url.openConnection();
      urlConnection.setConnectTimeout(connectTimeout);
      BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        ret.add(line);
      }
    } catch (IOException e) {
      log.log(Level.WARNING, "Failed to read URL: " + url.toString() + ", perhaps the meta-data API is not available" +
          " (not EC2)?", e);
    }

    return ret;
  }
}
