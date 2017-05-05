package it.psd.thundernote.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlFetcher {

  private static String getString(InputStream is, String charEncoding) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte ba[] = new byte[8192];
      int read = is.read(ba);
      while (read > -1) {
        out.write(ba, 0, read);
        read = is.read(ba);
      }
      String returnString = out.toString(charEncoding);
      if (returnString.equalsIgnoreCase("{}")) {
        returnString = "[{}]";
      }
      return returnString;
    } catch (Exception e) {
      return null;
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (Exception e) {
      }
    }
  }

  public static String get(String thisUrl) throws MalformedURLException,
      IOException {
    URL url = new URL(thisUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    return getString(con.getInputStream(), "utf-8");
  }

}
