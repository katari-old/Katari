/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.sample.time.crud;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.validator.NotNull;
import org.trails.descriptor.annotation.PropertyDescriptor;
import org.trails.validation.AssertNoOrphans;
import org.trails.validation.ValidateUniqueness;

/** The client that owns the project.
 */
@Entity(name = "clients")
@ValidateUniqueness(property = "name")
@AssertNoOrphans(childrenProperty = "projects",
    message = "You cannot remove a client that still has projects. Remove"
    + " projects first")

public class Client implements Serializable {

  /**
   * Status enumeration.
   */
  public enum Status {
    /**
     * The client is active.
     * Activities for a project of this client can be created.
     */
    ACTIVE,

    /**
     * The client is inactive.
     * No activities for any project of this client can be created.
     */
    INACTIVE
  }

  /** The serial version number.
   */
  private static final long serialVersionUID = 1;

  /** The client id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @PropertyDescriptor(hidden  = true)
  private Integer id;

  /** The client name.
   */
  @Column(name = "name", nullable = false, unique = true)
  @PropertyDescriptor(index  = 0)
  private String name;

  /** The client description.
   *
   * It is html content. It can be null when no description is available.
   */
  @Column(name = "description", nullable = true, unique = false)
  @PropertyDescriptor(richText  = true, index = 1)
  private String description = "";

  @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
      mappedBy = "client")
  @PropertyDescriptor(readOnly  = true, index = 2, searchable = false)
  // @Collection(child = true)
  private Set<Project> projects = new HashSet<Project>();

  /**
   * The client status.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @NotNull
  // @PropertyDescriptor(index = 3)
  private Status status = Status.ACTIVE;

  public Integer getId() {
    return id;
  }

  public void setId(final Integer theId) {
    id = theId;
  }

  public String getName() {
    return name;
  }

  public void setName(final String theName) {
    name = theName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String theDescription) {
    description = theDescription;
  }

  public Set<Project> getProjects() {
    return projects;
  }

  public void setProjects(final Set<Project> theProjects) {
    projects = theProjects;
  }

  public String toString() {
    return name;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(final Status theStatus) {
    status = theStatus;
  }
}

