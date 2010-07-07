/**
 * 
 */
package com.globant.katari.gadgetcontainer.application;

import com.globant.katari.gadgetcontainer.domain.GadgetInstance;

/**
 * This service creates an open social's security token.
 * 
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
public interface TokenService {
  /**
   * Creates a security token for the opensocial container.
   * @param gadgetInstance {@link GadgetInstance} the gadget instance that
   * the service use to generate the token. Can not be null.
   * @return {@link String} the security token. Never returns null.
   */
  String createSecurityToken(final GadgetInstance gadgetInstance);
}
