package com.cloudius.cli.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class OSvAPI {
  protected final static Logger log = Logger.getLogger(OSvAPI.class.getName());

  protected static String API_ENDPOINT = "http://localhost:8000";
  protected static Charset REQUEST_CHARSET = StandardCharsets.UTF_8;

  /**
   * Performs a local API (for a descriptive list, see /ui) call and returns the Http response.
   *
   * It doesn't handle exceptions at all and expects the commandline to log/format the proper message back to the user,
   * which is the default behavior of CRaSH in case of unhandled exceptions.
   *
   * @param path   The path of the API
   * @param method The method to use (GET, POST, etc.)
   * @param data   If method is POST, this is used as the data to send
   * @return The output body of the request
   */
  public static String call(String path, String method, String data) throws IOException {
    URL url = new URL(API_ENDPOINT + path);

    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.setRequestMethod(method);

      // POST the data if necessary
      if (method.equals("POST")) {
        byte[] dataBytes = data != null ? data.getBytes(REQUEST_CHARSET) : null;

        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=" + REQUEST_CHARSET);
        connection.setRequestProperty("Content-Length", Integer.toString(dataBytes != null ? dataBytes.length : 0));
        if (data != null) {
          connection.getOutputStream().write(dataBytes);
        }
      }

      // Read the output and return to the user
      return IOUtils.toString(connection.getInputStream());
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  /**
   * Returns `call(path, "GET", null)`
   *
   * @param path The path of the API
   */
  public static String get(String path) throws IOException {
    return call(path, "GET", null);
  }

  /**
   * Returns call(path, "POST", data)`
   *
   * @param path The path of the API
   * @param data The data to POST
   */
  public static String post(String path, String data) throws IOException {
    return call(path, "POST", data);
  }
}
