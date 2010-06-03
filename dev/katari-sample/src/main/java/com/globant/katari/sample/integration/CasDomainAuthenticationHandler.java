/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.integration;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import org.apache.commons.lang.Validate;
//
//import org.jasig.cas.authentication.principal.Credentials;
//import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
//import org.jasig.cas.authentication.handler.AuthenticationHandler;
//
//import com.globant.katari.sample.user.domain.User;
//import com.globant.katari.sample.user.domain.UserRepository;

// Sample class to integrate cas server into an katari webapp. Commented out to
// avoid bringing cas dependencies to every katari app.

/** A cas authentication handler that validates the username and password
 * through the application domain.
 *
 * This authentication handler obtains a user from the domain and validates its
 * password.
 */
public class CasDomainAuthenticationHandler
  /* implements AuthenticationHandler */ {

///** The class logger.
// */
//private static Log log =
//  LogFactory.getLog(CasDomainAuthenticationHandler.class);

///** The user repository used to get the domain user.
// *
// * It is never null.
// */
//private UserRepository repository;

///** Builds an instance of this authentication handler.
// *
// * @param theUserRepository The user repository to get the domain user from.
// * It cannot be null.
// */
//CasDomainAuthenticationHandler(final UserRepository theUserRepository) {
//  Validate.notNull(theUserRepository, "The user repository cannot be null");
//  repository = theUserRepository;
//}

///** Authenticates the provided credentials.
// *
// * @param credentials The credentials to authenticate. This cannot be null.
// *
// * @return true if the user was correctly authenticated, false otherwise.
// */
//public boolean authenticate(final Credentials credentials) {

//  log.trace("Entering authenticate");

//  Validate.notNull(credentials, "The credentials cannot be null");

//  UsernamePasswordCredentials userAndPassword;
//  userAndPassword = (UsernamePasswordCredentials) credentials;

//  String username = userAndPassword.getUsername();
//  String password = userAndPassword.getPassword();

//  User user = repository.findUserByName(username);
//  boolean isUserValid = false;
//  if (user != null) {
//    isUserValid = user.validatePassword(password);
//  }

//  log.trace("Leaving authenticate");
//  return isUserValid;
//}

///** Returns true if we support the provided credentials.
// *
// * This implementation only supports credentials with username and password.
// *
// * @param credentials The credentials to check if they are supported. This
// * must be a UsernamePasswordCredentials instance.
// *
// * @return returns true if the provided credentials is a
// * UsernamePasswordCredentials instance.
// */
//public boolean supports(final Credentials credentials) {
//  return credentials instanceof UsernamePasswordCredentials;
//}
}

