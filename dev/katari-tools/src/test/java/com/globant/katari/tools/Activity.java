/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.tools;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.persistence.Table;

/** The activity that users report on a project.
 */
@Entity
@Table(name = "activities")
public class Activity {

  /** The activity id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  /** The activity name.
   */
  @Column(name = "NAME", nullable = false, unique = false)
  private String name;

  @Transient
  private Client client;

  @ManyToOne
  @JoinColumn(name = "project_id")
  private Project project;

  public long getId() {
    return id;
  }

  public void setId(final long theId) {
    id = theId;
  }

  public String getName() {
    return name;
  }

  public void setName(final String theName) {
    name = theName;
  }

  public String toString() {
    return name;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(final Client theClient) {
    client = theClient;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(final Project theProject) {
    project = theProject;
  }
}

