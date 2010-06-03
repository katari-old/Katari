/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain;

import org.apache.commons.lang.Validate;

import java.util.List;

/** A wrapper that represents one 'row' for a search result.
 *
 * Exposes information required for visual presentation of each search result.
 *
 * @author nira.amit@globant.com
 */
public class SearchResultElement {

  /** Alias of the type of the object.
   *
   * It is never null.
   */
  private String alias;

  /** Title of the found object.
   *
   * It is never null.
   */
  private String title;

  /** Description of the object, intended to be shown after the title.
   *
   * It is never null, but it can be empty.
   */
  private String description;

  /** The url where you can view the object, relative to the webapp context
   * path.
   *
   * It is never null.
   */
  private String viewUrl;

  /** Possible additional actions you can perform on the found object, like
   * edit or delete.
   *
   * Each action is actually a link to another web page where you can perform
   * this action. It is never null.
   */
  private List<Action> actions;

  /** Search score, a number that states how accurate was the match.
   */
  private float score;

  /** Constructor, builds a search result object.
   *
   * @param theAlias the alias for the type of object. It cannot be null.
   *
   * @param theTitle the title of the found object. This is a short text to
   * describe the object. It cannot be null.
   *
   * @param theDescription the description of the found object. This is a more
   * comprehensive text to describe the object. It cannot be null.
   *
   * @param url the url that will show the full content of the object, relative
   * to the webapp context path. It cannot be null.
   *
   * @param theActions additional actions related to this object. It cannot be
   * null.
   *
   * @param score a number stating how exact was the match of the seach query
   * for the object.
   */
  public SearchResultElement(final String theAlias, final String theTitle,
      final String theDescription, final String url, final List<Action>
      theActions, final float theScore) {
    Validate.notNull(theAlias, "The alias cannot be null.");
    Validate.notNull(theTitle, "The title cannot be null.");
    Validate.notNull(theDescription, "The description cannot be null.");
    Validate.notNull(url, "The view url cannot be null.");
    Validate.notNull(theActions, "The actions cannot be null.");
    alias = theAlias;
    title = theTitle;
    description = theDescription;
    viewUrl = url;
    actions = theActions;
    score = theScore;
  }

  /** Returns a number indicating how accurate was the matching.
   *
   * @return the score of the represented object in the compass search
   */
  public float getScore() {
    return score;
  }

  /** Alias of the type of the object.
   *
   * @return the alias, never returns null.
   */
  public String getAlias() {
    return alias;
  }

  /** Access method.
   *
   * @return the title to display for this entity, never null.
   */
  public String getTitle() {
    return title;
  }

  /** Access method.
   *
   * @return the description, never null.
   */
  public String getDescription() {
    return description;
  }

  /** Access method.
   *
   * @return the viewUrl, never null.
   */
  public String getViewUrl() {
    return viewUrl;
  }

  /** Access method.
   *
   * @return the availableActions. Each action is actually a reference to
   * another web page where you can perform this action. Never returns null.
   */
  public List<Action> getActions() {
    return actions;
  }
}

