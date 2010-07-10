package com.globant.katari.gadgetcontainer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class Utils {

  public static String urlEncode(final String valueToEncode) {
    try {
      return URLEncoder.encode(valueToEncode, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
