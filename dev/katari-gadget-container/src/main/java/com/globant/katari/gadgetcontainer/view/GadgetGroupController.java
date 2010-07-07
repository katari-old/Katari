/**
 * 
 */
package com.globant.katari.gadgetcontainer.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

import com.globant.katari.core.application.Command;
import com.globant.katari.gadgetcontainer.application.GadgetGroupCommand;
import com.globant.katari.gadgetcontainer.domain.GadgetGroup;
import com.google.gson.Gson;

/**
 * Retrieve the representation for the requested {@link GadgetGroup}
 * 
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 * 
 */
public abstract class GadgetGroupController extends AbstractCommandController {
  
  /** Write directly on the servlet output the json representation of
   * the requested page.
   * 
   * Note: This controller always returns null, because write directly
   * to the response.
   * 
   * @see org.springframework.web.servlet.mvc.AbstractCommandController
   *      #handle(
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse, 
   *      java.lang.Object,
   *      org.springframework.validation.BindException)
   */
  @Override
  protected ModelAndView handle(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException errors) throws Exception {
    Gson gson = new Gson();
    GadgetGroupCommand gadgetGroupCommand = (GadgetGroupCommand) command;
    GadgetGroup page = gadgetGroupCommand.execute();
    response.addHeader("Content-type", "application/json");
    response.getWriter().write(gson.toJson(page));
    response.getWriter().close();
    return null;
  }
  
  /** @return {@link Command<GadgetGroup>}
   */
  protected abstract Command<GadgetGroup> createCommandBean();
  
  /** Create a new {@link Command} instance.
   * 
   * @see org.springframework.web.servlet.mvc.BaseCommandController#getCommand(
   *  javax.servlet.http.HttpServletRequest)
   */
  @Override
  protected Object getCommand(final HttpServletRequest request)
    throws Exception {
    return createCommandBean();
  }
}
