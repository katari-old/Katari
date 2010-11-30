package com.globant.katari.registration.domain;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;

import com.globant.katari.user.domain.User;

/** This entity represents a transaction where the user request a new password.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
@Entity
@Table(name = "recover_password_request")
public class RecoverPasswordRequest {

  /** The token lenght. */
  private static final int TOKEN_LENGTH = 120;

  /** The id of forget password token.*/
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @SuppressWarnings("unused")
  private long id;

  /** The user assoc. with the token. It's never null. */
  @Column(name = "user_id", nullable = false)
  private long userId;

  /** The forgot password token. It's never null. */
  @Column(name = "token", nullable = false, length = TOKEN_LENGTH)
  private String token;

  /** The creation date of the token. */
  @Column(name = "creation_date", nullable = false)
  private Date creationDate;

  /** Hibernate constructor. */
  RecoverPasswordRequest() {
  }

  /**
   * Create a new instance of the forgot password token.
   *
   * @param theUser user assoc. to this token. Cannot be null.
   */
  public RecoverPasswordRequest(final User theUser) {
    Validate.notNull(theUser, "the user cannot be null");
    userId = theUser.getId();
    token = createToken();
    creationDate = new Date();
  }

  /**
   * This only should be used in a test case.
   * Create a new instance of the forgot password token.
   *
   * @param theUser user assoc. to this token. Cannot be null.
   * @param theCreationDate the creation date of the request.
   */
  RecoverPasswordRequest(final User theUser, final Date theCreationDate) {
    this(theUser);
    creationDate = theCreationDate;
  }

  /** Returns the userid.
   * @return the user assoc. with this forgot password token. Never null.
   */
  public long getUserId() {
    return userId;
  }

  /** Returns the stored token.
   * @return the token generated for this user. Never null.
   */
  public String getToken() {
    return token;
  }

  /** Returns the creation date.
   * @return the creation date of the token. Never null.
   */
  public Date getCreationDate() {
    return new Date(creationDate.getTime());
  }

  /**
   * Create a new recovery password token.
   * @return a unique token for the given user.
   */
  private String createToken() {
    try {
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
      int number = Math.abs(random.nextInt() * ((Number) userId).hashCode());
      return UUID.randomUUID().toString() + "_" + String.valueOf(number);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

}
