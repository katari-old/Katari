package com.globant.katari.shindig.application;

import java.net.URLEncoder;

import org.apache.commons.lang.Validate;
import org.apache.shindig.auth.BlobCrypterSecurityToken;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.GadgetException.Code;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;

import com.google.inject.Singleton;

/* vim: set ts=2 et sw=2 cindent fo=qroca: */

/** A HttpFetcher wrapper that adds a st parameter (security token) with a fake
 * user.
 *
 * This is intended to be used in DefaultServiceFetcher to obtain the rpc
 * endpoints when the container does not support non-authenticated requests.
 *
 * This object delegates the final request to the provided HttpFetcher.
 */
@Singleton
public class FakeUserHttpFetcher implements HttpFetcher {

  /** The fetcher that performs the work.
   *
   * This is never null.
   */
  private HttpFetcher fetcher;

  /** The container config, needed to create a token.
   *
   * This is never null.
   */
  private ContainerConfig containerConfig;

  /** The encryption implementation.
   *
   * This is never null.
   */
  private BlobCrypter crypter;

  /** Creates a new fetcher.
   *
   * @param config the containr config needed to create a token.
   *
   * @param theFetcher the fetcher that performs the actual fetch work. It
   * cannot be null.
   *
   * @param theCrypter the encryption implementation. It cannot be null.
   */
  public FakeUserHttpFetcher(final ContainerConfig config,
      final HttpFetcher theFetcher, final BlobCrypter theCrypter) {
    Validate.notNull(config, "The container config cannot be null.");
    Validate.notNull(theFetcher, "The fetcher cannot be null.");
    Validate.notNull(theCrypter, "The crypter cannot be null.");
    containerConfig = config;
    fetcher = theFetcher;
    crypter = theCrypter;
  }

  /** {@inheritDoc}
   *
   * This implementation decorates the request and adds a token for a fake
   * user.
   *
   * The user is called 'system'.
   */
  public HttpResponse fetch(final HttpRequest request) throws GadgetException {

    Uri uri = request.getUri();

    if (uri.getQueryParameter("st") == null) {

      String url = uri.toString().substring(0, uri.toString().indexOf('?'));

      BlobCrypterSecurityToken systemToken;
      systemToken = new BlobCrypterSecurityToken(crypter,
          containerConfig.getContainers().iterator().next(), null);
      systemToken.setActiveUrl(url);
      systemToken.setAppUrl(url);
      systemToken.setModuleId(1L);
      systemToken.setOwnerId("system");
      systemToken.setTrustedJson("system");
      systemToken.setViewerId("system");
      String token;

      try {
        token = URLEncoder.encode(systemToken.encrypt(), "UTF-8");
      } catch (Exception e) {
        throw new GadgetException(Code.INTERNAL_SERVER_ERROR, e);
      }
      request.setUri(Uri.parse(uri.toString() + "&st=" + token));
    }
    return fetcher.fetch(request);
  }
}

