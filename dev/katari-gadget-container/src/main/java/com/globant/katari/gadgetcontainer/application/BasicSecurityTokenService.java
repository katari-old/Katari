package com.globant.katari.gadgetcontainer.application;

import static com.globant.katari.gadgetcontainer.Utils.urlEncode;
import static org.apache.commons.lang.Validate.notEmpty;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.Validate;

import com.globant.katari.gadgetcontainer.domain.GadgetInstance;

/**
 * Base and simple implementation of a  security token service.
 * 
 * NOTE: DO NOT USE ME IN PRODUCTION!
 * 
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class BasicSecurityTokenService implements TokenService {
  /**
   * 
   */
  private final String domain;
  /**
   * 
   */
  private final String container;
  /**
   * Constructor
   * 
   * @param domain {@link String} the opensocial domain. Can not be empty.
   * @param container {@link String} the opensocial container. Can not be empty.
   */
  public BasicSecurityTokenService(final String domain, final String container){
    notEmpty(domain, "domain can not be empty");
    notEmpty(container, "container can not be empty");
    this.domain = domain;
    this.container = container;
  }

  public String createSecurityToken(final GadgetInstance instance) {
    Validate.notNull(instance, "gadget instance can not be null");
    StringBuffer sb = new StringBuffer();
    sb.append(urlEncode(instance.getUser()));
    sb.append(":");
    // TODO just for devel, and for non "profile" pages.
    sb.append(urlEncode(instance.getUser()));
    sb.append(":");
    sb.append(instance.getId());
    sb.append(":");
    sb.append(urlEncode(domain));
    sb.append(":");
    //TODO: villa here! xD
    sb.append(urlEncode(instance.getUrl().replaceAll(":", "%3A")));
    sb.append(":");
    sb.append(urlEncode("0"));
    sb.append(":");
    sb.append(urlEncode(container));
    return StringEscapeUtils.escapeHtml(sb.toString());
  }
}
