/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.sitemesh;

import java.io.InputStream;

import java.util.Enumeration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.apache.commons.lang.Validate;

import com.opensymphony.module.sitemesh.factory.DefaultFactory;
import com.opensymphony.module.sitemesh.Config;

import com.globant.katari.core.web.ServletContextWrapper;

/** A sitemesh configuration factory that obtains the configuration file from
 * the classpath.
 */
public class SitemeshConfigFactory extends DefaultFactory {

  /** A servlet context that reimplements getResourceAsStream to obtain the
   * resources from the classpath.
   */
  private static class ClasspathContext extends ServletContextWrapper {

    /** Constructor.
     *
     * @param delegate The original context that we wrapped. It cannot be null.
     */
    public ClasspathContext(final ServletContext delegate) {
      super(delegate);
    }

    /** Obtains a resource, trying the servlet context and the current
     * classloader.
     *
     * @param path The path to get the resource from. It cannot be null.
     *
     * @return the input stream for the specified path, or null if the path was
     * not found.
     */
    public InputStream getResourceAsStream(final String path) {
      Validate.notNull(path, "The path cannot be null");
      InputStream is = getDelegate().getResourceAsStream(path);
      if (is == null) {
        is = getClass().getResourceAsStream(path);
      }
      if (is == null) {
        is = getClass().getResourceAsStream("/" + path);
      }
      return is;
    }

    /** This implementation always returns null.
     *
     * @param path The path to get the resource from. It is ignored.
     *
     * @return always null.
     */
    public String getRealPath(final String path) {
      return null;
    }
  }

  /** A filter config implementation that does nothing.
   */
  private static class DummyFilterConfig implements FilterConfig {

    /** This implementation does nothing.
     *
     * {@inheritDoc}
     */
    public String getFilterName() {
      return null;
    }

    /** This implementation does nothing.
     *
     * {@inheritDoc}
     */
    public ServletContext getServletContext() {
      return null;
    }

    /** This implementation does nothing.
     *
     * {@inheritDoc}
     */
    public String getInitParameter(final String name) {
      return null;
    }

    /** This implementation does nothing.
     *
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Enumeration getInitParameterNames() {
      return null;
    }
  }

  /** Constructor.
   *
   * @param config The filter configuration where the factory reads the
   * parameters from. It cannot be null.
   */
  public SitemeshConfigFactory(final Config config) {

    /*
    final ServletContext context =
      new ServletContextWrapper(config.getServletContext()) {
        public InputStream getResourceAsStream(final String path) {
          InputStream is = getDelegate().getResourceAsStream(path);
          if (is == null) {
            is = getClass().getResourceAsStream(path);
          }
          if (is == null) {
            is = getClass().getResourceAsStream("/" + path);
          }
          return is;
        }
        public String getRealPath(final String path) {
          return null;
        }
      };

    FilterConfig filterConfig = new FilterConfig() {
      public String getFilterName() {
        return filterConfig.getFilterName();
      }
      public ServletContext getServletContext() {
        return context;
      }
      public String getInitParameter(final String name) {
        return filterMapping.getParameters().get(name);
      }
      public Enumeration getInitParameterNames() {
        return IteratorUtils.asEnumeration(
            filterMapping.getParameters().values().iterator());
      }
    };
    */

    super(new Config(new DummyFilterConfig()) {
      public ServletContext getServletContext() {
        return new ClasspathContext(config.getServletContext());
      }
      public String getConfigFile() {
        return config.getConfigFile();
      }
    });

    /*
    super(new Config(new FilterConfig() {
      public String getFilterName() {
        return null;
      }
      public ServletContext getServletContext() {
        return null;
      }
      public String getInitParameter(final String name) {
        return null;
      }
      public Enumeration getInitParameterNames() {
        return null;
      }
    }) {

      public ServletContext getServletContext() {
        return new ServletContextWrapper(config.getServletContext()) {
          public InputStream getResourceAsStream(final String path) {
            InputStream is = getDelegate().getResourceAsStream(path);
            if (is == null) {
              is = getClass().getResourceAsStream(path);
            }
            if (is == null) {
              is = getClass().getResourceAsStream("/" + path);
            }
            return is;
          }
          public String getRealPath(final String path) {
            return null;
          }
        };
      }
      public String getConfigFile() {
        return config.getConfigFile();
      }
    });
    */
  }
}

