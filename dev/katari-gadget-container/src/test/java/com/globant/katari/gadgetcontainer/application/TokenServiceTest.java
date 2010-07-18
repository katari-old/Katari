/**
 * 
 */
package com.globant.katari.gadgetcontainer.application;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.globant.katari.gadgetcontainer.domain.GadgetInstance;
import com.globant.katari.shindig.crypto.KatariBasicBlobCrypter;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class TokenServiceTest {
  
  KatariBasicBlobCrypter crypter = new KatariBasicBlobCrypter("1234567890123456");

  @Test
  public void testConstructor() {
    try {
      new TokenService(null, "name", "domain");
      fail("should fail because blobcrypter can not be null");
    } catch (IllegalArgumentException e) {
    }
    try {
      new TokenService(crypter, null, "domain");
      fail("should fail because container name can not be null");
    }catch (IllegalArgumentException e) {
    }
    try {
      new TokenService(crypter, "", "domain");
      fail("should fail because container name can not be empty");
    } catch (IllegalArgumentException e) {
    }
    try {
      new TokenService(crypter, "asd", null);
      fail("should fail because container domain can not be null");
    } catch (IllegalArgumentException e) {
    }
    try {
      new TokenService(crypter, "asd", "");
      fail("should fail because container domain can not be empty");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void testCreateSecurityToken() {
    TokenService ts = new TokenService(crypter, "name", "domain");
    GadgetInstance gi = createMock(GadgetInstance.class);
    expect(gi.getUrl()).andReturn("http://katari.com").times(2);
    replay(gi);
    String token = ts.createSecurityToken(1, 1, gi);
    assertNotNull(token);
    verify(gi);
  }
}

