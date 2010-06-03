/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import com.globant.katari.cas.UrlTransformer;

import junit.framework.TestCase;

/* Tests the cas service builder.
 */
public class UrlTransformerTest extends TestCase {

  /* Tests that the UrlTransformer transforms to the same url.
   */
  public final void testTransformUrl_identity() {

    UrlTransformer transformer;
    transformer = new UrlTransformer(null, null, null, null);

    String result = transformer.transform("http", "localhost", 10,
        "/katari-web");

    assertEquals(result, "http://localhost:10/katari-web");
  }

  /* Tests that the UrlTransformer transforms to an incremented port.
   */
  public final void testTransformUrl_increment() {

    UrlTransformer transformer;
    transformer = new UrlTransformer(null, "+1", null, null);

    String result = transformer.transform("http", "localhost", 10,
        "/katari-web");

    assertEquals(result, "http://localhost:11/katari-web");
  }

  /* Tests that the UrlTransformer transforms to a decremented port.
   */
  public final void testTransformUrl_decrement() {

    UrlTransformer transformer;
    transformer = new UrlTransformer(null, "-1", null, null);

    String result = transformer.transform("http", "localhost", 10,
        "/katari-web");

    assertEquals(result, "http://localhost:9/katari-web");
  }

  /* Tests that the UrlTransformer transforms to a specific port.
   */
  public final void testTransformUrl_setPort() {

    UrlTransformer transformer;
    transformer = new UrlTransformer(null, "20", null, null);

    String result = transformer.transform("http", "localhost", 10,
        "/katari-web");

    assertEquals(result, "http://localhost:20/katari-web");
  }

  /* Tests that the UrlTransformer transforms to a specific port taken from a
   * system property.
   */
  public final void testTransformUrl_setPortFromProperty() {

    UrlTransformer transformer;
    String previous = System.setProperty("property", "20");
    transformer = new UrlTransformer(null, "property", null, null);

    String result = transformer.transform("http", "localhost", 10,
        "/katari-web");

    assertEquals(result, "http://localhost:20/katari-web");

    if (previous != null) {
      System.setProperty("casPortPropertyName", previous);
    }
  }

  /* Tests that the UrlTransformer transforms the path through a regular
   * expression.
   */
  public final void testTransformUrl_regex() {

    UrlTransformer transformer;
    transformer = new UrlTransformer(null, null, "^(.*)source$", "$1web");

    String result = transformer.transform("http", "localhost", 10,
        "/katari-source");

    assertEquals(result, "http://localhost:10/katari-web");
  }

  /* Tests that the UrlTransformer sets the path.
   */
  public final void testTransformUrl_setPath() {

    UrlTransformer transformer;
    transformer = new UrlTransformer(null, null, null, "/katari-web");

    String result = transformer.transform("http", "localhost", 10,
        "/katari-web");

    assertEquals(result, "http://localhost:10/katari-web");
  }

  /* Tests that the UrlTransformer sets the path.
   */
  public final void testTransformUrl_changeServer() {

    UrlTransformer transformer;
    transformer = new UrlTransformer("127.0.0.1", null, null, "/katari-web");

    String result = transformer.transform("http", "localhost", 10,
        "/katari-web");

    assertEquals(result, "http://127.0.0.1:10/katari-web");
  }
}

