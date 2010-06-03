package com.globant.katari.console.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Simple home controller for the scripting console.
 * @author juan.pereyra@globant.com
 */
public class HomeController extends AbstractController {

  @Override
  protected ModelAndView handleRequestInternal(final HttpServletRequest
      request, final HttpServletResponse response) {
    return new ModelAndView("home");
  }

}
