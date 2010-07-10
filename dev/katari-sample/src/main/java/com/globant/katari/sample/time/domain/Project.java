/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.sample.time.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;

/** Defines the Project entity.
 */
@Entity
@Table(name = "projects")
public class Project {

  /** The length in characters of the activity name.
   */
  private static final int NAME_LENGTH = 100;

  /** Project id, 0 for a newly created project.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id = 0;

  /** Project name.
   */
  @Column(name = "name", nullable = false, unique = false, length = NAME_LENGTH)
  private String name;

  /** Project's owner.
   */
  @SuppressWarnings("unused")
  @ManyToOne(targetEntity = Client.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  /** The default constructor.
   *
   * Builds an empty project.
   */
  protected Project() {
  }

  /** Creates an instance of Project.
   *
   * @param theName The name of the Project. Name cannot be null.
   *
   * @param theClient The Project's owner. Client cannot be null.
   */
  public Project(final String theName, final Client theClient) {
    Validate.notNull(theName, "the name cannot be null");
    Validate.notNull(theClient, "the client cannot be null");

    name = theName;
    client = theClient;
  }

  /** Returns the project id.
   *
   * @return project's id. 0 if the project has not been persisted yet.
   */
  public long getId() {
    return id;
  }

  /** Returns the project name.
   *
   * @return project's name.
   */
  public String getName() {
    return name;
  }
}


