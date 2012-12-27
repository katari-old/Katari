package com.globant.katari.core.spring.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.globant.katari.core.application.Initializable;

/**
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class ReferenceDataParameterProcessor implements ParameterProcessor {

  /** The index in the request parameter name of the path of the object to
   * bind.
   *
   * For example, in R-country-countries-id, this index references the position
   * of 'country'.
   */
  private static final int PATH_INDEX = 1;

  /** The index in the request parameter name of the path of the collection of
   * objects in the reference data that contains the value to bind.
   *
   * For example, in R-country-countries-id, this index references the position
   * of 'countries'.
   */
  private static final int DATA_PATH_INDEX = 2;

  /** The index in the request parameter name of the name of the property to
   * match to the request parameter value.
   *
   * For example, in R-country-countries-id, this index references the position
   * of 'id'.
   */
  private static final int KEY_PROPERTY_INDEX = 3;

  /** {@inheritDoc}. */
  public void process(final HttpServletRequest request,
      final HttpServletResponse response,
      final Map<String, Object> parameters) {
    Initializable referenceData;
    referenceData = (Initializable) request.getAttribute(
        SimpleFormController.REFERENCE_DATA_PARAMETER);
    if (referenceData != null) {
      BeanWrapper referenceDataWrapper = new BeanWrapperImpl(referenceData);
      @SuppressWarnings("unchecked")
      Enumeration<String> requestParameters = request.getParameterNames();
      while (requestParameters.hasMoreElements()) {
        String theParameter = requestParameters.nextElement();
        if (theParameter.startsWith("R-")) {
          addPropertyValue(theParameter, referenceDataWrapper, request,
              parameters);
        }
      }
    }
  }

  /** Adds into the parameter map values related to the reference data.
   * @param theParameter the parameter name.
   * @param wrapper the reference data wraper.
   * @param request the current request.
   * @param parameters the map of parameters.
   */
  private void addPropertyValue(final String theParameter,
      final BeanWrapper wrapper,
      final HttpServletRequest request,
      final Map<String, Object> parameters) {

    Object value = findParameter(request, theParameter);

    String[] components = theParameter.split("-");
    String path = components[PATH_INDEX];
    String referenceDataPath = components[DATA_PATH_INDEX];
    String property = components[KEY_PROPERTY_INDEX];

    Object source = wrapper.getPropertyValue(referenceDataPath);

    Collection<?> sourceObjects;
    if (source.getClass().isArray()) {
      sourceObjects = Arrays.asList((Object[]) source);
    } else {
      sourceObjects = (Collection<?>) source;
    }

    if (value.getClass().isArray()) {
      Object[] ids = (Object[]) value;
      List<Object> values = new LinkedList<Object>();
      for (int i = 0; i < ids.length; ++i) {
        Object object = findObjectForProperty(wrapper, sourceObjects,
            property, ids[i]);
        values.add(object);
      }
      parameters.put(path, values);
    } else {
      Object object = findObjectForProperty(wrapper, sourceObjects,
          property, value);
      parameters.put(path, object);
    }
  }

  /** Finds an object in the collection with the specified value in the
   * specified property.
   *
   * @param objects the collection of objects where the necessary object is
   * found. It cannot be null.
   *
   * @param propertyName the name of the property to match. It cannot be null.
   *
   * @param value the value of the named property. It cannot be null.
   *
   * @return the object in the collection with the value in the specified
   * property, or null if not found.
   */
  private Object findObjectForProperty(final BeanWrapper referenceDataWrapper,
      final Collection<?> objects, final String propertyName,
      final Object value) {
    for (Object object : objects) {
      Object objectId;
      objectId = new BeanWrapperImpl(object).getPropertyValue(propertyName);
      Object id;
      id = referenceDataWrapper.convertIfNecessary(value, objectId.getClass());
      if (objectId.equals(id)) {
        return object;
      }
    }
    return null;
  }

  /** Finds the value for the given property name.
   * @param request the current http servlet request.
   * @param parameterName the parameter name.
   * @return the object related to the given property.
   */
  private Object findParameter(final HttpServletRequest request,
      final String parameterName) {
    Object[] parameters = request.getParameterValues(parameterName);
    if (parameters != null) {
      if (parameters.length > 1) {
        return parameters;
      } else {
        return parameters[0];
      }
    }
    return parameters;
  }
}
