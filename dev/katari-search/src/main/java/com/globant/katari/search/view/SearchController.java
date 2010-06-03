/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.globant.katari.search.application.SearchCommand;
import com.globant.katari.search.domain.SearchResultElement;

/** The controller for the search module.
 *
 * It handles the search button in the search page.
 *
 * @author nira.amit@globant.com
 */
public abstract class SearchController extends AbstractCommandController {

  /** The class logger.
  */
  private static Log log = LogFactory.getLog(SearchController.class);

  /** Executes a full text search.
   *
   * This operation executes the full text search (delegating to the
   * SearchCommand execute operation), and forwards to the search view.
   *
   * @param request The HTTP request we are processing. It cannot be null.
   *
   * @param response The HTTP response we are creating. It cannot be null.
   *
   * @param command The populated command object. It cannot be null.
   *
   * @param errors Validation errors holder. It cannot be null.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return the ModelAndView for the next view, with the command (an instance
   * of SearchCommand) and the search results.
   */
  @Override
    protected ModelAndView handle(final HttpServletRequest request,
        final HttpServletResponse response, final Object command,
        final BindException errors) throws Exception {

    log.trace("Entering handleRequestInternal");

    SearchCommand searchCommand = (SearchCommand) command;
    Collection <SearchResultElement> results = searchCommand.execute();
    ModelAndView mav = new ModelAndView("search");
    mav.addObject("searchResults", results);
    mav.addObject("command", searchCommand);

    log.trace("Leaving handleRequestInternal");
    return mav;
    }

  /** Retrieves the SearchCommand as the form backing object.
   *
   * @param request The HTTP request we are processing. It cannot be null.
   *
   * @return The command bean object, an instance of SearchCommand, never null.
   */
  @Override
  protected Object getCommand(final HttpServletRequest request) {
    return createCommandBean();
  }

  /** This method is injected by spring.
   *
   * @return Returns the command bean injected.
   */
  protected abstract SearchCommand createCommandBean();
}

