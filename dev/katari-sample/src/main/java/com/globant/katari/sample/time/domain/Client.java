/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.sample.time.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;
import org.hibernate.validator.NotNull;

/**
 * Defines the Client entity.
 */
@Entity
@Table(name = "clients")
public class Client {

  /**
   * Status enumeration.
   */
  public enum Status {
    /**
     * The client is active. Activities for a project of this client can be
     * created.
     */
    ACTIVE,

    /**
     * The client is inactive. No activities for any project of this client can
     * be created.
     */
    INACTIVE
  }

  /** The length in characters of the client name.
   */
  private static final int NAME_LENGTH = 100;

  /** The length in characters of the client description.
   */
  private static final int DESCRIPTION_LENGTH = 100;

  /** The client id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id = 0;

  /** The client name.
   *
   * Two clients cannot have the same name. This is never null.
   */
  @Column(name = "name", nullable = false, unique = true, length = NAME_LENGTH)
  private String name = "";

  /** The client description.
   *
   * It is html content. It can be null when no description is available.
   */
  @Column(name = "description", nullable = true, unique = false, length =
      DESCRIPTION_LENGTH)
  private String description = "";

  /**
   * The client status.
   *
   * It is never null.
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @NotNull
  private Status status = Status.ACTIVE;

  /** The default constructor.
   *
   * Builds an empty client.
   */
  protected Client() {
  }

  /** Creates an instance of Client.
   *
   * @param theName The name of the Client. It cannot be null.
   */
  public Client(final String theName) {
    Validate.notNull(theName, "the name cannot be null");
    name = theName;
  }

  /** Returns the object id .
   *
   * @return object's id
   */
  public long getId() {
    return id;
  }
}

