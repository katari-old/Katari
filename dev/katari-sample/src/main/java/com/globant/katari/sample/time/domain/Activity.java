/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.sample.time.domain;

import org.apache.commons.lang.Validate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/** Defines the Activity entity.
 *
 * Activities are cross project tasks. The only way to create an activity is
 * through a repository. Activities are managed in the trails module.
 */
@Entity
@Table(name = "activities")
public class Activity {

  /** The length in characters of the activity name.
   */
  private static final int NAME_LENGTH = 100;

  /** Object id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id = 0;

  /** Activity name.
   *
   * It is never null.
   */
  @Column(name = "name", nullable = false, unique = false, length =
      NAME_LENGTH)
  private String name;

  /** The project to which this activity belongs.
  *
  * If null the activity applies to all projects.
  */
  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  /**
   * The default constructor.
   *
   * Creates an activity object that belongs to all projects.
   */
  public Activity() {
    super();
  }

  /** Creates an {@link Activity} object.
   *
   * @param theProject the project to which this activity belongs. It Cannot be
   * null.
   */
  public Activity(final Project theProject) {
    super();
    Validate.notNull(theProject, "The project cannot be null");
    project = theProject;
  }

  /** Returns the activity id.
   *
   * @return activity id, 0 for a not persisted object.
   */
  public long getId() {
    return id;
  }

  /** Returns the activity name.
   *
   * @return activity name. It never returns null..
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the project to which this activity belongs.
   * @return a {@link Project} object or null if the activity is applicable to
   * all projects.
   */
  public Project getProject() {
    return project;
  }
}

