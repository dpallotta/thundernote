package it.psd.thundernote.server.utils;

import java.util.Properties;

public class AuthenticationProvider {

  public static Integer GOOGLE=1, TWITTER=2, FACEBOOK=3;

  private static Properties props = System.getProperties();
  
  public static String getProp(String param){
      String skey = props.getProperty(param);
      return skey;
  }
  
}