/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.tools;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/** The project that users charge hours to.
 */
@Entity(name = "projects")
public class Project {

  /** The project id.
   */
  @Id @GeneratedValue(strategy = GenerationType.AUTO)
  private long id = 0;

  /** The project name.
   */
  private String name;

  /** The client owner of the project.
   *
   * This is not null once the class in correctly initialized.
   */
  @ManyToOne
  @JoinColumn(name = "client_id", nullable = false)
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
}

