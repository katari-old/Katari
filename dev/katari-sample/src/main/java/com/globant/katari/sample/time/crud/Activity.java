/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.time.crud;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.trails.descriptor.annotation.InitialValue;
import org.trails.descriptor.annotation.PossibleValues;
import org.trails.descriptor.annotation.PropertyDescriptor;

/** The activity that users report on a project.
 */
@Entity
@Table(name = "activities")
public class Activity {

  /** The activity id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @PropertyDescriptor(hidden = true)
  private long id;

  /** The activity name.
   */
  @Column(name = "name", nullable = false, unique = false)
  @PropertyDescriptor(index = 0)
  private String name;

  /**
   * Client of the project this activity belongs to.
   *
   * <p>
   * Example of PossibleValues annotation. Shows how to filter active clients
   * in the drop down.
   * </p>
   *
   * <p>
   * In the example ognl expression:<br>
   *
   * <strong>#spring</strong> provides a way to access the beans in the spring
   * context. #spring.bean['persistenceService'] returns the bean named
   * 'persistenceService' in the application context.<br>
   *
   * <strong>'@com.globant.katari.sample.time.crud.Client@class'</strong>
   * evaluates to com.globant.katari.sample.time.crud.Client.class (
   * '@class@method(args)' is the syntax for static calls).<br>
   *
   * <strong>clientCriteria</strong>
   * evaluates to a method call in root object, the current instance of this
   * class.
   *
   * @see {@link
   * http://www.ognl.org/2.6.9/Documentation/html/LanguageGuide/index.html}
   * @see {@link com.globant.katari.trails.SpringBeanProvider}
   */
  @InitialValue("project.client")
  @PossibleValues("#spring.bean['persistenceService']."
      + "getInstances(@com.globant.katari.sample.time.crud.Client@class,  "
      + "clientCriteria)")
  @Transient
  @PropertyDescriptor(index = 1, summary = false)
  private Client client;

  @PossibleValues("client.projects")
  @PropertyDescriptor(index = 2)
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

  /**
   * Returns a criteria that lists active clients.
   *
   * This criteria is * used as a parameter for
   * persistenceService.getInstances() call in '@PossibleValues'ognl
   * expression, annotated in client property.
   * @return DetachedCriteria, never returns null
   */
  @Transient
  @PropertyDescriptor(summary = false, hidden = true, searchable = false)
  public DetachedCriteria getClientCriteria() {
    DetachedCriteria criteria = DetachedCriteria.forClass(Client.class);
    criteria.add(Restrictions.eq("status", Client.Status.ACTIVE));
    return criteria;
  }
}

