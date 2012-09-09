package com.globant.katari.ehcache.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/** Just a Hibernate entity for test purposes.
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
@Entity
@Table(name = "one_hibernate_entity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OneHibernateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private long id;

  @Column(name = "fieldA", nullable = false)
  private String fieldA;


  /** ORM constructor.*/
  OneHibernateEntity() {
  }

  /** Creates a new instance of the the mock entity.
   *
   * @param field the field.
   */
  OneHibernateEntity(final String field) {
    fieldA = field;
  }

  /** Retrieves the id.
   * @return the id.
   */
  public long getId() {
    return id;
  }

  /** Retrieves the fieldA.
   * @return the fieldA.
   */
  public String getFieldA() {
    return fieldA;
  }

}
