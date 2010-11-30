/**
 * 
 */
package com.globant.katari.registration.domain;

import java.util.Date;

import com.globant.katari.user.domain.User;

/**
 * This class builds RecoverPasswordRequest to mantein the package access
 * constructor.
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class RecoverPasswordRequestFactory {

  /**
   * Create a new instance of the RecoverPasswordRequest with the 
   * given creation date
   * 
   * @param user the user to use.
   * @param date the custom creation date.
   * @return a customized RecoverPasswordRequest.
   */
  public static RecoverPasswordRequest generate(final User user,
      final Date date) {
    return new RecoverPasswordRequest(user, date);
  }

}
