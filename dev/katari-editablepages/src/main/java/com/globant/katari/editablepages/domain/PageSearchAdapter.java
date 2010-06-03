/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.domain;

import org.apache.commons.lang.Validate;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.search.domain.SearchAdapter;
import com.globant.katari.search.domain.SearchResultElement;
import com.globant.katari.search.domain.Action;

/** Converts Page objects to SearchResultElement.
 *
 * Implements the necessary SearchAdapter so that the search module can present
 * the result of a search when the object found is of type Page.
 */
public class PageSearchAdapter implements SearchAdapter {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(PageSearchAdapter.class);

  /** The prefix to prepend to all action urls.
   *
   * It is never null.
   */
  private String urlPrefix;

  /** Constructor.
   *
   * @param theUrlPrefix the prefix to prepend to all action urls. This url is
   * relative to the webapp context path. It cannot be null.
   */
  public PageSearchAdapter(final String theUrlPrefix) {
    Validate.notNull(theUrlPrefix, "The url prefix cannot be null.");
    urlPrefix = theUrlPrefix;
    if (!theUrlPrefix.endsWith("/")) {
      urlPrefix += "/";
    }
  }

  /** Converts (or wraps) the provided object to a SearchResultElement.
   *
   * Only call this operation if canConvert returned true on object, so object
   * must be an instance of Page.
   *
   * @param object the object to convert. It must be an instance of Page. It
   * cannot be null.
   *
   * @return a SearchResultElement initialized, never returns null.
   */
  public SearchResultElement convert(final Object object, final float score) {
    log.trace("Entering convert");

    Validate.notNull(object, "The object to convert cannot be null.");

    Page page = (Page) object;

    ArrayList<Action> actions;
    actions = new ArrayList<Action>();

    actions.add(new Action("Edit", null,
          urlPrefix + "edit/edit.do?id=" + page.getId()));

    StringBuilder description = new StringBuilder();
    description.append("Page - name: " + page.getName());
    description.append("; title: " + page.getTitle());
    if (page.getContent() != null) {
      description.append("; content: ");
      if (page.getContent().length() > 100) {
        description.append(page.getContent().substring(0, 100));
        description.append(" ...");
      } else {
        description.append(page.getContent());
      }
    }

    SearchResultElement result = new SearchResultElement("Page",
        page.getName(), description.toString(), urlPrefix
        + "page/" + page.getName(), actions, score);

    log.trace("Leaving convert");
    return result;
  }

  /** Returns the url to view a page, relative to the web application context.
   *
   * @return a string with the url.
   */
  public String getViewUrl() {
    // The page name is not significant, it is there only to match **/* in
    // acegi.
    return urlPrefix + "page/Z";
  }

  /** Returns which class we adapt.
   *
   * @return this implementation always returns Page.class.
   */
  public Class<Page> getAdaptedClass() {
    return Page.class;
  }
}


