/**
 * 
 */
package com.globant.katari.gadgetcontainer.domain;

import static org.acegisecurity.context.SecurityContextHolder.getContext;

import org.acegisecurity.context.SecurityContext;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO this class is hardcoded because it need data non harcoded inside
 * the shindig implementation.
 * 
 * This implementation should work together with the new katari User 
 * implementation. Basically, should extract the user id assosiated with
 * the current thread.
 * 
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class ContextUserService {
  
  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(ContextUserService.class);
  
  /** {@link SecurityContext} spring security context.
   */
  private final SecurityContext securityContext;
  
  /** Construct the object getting the context from the classpath, with the
   *  class {@link org.acegisecurity.context.SecurityContextHolder#getContext()}
   */
  public ContextUserService() {
    securityContext = getContext();
  }
  
  /**
   * @param theCecurityContext {@link SecurityContext}. Can not be null.
   */
  public ContextUserService(final SecurityContext theCecurityContext) {
    Validate.notNull(theCecurityContext, "SecurityContext can not be null.");
    securityContext = theCecurityContext;
  }
  
  /** Retrieves from the context the user, and then access throw reflextion
   * to his id.
   * 
   * @return {@link String} the userid.
   * @throws CanvasException if the operation can not be completed.
   */
  public String getCurrentUserId() {
    String userId = "john.doe";
    log.error("Using hardcoded implementation, returning user: " + userId);
    return userId;
  }
}
