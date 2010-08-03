/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.application;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.core.application.Initializable;

import com.globant.katari.editablepages.domain.Page;
import com.globant.katari.editablepages.domain.PageRepository;

/** Used to show a page from a weblet.
 *
 * This command is intended to be used with PageEditController and page.ftl
 * view. This is not strictly a command, given that we only need the
 * initialization phase.
 */
public class ShowPageCommand implements Initializable {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(ShowPageCommand.class);

  /** The page repository.
   */
  private PageRepository pageRepository;

  /** The page id.
   */
  private long id = 0L;

  /** The name of the page.
   *
   * It is never null.
   */
  private String name = "";

  /** The title of the page.
  *
  * It is never null.
  */
 private String title = "";

  /** The content of the page.
   *
   * It is null for a new page.
   */
  private String content = null;

  /** The unpublished content of the page.
  *
  * It is null for a new page.
  */
  private String unpublishedContent = null;

  /** Specifies if the page has non published content.
   */
  private boolean isDirty = false;

  /** The name of the site, it is set at configuration time.
   *
   * It is never null.
   */
  private String siteName;

  /** Constructor, builds a ShowPageCommand.
   *
   * @param thePageRepository The page repository. It cannot be null.
   *
   * @param theSiteName The name of the site. It cannot be null.
   */
  public ShowPageCommand(final PageRepository thePageRepository,
      final String theSiteName) {
    Validate.notNull(thePageRepository, "The page repository cannot be null");
    Validate.notNull(theSiteName, "The site name cannot be null");
    pageRepository = thePageRepository;
    siteName = theSiteName;
  }

  /** Sets the page name, usually obtained from the request.
   *
   * @param thePageName the page name, it cannot be null.
   */
  public void setInstance(final String thePageName) {
    Validate.notNull(thePageName, "The page name cannot be null.");
    name = thePageName;
  }

  /** Gets the page id.
   *
   * @return Returns the page id.
   */
  public long getId() {
    return id;
  }

  /** Returns the page name.
   *
   * @return Returns the page name, the empty string for a new page. Never
   * returns null.
   */
  public String getName() {
    return name;
  }

  /** Returns the page title.
   *
   * @return Returns the page title, the empty string for a new page. Never
   * returns null.
   */
  public String getTitle() {
    return title;
  }

  /** Defines if the page has unpublished content.
   *
   * @return true if the page has unpublished content.
   */
  public boolean isDirty() {
    return isDirty;
  }

  /** Returns the page content.
   *
   * @return Returns the page content.
   */
  public String getContent() {
    return content;
  }

  /** Returns the page unpublished content.
  *
  * @return Returns the page unpublished content.
  */
 public String getUnpublishedContent() {
   return unpublishedContent;
 }

  /** Initializes this command from the specified page name.
   *
   * This loads the unpublished content from the page. If the page has no
   * unpublished content, it loads the page content.
   *
   * The page must exist, otherwise it throws an IllegalArgumentException.
   */
  public void init() {
    log.trace("Entering init");
    if (name == null) {
      throw new IllegalArgumentException("The page name was not specified");
    }
    Page page = pageRepository.findPageByName(siteName, name);
    if (page == null) {
      throw new IllegalArgumentException("The page " + name
          + " was not found");
    }
    log.debug("Found page {}", page.getName());
    id = page.getId();
    name = page.getName();
    title = page.getTitle();
    content = page.getContent();
    unpublishedContent = page.getUnpublishedContent();
    isDirty = page.isDirty();

    log.trace("Leaving init");
  }
}

