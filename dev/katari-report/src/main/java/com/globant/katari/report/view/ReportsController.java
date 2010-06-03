package com.globant.katari.report.view;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.globant.katari.report.application.ReportsCommand;
import com.globant.katari.report.domain.ReportDefinition;

/**
 * The controller that lists the reports definitions.
 *
 * @author sergio.sobek
 */
public abstract class ReportsController extends AbstractCommandController {

  /** The class logger. */
  private static Log logger = LogFactory.getLog(ReportsController.class);

  /**
   * It handles the view of the form.
   *
   * @param request the servlet request. This parameter is ignored.
   * @param response the servlet response. This parameter is ignored.
   * @param command the command object used in the form.
   * @param errors the errors. This parameter is ignored
   * @return the model and view. Never returns null.
   * @throws Exception when there's an error.
   */
  @Override
  protected final ModelAndView handle(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException errors) throws Exception {
    logger.trace("entering handle");

    ReportsCommand reportCommand = (ReportsCommand) command;
    List<ReportDefinition> results = reportCommand.execute();

    ModelAndView mav = new ModelAndView("reportsDefinitions");
    mav.addObject("reportsDefinitions", results);

    logger.trace("leaving handle");
    return mav;
  }

  /**
   * Returns the command associated with this controller.
   *
   * @param request the servlet request. This parameter is ignored.
   * @return the command associated with this controller. Never returns null.
   * @throws Exception when there's an error.
   */
  @Override
  protected final Object getCommand(final HttpServletRequest request)
      throws Exception {
    Object command = createCommandBean();
    Validate.notNull(command, "The command cannot be null.");
    return command;
  }

  /**
   * Creates the command associated with this controller.
   *
   * In the configuration file, this method has to be defined as a
   * lookup-method. This method is injected by spring framework AOP.
   *
   * @return the command. Never returns null.
   */
  protected abstract Object createCommandBean();

}
