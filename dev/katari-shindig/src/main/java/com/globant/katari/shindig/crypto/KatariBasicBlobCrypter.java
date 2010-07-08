/**
 * 
 */
package com.globant.katari.shindig.crypto;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.Validate;
import org.apache.shindig.common.crypto.BasicBlobCrypter;

/**
 * Wrapper for easy construction of {@link BasicBlobCrypter}
 * 
 * @see {@link org.apache.shindig.common.crypto.BasicBlobCrypter}
 * 
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class KatariBasicBlobCrypter extends BasicBlobCrypter {
  
  /**Constructor
   * 
   * @param key {@link String} the key to use to creates security tokens.
   * Can not be empty. Also shouls have at least 16 bytes of lenght.
   */
  public KatariBasicBlobCrypter(final String key) {
    super(getBytes(key));
  }
  
  /** Validate and get bytes with specific charset (UTF-8)
   * @param key {@link String} the key to encode the tokens.
   * @return byte[] with utf-8 charset
   * @throws IllegalArgumentException if the key is blank
   * @throws RuntimeException if an UnsupportedEncodingException happends.
   */
  private static byte[] getBytes(final String key) {
    Validate.notEmpty(key, "the key can not be null");
    try {
      return key.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
