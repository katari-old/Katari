/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

/** Defines a sample user entity.
 */
@Entity
@DiscriminatorValue("user")
public class SampleUser extends CoreUser {
  SampleUser() {
  }

  public SampleUser(final String theName) {
    super(theName);
  }
}

