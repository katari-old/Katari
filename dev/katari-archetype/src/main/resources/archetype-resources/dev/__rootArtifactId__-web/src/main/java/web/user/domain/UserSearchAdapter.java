#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package ${package}.web.user.domain;

import org.apache.commons.lang.Validate;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.search.domain.SearchAdapter;
import com.globant.katari.search.domain.SearchResultElement;
import com.globant.katari.search.domain.Action;

import com.globant.katari.hibernate.coreuser.domain.Role;

/** Converts User objects to SearchResultElement.
 *
 * Implements the necessary SearchAdapter so that the search module can present
 * the result of a search when the object found is of type User.
 */
public class UserSearchAdapter implements SearchAdapter {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(UserSearchAdapter.class);

  /** The prefix to prepend to all action urls.
   *
   * It is never null.
   */
  private String urlPrefix;

  /** Constructor.
   *
   * @param theUrlPrefix the prefix to prepend to all action urls. This url is
   * relative to the webapp context path. It cannot be null.
   */
  public UserSearchAdapter(final String theUrlPrefix) {
    Validate.notNull(theUrlPrefix, "The url prefix cannot be null.");
    urlPrefix = theUrlPrefix;
    if (!theUrlPrefix.endsWith("/")) {
      urlPrefix += "/";
    }
  }

  /** Converts (or wraps) the provided object to a SearchResultElement.
   *
   * Only call this operation if canConvert returned true on object, so object
   * must be an instance of User.
   *
   * @param object the object to convert. It must be an instance of User. It
   * cannot be null.
   *
   * @return a SearchResultElement initialized, never returns null.
   */
  public SearchResultElement convert(final Object object, final float score) {
    log.trace("Entering convert");

    Validate.notNull(object, "The object to convert cannot be null.");

    User user = (User) object;

    ArrayList<Action> actions;
    actions = new ArrayList<Action>();

    actions.add(new Action("Edit", null,
          urlPrefix + "userEdit.do?userId=" + user.getId()));

    StringBuilder roles = new StringBuilder();

    for (Role role: user.getRoles()) {
      if (roles.length() != 0) {
        roles.append(", ");
      }
      roles.append(role.getName());
    }

    StringBuilder description = new StringBuilder();
    description.append("User - name: " + user.getName());
    description.append("; email: " + user.getEmail());
    if (user.getRoles().size() != 0) {
      description.append("; roles: " + roles.toString());
    }

    SearchResultElement result = new SearchResultElement("User",
        user.getName(), description.toString(), urlPrefix
        + "userView.do?userId=" + user.getId(), actions, score);

    log.trace("Leaving convert");
    return result;
  }

  /** Returns the url to view a user, relative to the web application context.
   *
   * @return a string with the url.
   */
  public String getViewUrl() {
    return urlPrefix + "userView.do";
  }

  /** Returns which class we adapt.
   *
   * @return this implementation always returns User.class.
   */
  public Class<User> getAdaptedClass() {
    return User.class;
  }
}

