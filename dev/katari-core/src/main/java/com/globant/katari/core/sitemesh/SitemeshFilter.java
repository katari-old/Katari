/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.sitemesh;

import javax.servlet.FilterConfig;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.sitemesh.ContentProcessor;
import com.opensymphony.sitemesh.DecoratorSelector;
import com.opensymphony.sitemesh.compatability
  .DecoratorMapper2DecoratorSelector;
import com.opensymphony.sitemesh.compatability.PageParser2ContentProcessor;
import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import com.opensymphony.sitemesh.webapp.SiteMeshWebAppContext;

/** A sitemesh page filter that searches for the configuration files in the
 * classpath.
 */
public class SitemeshFilter extends SiteMeshFilter {

  /** The filter configuration.
   *
   * This is null until init is called and after destroy.
   */
  private FilterConfig filterConfig = null;

  /** Called by the servlet container to initialize the filter.
   *
   * @param theFilterConfig The filter configuration. It cannot be null.
   */
  public void init(final FilterConfig theFilterConfig) {
    super.init(theFilterConfig);
    filterConfig = theFilterConfig;
  }

  /** Called by the container when the filter is about to be destroyed.
   */
  public void destroy() {
    filterConfig = null;
  }

  /** Initializes the content processor.
   *
   * @param webAppContext The sitemesh wrapper for the web application context.
   * It cannot be null.
   *
   * @return the content processor. Never returns null.
   */
  protected ContentProcessor initContentProcessor(final SiteMeshWebAppContext
      webAppContext) {
    Factory factory = new SitemeshConfigFactory(new Config(filterConfig));
    factory.refresh();
    return new PageParser2ContentProcessor(factory);
  }

  /** Initializes the decorator selector.
   *
   * @param webAppContext The sitemesh wrapper for the web application context.
   * It cannot be null.
   *
   * @return the decorator selector. Never returns null.
   */
  protected DecoratorSelector initDecoratorSelector(final SiteMeshWebAppContext
      webAppContext) {
    Factory factory = new SitemeshConfigFactory(new Config(filterConfig));
    factory.refresh();
    return new DecoratorMapper2DecoratorSelector(factory.getDecoratorMapper());
  }
}

