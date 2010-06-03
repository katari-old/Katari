/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain.mock;

import org.apache.commons.lang.Validate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SearchableProperty;

/** Defines a activity entity to be used for testing.
 */
@Entity
@Table(name = "activities_mock")
@Searchable
public class Activity {

  /** The id of the activity.
   *
   * This is 0 for a newly created activity.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @SearchableId
  private long id = 0;

  /** The name of the activity.
   */
  @Column(name = "name", nullable = false, length = 50)
  @SearchableProperty
  private String name;

  /** The default constructor.
   *
   * Builds an empty activity.
   */
  public Activity() {
  }

  /** A custom constructor.
   *
   * Builds a activity with the most basic data it needs to have.
   *
   * @param theName The activity name. It cannot be null.
   */
  public Activity(final String theName) {
    Validate.notNull(theName, "The activity name cannot be null");
    name = theName;
  }

  /** Modifies the values of the entity.
   *
   * @param newName The new name of the activity. It cannot be null.
   */
  public void modify(final String newName) {
    Validate.notNull(newName, "The activity name cannot be null");
    name = newName;
  }

  /** Returns the id of the activity.
   *
   * @return Returns the activity id, 0 if the activity was not persisted yet.
   */
  public long getId() {
    return id;
  }

  /** Returns the name of the activity.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }
}

