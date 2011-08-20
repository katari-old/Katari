/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

/** View resolver that, by default, uses katari's FreemarkerView class.
 *
 * This resolver is configured to use, by default, text/html in utf-8.
 *
 * The original freemarker view copies all model objects to the request, and
 * makes weblets fail. This resolver fixes this problem.
 */
public class FreeMarkerViewResolver extends
    org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver {

  /** FreeMarkerViewResolver constructor.
   */
  public FreeMarkerViewResolver() {
    setContentType("text/html; charset=utf-8");
  }

  /** Sets the view name to katari's FreemarkerView.
   */
  @Override
  protected Class<FreeMarkerView> requiredViewClass() {
    return FreeMarkerView.class;
  }
}

