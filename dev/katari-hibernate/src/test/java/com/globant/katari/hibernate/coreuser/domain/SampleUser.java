/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/** Sample persisted user.
 */
@Entity
@DiscriminatorValue("user")
public class SampleUser extends CoreUser {

  /** Hibernate constructor.
   */
  SampleUser() {
  }

  public SampleUser(final String name) {
    super(name);
  }
}

