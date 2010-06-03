package com.globant.katari.report.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.globant.katari.report.application.RetrieveReportContentCommand;

/**
 * Controller to handle download operations on a single Report Definition.
 * @author gerardo.bercovich
 */
public abstract class DownloadReportDefinitionController extends
    AbstractCommandController {

  /**
   * Process the request and writes the report content on the servlet response.
   *
   * This controller configures the response for download the report xml as
   * file.
   * TODO this can be implemented on a custom View.
   * {@inheritDoc}
   */
  protected final ModelAndView handle(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException error) throws Exception {

    RetrieveReportContentCommand downloadCommand =
      (RetrieveReportContentCommand) command;

    byte[] reportContent = downloadCommand.execute();
    response.setContentType("text/xml");
    response.setHeader("Content-Disposition", "attachment; filename=\""
        + downloadCommand.getName() + ".jrxml\"");
    response.getOutputStream().write(reportContent);
    return null;
  }

  /**
   * Retrieve a backing object for the current form from the given request.
   *
   * @param request The HTTP request we are processing.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return The command bean object.
   */
  @Override
  protected Object getCommand(final HttpServletRequest request)
      throws Exception {
    return createCommandBean();
  }

  /**
   * This method is injected by AOP.
   *
   * @return Returns the command bean injected.
   */
  protected abstract RetrieveReportContentCommand createCommandBean();
}
