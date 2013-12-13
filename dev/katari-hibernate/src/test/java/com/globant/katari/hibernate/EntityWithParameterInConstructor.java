package com.globant.katari.hibernate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/** POJO for testing purposes.
 * <p>A spring bean will be used to instantiate this object.</p>
 */
@Entity
@Table(name = "entities_with_parameter_in_constructor")
public class EntityWithParameterInConstructor {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @SuppressWarnings("unused")
  private long id = 0;

  @Embedded
  private OneEmbedded oneEmbedded = new OneEmbedded("");

  /** Transient name.
   */
  @Transient
  private String name;

  /** Constructor to be used by spring.
   *
   * @param theName name, it cannot be null.
   */
  EntityWithParameterInConstructor(final String theName) {
    name = theName;
  }

  /** Returns the name.
   *
   * @return the name, never null.
   */
  public String getName() {
    return name;
  }

  /** Retrieves the oneEmbedded.
   * @return the oneEmbedded
   */
  public OneEmbedded getOneEmbedded() {
    return oneEmbedded;
  }

}

