package com.globant.katari.core.spring.controller;

import java.beans.PropertyEditorSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/** Factory class (binder) for the spring Property Editor Support.
 *
 * Should we call it Factory? Binder? Mapper? ideas...?
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class PropertyEditorBinder implements ApplicationContextAware,
    InitializingBean {

  /** The class logger.*/
  private static Logger log =
      LoggerFactory.getLogger(PropertyEditorBinder.class);

  /** The target class of the bean, It's never null. */
  private Class<?> targetClass;

  /** The target field of the bean, Can be null. */
  private String targetField;

  /** The property editor class, It's never null. */
  private Class<PropertyEditorSupport> propertyEditorClass;

  /** The property editor bean name, It's never null. */
  private String propertyEditorBeanName;

  /** The spring application context, It's never null. */
  private ApplicationContext applicationContext;

  /** {@inheritDoc} .*/
  public void afterPropertiesSet() throws Exception {
    Validate.notNull(targetClass, "The target class cannot be null");

    if (StringUtils.isBlank(propertyEditorBeanName)
        && propertyEditorClass == null) {
      throw new IllegalArgumentException("please define at least one strategy,"
        + "setting the propertyEditorBeanName or propertyEditorClass");
    }

    if (propertyEditorClass != null) {
      boolean isAssignable = PropertyEditorSupport.class.isAssignableFrom(
          propertyEditorClass);
      Validate.isTrue(isAssignable,
          "The given Property editor class does not extends from "
              + "PropertyEditorSupport.class");
    }
  }

  /** Attaches to the given binder the property editor.
   * @param binder the binder to attach the property editor. Cannot be null.
   */
  public void register(final ServletRequestDataBinder binder) {
    Validate.notNull(binder, "The binder cannot be null");
    PropertyEditorSupport editor = null;
    try {

      if (StringUtils.isBlank(propertyEditorBeanName)) {
        editor = propertyEditorClass.newInstance();
      } else {
        editor = (PropertyEditorSupport) applicationContext.getBean(
            propertyEditorBeanName);
        Validate.notNull(editor, "Could not find the bean named:"
            + propertyEditorBeanName);
      }

      if (editor instanceof DataBinderHttpServletRequestAware) {
        ((DataBinderHttpServletRequestAware) editor)
          .setHttpServletRequest(binder.getHttpServletRequest());
      }

      if (editor instanceof DataBinderHttpServletResponseAware) {
        ((DataBinderHttpServletResponseAware) editor)
            .setHttpServletResponse(binder.getHttpServletResponse());
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    log.debug("creating binder for: [ "
        + "target-field: " + targetField + ", "
        + "target-class: " + targetClass.getName() + ", "
        + "editor-class: " + editor.getClass().getName()
        + " ]");

    if (targetField == null) {
      binder.registerCustomEditor(targetClass, editor);
    } else {
      binder.registerCustomEditor(targetClass, targetField, editor);
    }
  }

  /** {@inheritDoc}. */
  public void setApplicationContext(final ApplicationContext context) {
    applicationContext = context;
  }

  /** Sets the target class.
   * @param klass the target class to set.
   */
  public void setTargetClass(final Class<?> klass) {
    if (targetClass != null) {
      throw new IllegalStateException("Cannot modify the target class");
    }
    targetClass = klass;
  }

  /** Sets the target field.
   * @param field the target field to set.
   */
  public void setTargetField(final String field) {
    if (targetField != null) {
      throw new IllegalStateException("Cannot modify the target field");
    }
    targetField = field;
  }

  /** Sets the property editor class.
   * @param editorClass the property editor class to set.
   */
  public void setPropertyEditorClass(
      final Class<PropertyEditorSupport> editorClass) {
    if (propertyEditorClass != null) {
      throw new IllegalStateException(
          "Cannot modify the property editor class");
    }
    propertyEditorClass = editorClass;
  }

  /** Sets the property editor bean name.
   * @param editorBeanName the property editor bean name to set.
   */
  public void setPropertyEditorBeanName(final String editorBeanName) {
    if (propertyEditorBeanName != null) {
      throw new IllegalStateException(
          "Cannot modify the property editor bean name");
    }
    propertyEditorBeanName = editorBeanName;
  }

  /** {@inheritDoc}.*/
  @Override
  public String toString() {
    return "Propery Editor binder for:" + targetClass.getName();
  }

  /** Interface to be implemented by any object that needs the
   * HttpServletRequest.
   */
  public static interface DataBinderHttpServletRequestAware {

    /** Sets the HttpServlet Request.
     * @param request the HTTP servlet request.
     */
    void setHttpServletRequest(final HttpServletRequest request);

  }

  /** Interface to be implemented by any object that needs the
   * HttpServletResponse.
   */
  public static interface DataBinderHttpServletResponseAware {

    /** Sets the HttpServlet Response.
     * @param response the HTTP servlet response.
     */
    void setHttpServletResponse(final HttpServletResponse response);

  }
}
