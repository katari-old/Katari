/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.sample.time.crud;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.trails.descriptor.annotation.PropertyDescriptor;

/** The project that users charge hours to.
 */
@Entity(name = "projects")
public class Project implements Serializable {

  /** The serial version number.
   */
  private static final long serialVersionUID = 1;

  /** The project id.
   */
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  @PropertyDescriptor(hidden = true)
  private long id = 0;

  /** The project name.
   */
  @PropertyDescriptor(index = 1)
  private String name;

  /** The client owner of the project.
   *
   * This is not null once the class in correctly initialized.
   */
  @ManyToOne
  @JoinColumn(name = "client_id", nullable = false)
  @PropertyDescriptor(index = 0)
  private Client client;

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

  public Client getClient() {
    return client;
  }

  public void setClient(final Client theClient) {
    client = theClient;
  }

  public String toString() {
    return name;
  }

  public boolean equals(final Object other) {
    if (other == null) {
      return false;
    }
    if (this == other) {
      return true;
    }
    if (!(other instanceof Project)) {
      return false;
    }

    Project otherProject = (Project) other;
    return new EqualsBuilder().append(name, otherProject.getName()).isEquals();
  }

  public int hashCode() {
    return new HashCodeBuilder().append(name).toHashCode();
  }
}

