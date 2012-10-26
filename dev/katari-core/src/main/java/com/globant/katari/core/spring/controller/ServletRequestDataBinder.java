package com.globant.katari.core.spring.controller;

import java.util.List;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyEditor;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/** Special {@link org.springframework.validation.DataBinder} to perform
 * data binding from servlet request parameters to JavaBeans,
 * including support for multipart files.
 *
 * Also, includes support for {@link HttpServletRequestAware} for editors that
 * implements that interface.
 *
 * @see org.springframework.web.bind.ServletRequestDataBinder
 *
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class ServletRequestDataBinder
  extends org.springframework.web.bind.ServletRequestDataBinder {

  /** The class logger.*/
  private static Logger log = LoggerFactory
      .getLogger(ServletRequestDataBinder.class);

  /** The default command name, for binding purposes.*/
  public static final String DEFAULT_COMMAND_NAME = "command";

  /** The HTTP Servlet request, it's never null. */
  private final HttpServletRequest httpServletRequest;

  /** The HTTP Servlet response, it's never null.*/
  private final HttpServletResponse httpServletResponse;

  /** The property mapper, it's never null. */
  private final ServletRequestPropertyMapper propertyMapper;

  /** The list of property editor factory, it can be null.*/
  private final List<PropertyEditorBinder> propertyEditorBinder;

  /** Creates a new instance of the Servlet data binder.
   *
   * @param target the target object, cannot be null.
   * @param request the current request, cannot be null.
   * @param response the current response, cannot be null.
   * @param mapper the property mapper, cannot be null.
   * @param propertyEditors the list of property editors, can be null.
   */
  public ServletRequestDataBinder(final Object target,
      final HttpServletRequest request, final HttpServletResponse response,
      final ServletRequestPropertyMapper mapper,
      final List<PropertyEditorBinder> propertyEditors) {

    super(target, DEFAULT_COMMAND_NAME);

    Validate.notNull(request, "The mapper cannot be null");
    Validate.notNull(response, "The mapper cannot be null");
    Validate.notNull(mapper, "The mapper cannot be null");

    httpServletRequest = request;
    httpServletResponse = response;
    propertyMapper = mapper;
    propertyEditorBinder = propertyEditors;
  }

  /**
   * Bind the parameters of the given request to this binder's target, also
   * binding multipart files in case of a multipart request.
   * <p>
   * This call can create field errors, representing basic binding errors like a
   * required field (code "required"), or type mismatch between value and bean
   * property (code "typeMismatch").
   * <p>
   * Multipart files are bound via their parameter name, just like normal HTTP
   * parameters: i.e. "uploadedFile" to an "uploadedFile" bean property,
   * invoking a "setUploadedFile" setter method.
   * <p>
   * The type of the target property for a multipart file can be MultipartFile,
   * byte[], or String. The latter two receive the contents of the uploaded
   * file; all metadata like original file name, content type, etc are lost in
   * those cases.
   *
   * @param request request with parameters to bind (can be multipart)
   * @see org.springframework.web.multipart.MultipartHttpServletRequest
   * @see org.springframework.web.multipart.MultipartFile
   * @see #bindMultipartFiles
   * @see #bind(org.springframework.beans.PropertyValues)
   */
  public void bind(final ServletRequest request) {
    log.trace("Entering bind");

    if (propertyEditorBinder != null) {
      log.debug("There are property editors");
      for (PropertyEditorBinder binder : propertyEditorBinder) {
        log.debug("Registering: " + binder.toString());
        binder.register(this);
      }
    }

    MutablePropertyValues mpvs;
    mpvs = new MutablePropertyValues(propertyMapper.createPropertyMap(
        httpServletRequest, httpServletResponse));

    if (request instanceof MultipartRequest) {
      MultipartRequest multipartRequest = (MultipartRequest) request;
      bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
    }

    doBind(mpvs);
    log.trace("Leaving bind");
  }

  /** Retrieves the HTTP Servlet request.
   * @return the Servlet request, never null.
   */
  public HttpServletRequest getHttpServletRequest() {
    return httpServletRequest;
  }

  /** Retrieves the HTTP Servlet response.
   * @return the Servlet response, never null.
   */
  public HttpServletResponse getHttpServletResponse() {
    return httpServletResponse;
  }

  /** {@inheritDoc} . */
  @Override
  @SuppressWarnings("rawtypes")
  public void registerCustomEditor(final Class requiredType,
      final PropertyEditor propertyEditor) {
    super.registerCustomEditor(requiredType, propertyEditor);
  }

  /** {@inheritDoc} . */
  @Override
  @SuppressWarnings("rawtypes")
  public void registerCustomEditor(final Class requiredType, final String field,
      final PropertyEditor propertyEditor) {
    super.registerCustomEditor(requiredType, field, propertyEditor);
  }

}
