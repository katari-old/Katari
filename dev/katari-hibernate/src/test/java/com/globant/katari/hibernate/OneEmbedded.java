package com.globant.katari.hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class OneEmbedded {

  @Column(name = "hola", nullable = true)
  private String hola = "lalalal";

  @Transient
  private transient String transientValue;

  public OneEmbedded(final String value) {
    transientValue = value;
  }

  /** Retrieves the transientValue.
   * @return the transientValue
   */
  public String getTransientValue() {
    return transientValue;
  }

}
