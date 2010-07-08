/**
 * 
 */
package com.globant.katari.gadgetcontainer.application;

import static org.slf4j.LoggerFactory.getLogger;

import org.apache.commons.lang.Validate;
import org.apache.shindig.auth.BlobCrypterSecurityToken;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.slf4j.Logger;

import com.globant.katari.gadgetcontainer.Utils;
import com.globant.katari.gadgetcontainer.domain.GadgetInstance;

/**
 * Implementation of token service thay provides an strong encryption.
 * 
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class TokenService {

  /** Log
   */
  private final Logger log = getLogger(TokenService.class);

  /** The crypt implementation.
   */
  private final BlobCrypter crypter;

  /** The os container name.
   */
  private final String container;

  /** The os domain name.
   */
  private final String domain;

  /** Constructor.
   * 
   * @param blobCrypter the object who crypt the token. It can not be null
   * @param containerName the cotnainer name defined in the OS container. 
   * Can not be null.
   * @param containerDomain the container name defined in the OS container.
   * Can not be null.
   */
  public TokenService(final BlobCrypter blobCrypter,
      final String containerName, final String containerDomain) {

    Validate.notNull(blobCrypter);
    Validate.notEmpty(containerName);
    Validate.notEmpty(containerDomain);

    crypter = blobCrypter;
    container = containerName;
    domain = containerDomain;
  }

  /**Creates a new security token encrypted with the strong implementation
   * define in katari-shindig.
   * 
   * {@inheritDoc}
   */
  public String createSecurityToken(final GadgetInstance gadgetInstance) {
    BlobCrypterSecurityToken token = new BlobCrypterSecurityToken(
        crypter, container, domain);
    token.setActiveUrl(gadgetInstance.getUrl());
    token.setAppUrl(gadgetInstance.getUrl());
    token.setModuleId(0L);
    token.setOwnerId(gadgetInstance.getUser());
    token.setTrustedJson("trusted");
    token.setViewerId(gadgetInstance.getUser());
    String cryptedToken;
    try {
      cryptedToken = Utils.urlEncode(token.encrypt());
      log.debug("generated new security token: " + cryptedToken);
      return cryptedToken;
    } catch (BlobCrypterException e) {
      throw new RuntimeException(e);
    }
    
  }
}
