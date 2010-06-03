/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.globant.katari.editablepages.domain.PageRepository;
import com.globant.katari.editablepages.domain.Page;

/** Controller that shows pages that can be edited by the user.
 *
 * This should be mapped to the /page/* request. It obtains the page name from
 * the component after /page/.
 */
public class PageController extends AbstractController {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(PageController.class);

  /** The length of /page/ string.
   */
  private static final int PAGE_PREFIX_LENGTH = 6;

  /** The page repository.
   *
   * It is never null.
   */
  private PageRepository pageRepository;

  /** The name of the site, it is set in configuration time.
   *
   * It is never null.
   */
  private String siteName;

  /** Builds a PageController instance.
   *
   * @param thePageRepository the page repository, it cannot be null.
   *
   * @param theSiteName the name of the site, it cannot be null.
   */
  public PageController(final PageRepository thePageRepository, final String
      theSiteName) {
    Validate.notNull(thePageRepository, "The page repository cannot be null.");
    Validate.notNull(theSiteName, "The site name cannot be null.");
    pageRepository = thePageRepository;
    siteName = theSiteName;
  }

  /** Finds a page from the domain.
   *
   * The page name is obtained from the request uri. It renders the view called
   * 'page' with the page object as a model parameter and the list of allowed
   * actions.
   *
   * @param request The HTTP request we are processing.
   *
   * @param response The HTTP response we are creating.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return the ModelAndView for the view named 'page'.
   */
  protected final ModelAndView handleRequestInternal(final HttpServletRequest
      request, final HttpServletResponse response) throws Exception {
    log.trace("Entering handleRequestInternal");

    String pathInfo = request.getPathInfo();
    log.debug("pathInfo: {}", pathInfo);
    Validate.notNull(pathInfo, "The path info cannot be null");
    Validate.isTrue(pathInfo.matches("/page/.+"));

    // Strip the '/page/' prefix from the pathinfo to obtain the page name.
    String pageName = pathInfo.substring(PAGE_PREFIX_LENGTH);
    Page page = pageRepository.findPageByName(siteName, pageName);
    ModelAndView mav = new ModelAndView("page");
    if (null != page) {
      // We found a page, but it may have never been published.
      if (page.getContent() == null) {
        throw new RuntimeException("Page not found: " + pageName);
      }
      // We found the page, prepare the edition toolbar.
      mav.addObject("page", page);
      mav.addObject("request", request);
      mav.addObject("baseweb", request.getAttribute("baseweb"));

      // Increments the elementId from the request.
      Long elementId = (Long) request.getAttribute("elementId");
      if (elementId == null) {
        elementId = Long.valueOf(1);
      } else {
        elementId++;
      }
      request.setAttribute("elementId", elementId);

      mav.addObject("elementId", elementId);

    } else {
      // TODO The page was not found. See what to do next. It is probably a
      // good idea to redirect to the creation page if the user has the
      // privileges.
      throw new RuntimeException("Page not found: " + pageName);
    }

    log.trace("Leaving handleRequestInternal");
    return mav;
  }
}

