/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.core.application.JsonRepresentation;

import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/** A Freemarker JsonRepresentation wrapper to access the Json objects from
 * freemarker.
 *
 * TODO: See if we should call setSimpleMapWrapper(true) to support non-string
 * keys in freemarker.
 */
public final class JsonRepresentationWrapper extends DefaultObjectWrapper {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      JsonRepresentationWrapper.class);

  /** {@inheritDoc}
   */
  public TemplateModel wrap(final Object object)
      throws TemplateModelException {
    log.trace("Entering wrap");
    TemplateModel model;
    if (object instanceof JsonRepresentation) {
      model = new JsonRepresentationModel((JsonRepresentation) object, this);
      log.trace("Leaving wrap with JsonRepresentationWrapper");
    } else {
      model = super.wrap(object);
      log.trace("Leaving wrap with a default wrapper");
    }
    return model;
  }
}

