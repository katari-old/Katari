/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

/** Holds the information needed by MenuBarFactory to create menu nodes.
 */
class MenuNodeHolder {

  /** The node display name to the customer. It cannot be null.
   */
  private String displayName = "";

  /** The node identifier name.
   *
   * It cannot be null.
   */
  private String name = "";

  /** The node position in the container.
   *
   * It only applies for non top level nodes and can be either negative or
   * positive.
   */
  private int position = 0;

  /** The node tool tip text.
   *
   * A null tooltip means that no tooltip will be rendered.
   */
  private String toolTip = null;

  /** The path this node is linked to.
   *
   * It will be only null for containers.
   */
  private String linkPath = null;

  /** A List containing all the child nodes for this node.
   *
   * It is never null, just empty for leaf nodes.
   */
  private List<MenuNodeHolder> children = new ArrayList<MenuNodeHolder>();

  /** Creates a new <code>MenuNodeHolder</code> container.
   *
   * @param theDisplayName the node display name to the customer. It cannot be
   * null.
   *
   * @param theName the node identifier name. It cannot be null and cannot
   * contain empty spaces.
   *
   * @param thePosition the node position in the container. It only applies for
   * non top level nodes and can be either negative or positive.
   *
   * @param theToolTip the menu tooltip, a null tooltip means that no tooltip
   * will be rendered.
   */
  public MenuNodeHolder(final String theDisplayName, final String theName,
      final int thePosition, final String theToolTip) {

    displayName = theDisplayName;
    name = theName;
    position = thePosition;
    toolTip = theToolTip;
  }

  /** Creates a new <code>MenuNodeHolder</code> leaf.
   *
   * @param theDisplayName the node display name to the client. If empty, it
   * uses the name as display name.
   *
   * @param theName the node identifier name. It cannot be null and cannot
   * contain empty spaces.
   *
   * @param thePosition the node position in the container. It only applies for
   * non top level nodes and can be either negative or positive.
   *
   * @param theToolTip the menu tooltip, a null tooltip means that no tooltip
   * will be rendered.
   *
   * @param theLinkPath the leaf link, since is a leaf it cannot be null.
   */
  public MenuNodeHolder(final String theDisplayName,
      final String theName, final int thePosition, final String theToolTip,
      final String theLinkPath) {

    Validate.notEmpty(theName, "You have to specify the identifier name");
    Validate.isTrue(theName.indexOf(' ') == -1,
        "the name cannot contain empty spaces");
    Validate.notNull(theLinkPath, "A Leaf should always have a link path");

    displayName = theDisplayName;
    name = theName;
    position = thePosition;
    toolTip = theToolTip;
    linkPath = theLinkPath;
  }

  /**
   * Returns the node display name.
   *
   * @return the node display name, it is never null.
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Returns the node identifier name.
   *
   * @return the node identifier name, it is never null and cannot contain empty
   * spaces.
   */
  public String getName() {
    return name;
  }

  /** Returns the node position in the container.
   *
   * It only applies for non top level nodes and can be either negative or
   * positive. The menu is shown in the screen in ascending position order.
   *
   * @return the node position;
   */
  public int getPosition() {
    return position;
  }

  /** Returns the node tooltip.
   *
   * @return the menu tooltip. A null tooltip means that no tooltip will be
   * rendered.
   */
  public String getToolTip() {
    return toolTip;
  }

  /** Returns the menu path.
   *
   * @return the menu path. A null path means that this is a non leaf node.
   */
  public String getLinkPath() {
    return linkPath;
  }

  /** Returns the children of the receiver as an <code>List</code>.
   *
   * This is only valid if the node is a container, means not a leaf.
   *
   * @return a List containing the children of this node.
   */
  public List<MenuNodeHolder> getChildren() {
    return children;
  }

  /** Sets the list of children for this node.
   *
   * @param theChildren a list of MenuNodeHolder instances. Each holder holds a
   * menu node that has the current node as parent. It cannot be null.
   */
  public void setChildren(final List<MenuNodeHolder> theChildren) {
    Validate.notNull(theChildren, "The children cannot be null.");
    children = theChildren;
  }
}

