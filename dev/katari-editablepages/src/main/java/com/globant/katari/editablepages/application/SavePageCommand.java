/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.application;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.globant.katari.core.application.Command;
import com.globant.katari.core.application.Initializable;
import com.globant.katari.core.application.Validatable;

import com.globant.katari.editablepages.domain.Page;
import com.globant.katari.editablepages.domain.PageRepository;

/** Command that saves a page.
 *
 * The execution of this command saves a page into the page repository.
 */
public class SavePageCommand implements Command<String>, Validatable,
    Initializable {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(SavePageCommand.class);

  /** The page repository.
   */
  private PageRepository pageRepository;

  /** The page id, 0 for a new page.
   */
  private long id = 0L;

  /** The name of the page.
   *
   * It is never null.
   */
  private String name = "";

  /** The original name of the page, used to go to the original page if the
   * user cancels the edition.
   *
   * It is never null.
   */
  private String originalName = "";

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

  /** The name of the site, it is set in configuration time.
   *
   * It is never null.
   */
  private String siteName;

  /** Contructor, builds a SavePageCommand suitable for creating new pages.
   *
   * @param thePageRepository The page repository. It cannot be null.
   *
   * @param theSiteName The name of the site. It cannot be null.
   */
  public SavePageCommand(final PageRepository thePageRepository,
      final String theSiteName) {
    Validate.notNull(thePageRepository, "The page repository cannot be null");
    Validate.notNull(theSiteName, "The page site cannot be null");
    pageRepository = thePageRepository;
    siteName = theSiteName;
  }

  /** Gets the page id.
   *
   * @return Returns the page id, 0 for a new page.
   */
  public long getId() {
    return id;
  }

  /** Sets the page id, usually obtained from the request.
   *
   * @param theId the page id, it cannot be 0.
   */
  public void setId(final long theId) {
    id = theId;
  }

  /** Returns the page name.
   *
   * @return Returns the page name, the empty string for a new page. It is
   * never null.
   */
  public String getName() {
    return name;
  }

  /** Returns the page title.
   *
   * @return Returns the page title, the empty string for a new page. It is
   * never null.
   */
  public String getTitle() {
    return title;
  }

  /** Sets the page name.
   *
   * @param thePageName The page name, it cannot be null.
   */
  public void setName(final String thePageName) {
    Validate.notNull(thePageName, "The page name cannot be null.");
    name = thePageName;
  }

  /** Sets the page title.
   *
   * @param thePageTitle The page title, it cannot be null.
   */
 public void setTitle(final String thePageTitle) {
   Validate.notNull(thePageTitle, "The page title cannot be null.");
   title = thePageTitle;
 }

  /** Returns the original page name.
   *
   * @return Returns the page name, null for a new page.
   */
  public String getOriginalName() {
    return originalName;
  }

  /** Returns the page content.
   *
   * @return Returns the page content.
   */
  public String getPageContent() {
    return content;
  }

  /** Sets the page content.
   *
   * @param theContent The page content, it cannot be null.
   */
  public void setPageContent(final String theContent) {
    content = theContent;
  }

  /** Initializes this command form the specified page id.
   *
   * This loads the unpublished content from the page. If the page has no
   * unpublished content, it loads the page content.
   *
   * If id is not 0, then the page must exist, otherwise it throws an
   * IllegalArgumentException.
   */
  public void init() {
    log.trace("Entering init");
    if (id != 0) {
      Page page = pageRepository.findPage(id);
      if (page == null) {
        throw new IllegalArgumentException("The page name was not found");
      }
      name = page.getName();
      title = page.getTitle();
      originalName = name;
      if (page.isDirty()) {
        content = page.getUnpublishedContent();
      } else {
        content = page.getContent();
      }
    }

    log.trace("Leaving init");
  }

  /** Validates the page.
   *
   * The name and content cannot be empty. Dups are not allowed for page names.
   *
   * @param errors Contextual state about the validation process. It can not be
   * null.
   */
  public void validate(final Errors errors) {
    log.trace("Entering validate");

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pageContent",
        "required");

    Page page = pageRepository.findPageByName(siteName, name);
    boolean duplicateName = (page.getName().equals(name))
        && (id == 0 || page.getId() != id);
    if (duplicateName) {
      errors.rejectValue("name", "duplicate");
    }
    log.trace("Leaving validate");
  }

  /** Stores the modification of the page.
   *
   * @return It returns page being edited.
   */
  public String execute() {
    log.trace("Entering execute");

    Page page;
    if (id == 0) {
      page = new Page("some creator", name, title, content);
    } else {
      page = pageRepository.findPage(id);
      page.modify("some modificator", name, title, content);
    }

    pageRepository.save(siteName, page);

    log.trace("Leaving execute with {}", page.getName());
    return page.getName();
  }
}

