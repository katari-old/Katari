package com.globant.katari.core.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.lang.Validate;


/**
 * A servlet context wrapper that implements scoped attributes and init
 * parameters.
 * <p>
 * This servlet context implementation holds a map of local attributes and
 * local init parameters. When the {@link #setAttribute(String, Object)}
 * method is called, the attribute is stored on the local map.
 * <p>
 * When the {@link #getAttribute(String)} method is called, the local map is
 * queried first. If a value is found in the local map, it is returned.
 * Otherwise, the delegate's {@link #getAttribute(String)} method is called.
 * <p>
 * The init parameters of this servlet context may be added by calling the
 * {@link #addInitParalmeter(String, String)} method, but the wrapped servlet
 * context parameters will not be queried.
 * @see ServletContext
 * @see ServletContextWrapper
 * @author pablo.saavedra
 */
class ScopedServletContext extends ServletContextWrapper {

  /**
   * The local attributes map.
   */
  private Map<String, Object> localAttributes;

  /**
   * The local initialization parameters map.
   */
  private Map<String, String> localInitParameters;

  /**
   * Creates a new {@link ScopedServletContext}, wrapping the given
   * {@link ServletContext}.
   * @param theDelegate
   *          The servlet context to wrap, cannot be null.
   */
  public ScopedServletContext(final ServletContext theDelegate) {
    super(theDelegate);
    localAttributes = new HashMap<String, Object>();
    localInitParameters = new HashMap<String, String>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAttribute(final String name) {
    if (localAttributes.containsKey(name)) {
      return localAttributes.get(name);
    }
    return super.getAttribute(name);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public Enumeration<String> getAttributeNames() {
    Enumeration<String> attributes = super.getAttributeNames();
    Set<String> attrList = new HashSet<String>(Collections
        .list(attributes));
    attrList.addAll(localAttributes.keySet());
    return Collections.enumeration(attrList);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeAttribute(final String name) {
    if (localAttributes.containsKey(name)) {
      localAttributes.remove(name);
      return;
    }
    super.removeAttribute(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAttribute(final String name, final Object object) {
    localAttributes.put(name, object);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getInitParameter(final String name) {
    return localInitParameters.get(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Enumeration<String> getInitParameterNames() {
    return Collections.enumeration(localInitParameters.keySet());
  }

  /**
   * Adds an init parameters to the servlet context.
   * @param key
   *          The parameter key, cannot be null.
   * @param value
   *          The parameter value, cannot be null.
   */
  public void addInitParameter(final String key, final String value) {
    Validate.notNull(key, "The key cannot be null");
    Validate.notNull(value, "The value cannot be null");
    localInitParameters.put(key, value);
  }
}

