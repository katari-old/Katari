package com.globant.katari.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/** POJO for testing purposes.
 */
@Entity
@Table(name = "foos")
public class Foo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @SuppressWarnings("unused")
  private long id = 0;

  /** Transient foo name.
   */
  @Transient
  private String name;

  /** Default hibernate constructor.
   */
  Foo() {}

  /** Returns the foo name.
   *
   * @return the foo name always null.
   */
  public String getName() {
    return name;
  }
}

