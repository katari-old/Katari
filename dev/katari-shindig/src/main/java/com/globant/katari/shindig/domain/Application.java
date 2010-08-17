/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.Validate;
import org.w3c.dom.Document;

/** A social application, mainly represented by the gadget xml url.
 *
 * This id of this class is used as the open social appId.
 */
@Entity
@Table(name = "applications")
public class Application {

  /** The id of the application, 0 for a newly created one.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  /** The application (gadget) title.
   *
   * This should be obtained from the gadget xml specification. It is not a
   * responsibility of this class, though. It is null if not known.
   */
  @Column(nullable = true)
  private String title;

  /** {@link String} that identifies the url of the gadget xml spec.
   *
   * It is never null.
   */
  @Column(nullable = false)
  private String url;

  /** Hibernate constructor.
   */
  Application() {
  }

  /** Creates a new application.
   *
   * @param gadgetUrl with the url of the gadget xml. It cannot be empty.
   */
  public Application(final String gadgetUrl) {
    Validate.notEmpty(gadgetUrl, "gadget url can not be empty");
    url = gadgetUrl;

    InputStream gadgetSpecStream = null;
    try {
      // This should probably go to some utility.
      gadgetSpecStream = new URL(url).openStream();

      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true);
      DocumentBuilder builder = domFactory.newDocumentBuilder();
      Document doc = builder.parse(gadgetSpecStream);

      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      XPathExpression expr = xpath.compile("/Module/ModulePrefs/@title");

      Object result = expr.evaluate(doc, XPathConstants.STRING);
      if (result != null) {
        title = result.toString();
      }
    } catch (Exception e) {
      throw new RuntimeException("Error obtaining gadget title", e);
    } finally {
      if (gadgetSpecStream != null) {
        try {
          gadgetSpecStream.close();
        } catch (IOException e) {
          // Ignored, doesn't matter.
        }
      }
    }
  }

  /** @return long the id of the gadget instance.
   */
  public long getId() {
    return id;
  }

  /** The title of the gadget.
   *
   * @return The title of gadget, usually obtained from the gadget xml
   * specification. It returns null if the title can not be determined.
   */
  public String getTitle() {
    return title;
  }

  /** @return {@link String} location of the gadget xml spec.
   */
  public String getUrl() {
    return url;
  }
}

