package org.apache.shindig.auth;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypterException;

/** Implementation of token decoder.
 *
 * This is an implementation of SecurityTokenCodec that can be configured with
 * an external BlobCrypter, instead of creating itself the crypters with
 * information from ContainerConfig, as the default token decoder does.
 *
 * The BlobCrypter created by the default codec can only obtain the password
 * from a file.
 *
 * This is a hack because shindig's BlobCrypterSecurityToken#decrypt is package
 * access.
 *
 * @see {@link org.apache.shindig.auth.BlobCrypterSecurityTokenDecoder}
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class KatariBlobCrypterSecurityTokenDecoder
      implements SecurityTokenCodec {

  /** {@link String} the name of the domain related with this decoder.
   * Never null.
   */
  private final String domain;

  /** {@link BlobCrypter}.
   * Never null.
   */
  private final BlobCrypter crypter;

  /** Constructor.
   *
   * @param containerDomain {@linkString} the domain related with this decoder.
   * Can not be empty.
   * @param blobCrypter {@link BlobCrypter} the token crypter. Can not be null.
   */
  public KatariBlobCrypterSecurityTokenDecoder(final String containerDomain,
      final BlobCrypter blobCrypter) {

    Validate.notEmpty(containerDomain, "domain can not be blank");
    Validate.notNull(blobCrypter, "blobCrypter can not be blank");

    domain = containerDomain;
    crypter = blobCrypter;
  }

  /** @see org.apache.shindig.auth.SecurityTokenDecoder#createToken(
   *  java.util.Map)
   *
   *  @param tokenParameters {@link Map<String, String>} can not be empty.
   *
   *  @return {@link SecurityToken} if the token is null will return the
   *  implementation: {@link AnonymousSecurityToken}
   *
   *  @throws SecurityTokenException if can not decrypt the given token.
   */
  public SecurityToken createToken(final Map<String, String> tokenParameters)
      throws SecurityTokenException {

    Validate.notNull(tokenParameters);

    String token = tokenParameters.get(SECURITY_TOKEN_NAME);

    if (token == null || token.trim().length() == 0) {
      return new AnonymousSecurityToken();
    }

    String[] fields = token.split(":");

    if (fields.length != 2) {
      throw new SecurityTokenException("Invalid security token " + token);
    }

    String container = fields[0];
    String activeUrl = tokenParameters.get(ACTIVE_URL_NAME);
    String crypted = fields[1];

    try {
      return BlobCrypterSecurityToken.decrypt(
          crypter, container, domain, crypted, activeUrl);
    } catch (BlobCrypterException e) {
      throw new SecurityTokenException(e);
    }
  }

  public String encodeToken(final SecurityToken token)
        throws SecurityTokenException {
    if (!(token instanceof BlobCrypterSecurityToken)) {
      throw new SecurityTokenException(
          "Can only encode BlogCrypterSecurityTokens");
    }

    BlobCrypterSecurityToken t = (BlobCrypterSecurityToken) token;

    try {
      return t.encrypt();
    } catch (BlobCrypterException e) {
      throw new SecurityTokenException(e);
    }
  }
}
