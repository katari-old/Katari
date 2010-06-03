/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.application;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.core.application.Command;

import com.globant.katari.editablepages.domain.Page;
import com.globant.katari.editablepages.domain.PageRepository;

/** Command that saves a page.
 *
 * The execution of this command saves a page into the page repository.
 */
public class RevertPageCommand implements Command<String> {

  /** The class logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(RevertPageCommand.class);

  /** The page repository.
   *
   * It is never null.
   */
  private PageRepository pageRepository;

  /** The id of the page whose changes will be reverted.
   *
   * It cannot be 0.
   */
  private long id = 0L;

  /** The name of the site, it is set at configuration time.
   *
   * It is never null.
   */
  private String siteName;

  /** Constructor, builds a RevertPageCommand suitable for creating new pages.
   *
   * @param thePageRepository The page repository. It cannot be null.
   *
   * @param theSiteName The name of the site. It cannot be null nor empty.
   */
  public RevertPageCommand(final PageRepository thePageRepository,
      final String theSiteName) {
    Validate.notNull(thePageRepository, "The page repository cannot be null");
    Validate.notNull(theSiteName, "The site name cannot be null");
    Validate.notEmpty(theSiteName, "The site name cannot be empty");
    pageRepository = thePageRepository;
    siteName = theSiteName;
  }

  /** Gets the page id.
   *
   * @return Returns the page id, never 0.
   */
  public long getId() {
    return id;
  }

  /** Sets the page id, usually obtained from the request.
   *
   * @param theId the page id, it cannot be 0.
   */
  public void setId(final long theId) {
    Validate.notNull(theId);
    id = theId;
  }

  /** Reverts the page modifications.
   *
   * @return the published page name.
   */
  public String execute() {
    log.trace("Entering execute with page id {}", id);

    Page page;
    page = pageRepository.findPage(id);
    if (page == null) {
      throw new RuntimeException("The page was not found");
    }

    page.revert();

    pageRepository.save(siteName, page);

    log.trace("Leaving execute with page name {}", page.getName());

    return page.getName();
  }
}

