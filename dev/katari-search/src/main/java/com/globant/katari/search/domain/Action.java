/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain;

import org.apache.commons.lang.Validate;

/** An action that can be performed by the user on an object returned by the
 * index repository, for example, edit or delete.
 *
 * Each action is essentially a link to a web page where you can perform this
 * action.
 *
 * @author nira.amit@globant.com
 */
public class Action {

  /** The name or title of the action, never null.
   */
  private String name;

  /** An optional icon to be displayed as a link to the action.
   *
   * If null, no icon will be shown.
   */
  private String icon = null;

  /** The action url, context relative.
   *
   * It is never null.
   */
  private String url;

  /** Creates a new action.
   *
   * @param theName the name or title of the action. It cannot be null.
   *
   * @param theIcon the icon to display as a link to the action. It cannot be
   * null.
   *
   * @param theUrl the action url, a link to show to the user to perform the
   * action. It cannot be null.
   */
  public Action(final String theName, final String theIcon, final
      String theUrl) {
    Validate.notNull(theName, "The name cannot be null.");
    Validate.notNull(theUrl, "The url cannot be null.");
    name = theName;
    icon = theIcon;
    url = theUrl;
  }

  /** The action name to present in the link to the action, for example,
   * "edit", "delete", etc.
   *
   * @return the action's title, never returns null.
   */
  public String getName() {
    return name;
  }

  /** An (optional) icon to be displayed as a link to the action.
   *
   * @return a string that is the url to the resource for the icon, relative to
   * the webapp context path. null if no icon was defined.
   */
  public String getIcon() {
    return icon;
  }

  /** The url to the web-page where the said action can be performed.
   *
   * A saple url is "user/userView.do?userId=" + user.getId(). It is relative
   * to the webapp context path.
   *
   * @return a url to the web-page where the said action can be performed,
   * never null.
   */
  public String getUrl() {
    return url;
  }
}

