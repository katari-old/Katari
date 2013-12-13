package com.globant.katari.hibernate;

import javax.persistence.Column;
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
@Table(name = "bean_defined_foos")
public class BeanDefinedFoo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @SuppressWarnings("unused")
  private long id = 0;

  /** Transient foo name.
   */
  @Transient
  private String name;

  /** Constructor to be used by spring.
   *
   * @param theName foo name
   */
  BeanDefinedFoo(final String theName) {
    name = theName;
  }

  /** Returns the foo name.
   *
   * @return the foo name, never null.
   */
  public String getName() {
    return name;
  }
}

