/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang.Validate;

/** A social application, mainly represented by the gadget xml url.
 *
 * This id of this class is used as the open social appId.
 */
@Entity
@Table(name = "applications")
public class Application {

  /** The id of the application, 0 for a newly created one.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  /** {@link String} that identifies the url of the gadget xml spec.
   *
   * It is never null.
   */
  @Column(nullable = false)
  private String url;

  /** Hibernate constructor.
   */
  Application() {
  }

  /** Creates a new application.
   *
   * @param gadgetUrl with the url of the gadget xml. It cannot be empty.
   */
  public Application(final String gadgetUrl) {
    Validate.notEmpty(gadgetUrl, "gadget url can not be empty");
    url = gadgetUrl;
  }

  /** @return long the id of the gadget instance.
   */
  public long getId() {
    return id;
  }

  /** @return {@link String} location of the gadget xml spec.
   */
  public String getUrl() {
    return url;
  }
}

