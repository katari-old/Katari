/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import java.util.List;
import java.util.LinkedList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang.Validate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ElementCollection;
import javax.persistence.CollectionTable;
import javax.persistence.JoinColumn;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
   * This is obtained from the gadget xml. Never null.
   */
  @Column(nullable = false)
  private String title;

  /** The optional gadget icon.
   *
   * This is obtained from the gadget xml.
   *
   * It is null if not known.
   */
  @Column(nullable = true)
  private String icon;

  /** The optional gadget description.
   *
   * This is obtained from the gadget xml.
   *
   * It is null if not known.
   */
  @Column(length = 4096, nullable = true)
  private String description;

  /** The optional gadget author.
   *
   * This is obtained from the gadget xml.
   *
   * It is null if not known.
   */
  @Column(nullable = true)
  private String author;

  /** The optional gadget thumbnail.
   *
   * This is obtained from the gadget xml.
   *
   * It is null if not known.
   */
  @Column(nullable = true)
  private String thumbnail;

  /** The url of the gadget xml spec.
   *
   * It is never null.
   */
  @Column(nullable = false)
  private String url;

  /** The list of views that the gadget supports.
   *
   * If the gadget supports the view named 'default', the gadget can be shown
   * on every gadget group. Otherwise, the gadget must support the same view as
   * the group. In other words, if the gadget does not support the view of the
   * group, it cannot be added to that group. A gadget with an empty
   * supportedViews list cannot be used anywhere.
   *
   * It is never null. 
   */
  @ElementCollection
  @CollectionTable(name = "supported_views",
      joinColumns = @JoinColumn(name = "application_id")
  )
  @Column(name = "view_name", nullable = false)
  public List<String> supportedViews = new LinkedList<String>();

  /** Hibernate constructor.
   */
  Application() {
  }

  /** Creates a new application.
   *
   * This constructor obtains all the gadget information from the xml obtained
   * from gadgetUrl. This constructor can throw an exception if the url is not
   * accessible.
   *
   * If the title is not found in the gadget xml spec, it sets it to the gadget
   * xml url.
   *
   * @param gadgetUrl with the url of the gadget xml. It cannot be empty.
   */
  public Application(final String gadgetUrl) {
    Validate.notEmpty(gadgetUrl, "gadget url cannot be empty");
    url = gadgetUrl;

    InputStream gadgetSpecStream = null;
    String message = "Error obtaining gadget information.";
    try {
      // This should probably go to some utility.
      gadgetSpecStream = new URL(url).openStream();

      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true);
      DocumentBuilder builder = domFactory.newDocumentBuilder();
      Document document = builder.parse(gadgetSpecStream);

      message = "Error obtaining gadget title";
      title = getXpathValue(document, "/Module/ModulePrefs/@title");
      if (title == null) {
        title = gadgetUrl;
      }
      message = "Error obtaining gadget icon";
      icon = getXpathValue(document, "/Module/ModulePrefs/icon/text()");
      message = "Error obtaining gadget description";
      description = getXpathValue(document,
          "/Module/ModulePrefs/@description");
      message = "Error obtaining gadget author";
      author = getXpathValue(document, "/Module/ModulePrefs/@author");
      message = "Error obtaining gadget thumbnail";
      thumbnail = getXpathValue(document, "/Module/ModulePrefs/@thumbnail");

      message = "Error obtaining gadget views";
      List<String> viewAttributes;
      viewAttributes = getXpathValues(document, "/Module/Content/@view");
      // The view attribute is a comma separated list of views.
      for (String viewAttribute : viewAttributes) {
        for (String view : viewAttribute.split(" *, *")) {
          supportedViews.add(view);
        }
      }
      // Checks for a content with no view.
      if (xpathMatches(document, "/Module/Content[not(@view)]")) {
        supportedViews.add("default");
      }

    } catch (Exception e) {
      throw new RuntimeException(message, e);
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

  /** Retuns the result of evaluating an xpath expression.
   * 
   * @param document the document to evaluate the expression against.
   * 
   * @param expression the xpath expression to evaluate.
   * 
   * @return returns a string with the result of evaluationg the xpath
   * expression. It never returns null, even if the element is not found.
   */
  private String getXpathValue(final Document document,
      final String expression) throws XPathExpressionException {

    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    XPathExpression expr = xpath.compile(expression);

    Object result = expr.evaluate(document, XPathConstants.STRING);
    // Tracing through java's code, it appears that result should never be
    // null: if the evaluated xpath expression did not match a node, evaluate
    // returns an empty string instead of null. But this is not documented in
    // the api.
    Validate.notNull(result, "Should never be null.");
    return result.toString();
  }

  /** Retuns the result of evaluating an xpath expression that could result in
   * many elements.
   * 
   * @param document the document to evaluate the expression against. It cannot
   * be null.
   * 
   * @param expression the xpath expression to evaluate. It cannot be null.
   * 
   * @return returns a list of strings with the result of evaluationg the xpath
   * expression. It never returns null.
   */
  private List<String> getXpathValues(final Document document,
      final String expression) throws XPathExpressionException {

    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    XPathExpression expr = xpath.compile(expression);

    Object values = expr.evaluate(document, XPathConstants.NODESET);
    NodeList list = (NodeList) values;
    List<String> result = new LinkedList<String>();
    for (int i = 0; i < list.getLength(); i++) {
      String nodeValue = list.item(i).getNodeValue();
      if (nodeValue != null) {
        result.add(nodeValue);
      }
    }
    return result;
  }

  /** Checks if an xpath expresion finds a node.
   *
   * @param document the document to evaluate the expression against. It cannot
   * be null.
   * 
   * @param expression the xpath expression to evaluate. It cannot be null.
   * 
   * @return true if the expression finds a node, false otherwise.
   */
  private boolean xpathMatches(final Document document,
      final String expression) throws XPathExpressionException {

    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    XPathExpression expr = xpath.compile(expression);

    Object values = expr.evaluate(document, XPathConstants.NODESET);
    NodeList list = (NodeList) values;
    return list.getLength() != 0;
  }

  /** @return long the id of the gadget instance.
   */
  public long getId() {
    return id;
  }

  /** The title of the gadget.
   *
   * @return The title of gadget, usually obtained from the gadget xml
   * specification. It never returns null.
   */
  public String getTitle() {
    return title;
  }

  /** The url of the icon of the gadget.
   *
   * @return The icon of the gadget, usually obtained from the gadget xml
   * specification. It returns null if the icon cannot be determined.
   */
  public String getIcon() {
    return icon;
  }

  /** The description of the gadget.
   *
   * @return The description of the gadget, usually obtained from the gadget
   * xml specification. It returns null if the description cannot be
   * determined. It can also be an empty string.
   */
  public String getDescription() {
    return description;
  }

  /** The author of the gadget.
   *
   * @return The author of the gadget, usually obtained from the gadget xml
   * specification. It returns null if the author cannot be determined.
   */
  public String getAuthor() {
    return author;
  }

  /** The url of the thumbnail of the gadget.
   *
   * @return The thumbnail of the gadget, usually obtained from the gadget xml
   * specification. It returns null if the thumbnail cannot be determined.
   */
  public String getThumbnail() {
    return thumbnail;
  }

  /** The url of the gadget xml spec.
   *
   * @return a string with the location of the gadget xml spec, never null.
   */
  public String getUrl() {
    return url;
  }

  /** Verifies if the gadget supports the provided view.
   *
   * If the gadget has a default view, it means that it supports all views.
   *
   * @param view the view to check. It cannot be null.
   * 
   * @return true if the gadget support the view.
   */
  public boolean isViewSupported(final String view) {
    if (supportedViews.contains(view)) {
      return true;
    }
    return supportedViews.contains("default");
  }
}

