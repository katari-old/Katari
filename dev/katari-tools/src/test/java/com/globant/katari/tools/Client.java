/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.tools;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/** The client that owns the project.
 */
@Entity(name = "clients")
public class Client {

  /** The client id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  /** The client name.
   */
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE },
      mappedBy = "client")
  private Set<Project> projects = new HashSet<Project>();

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

  public Set<Project> getProjects() {
    return projects;
  }

  public void setProjects(final Set<Project> theProjects) {
    projects = theProjects;
  }

  public String toString() {
    return name;
  }
}

