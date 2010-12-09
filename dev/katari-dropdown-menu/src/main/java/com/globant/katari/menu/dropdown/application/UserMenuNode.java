/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.menu.dropdown.application;

import org.apache.commons.lang.Validate;

import java.util.List;
import java.util.LinkedList;

import com.globant.katari.core.web.MenuNode;

import com.globant.katari.core.security.MenuAccessFilterer;

/** A menu tree accesible by the logged in user.
 */
public class UserMenuNode {

  /** The node to display.
   *
   * It is never null.
   */
  private MenuNode menuNode;

  /** The childre of this node.
   *
   * This is never null.
   */
  private List<UserMenuNode> children = new LinkedList<UserMenuNode>();

  /** Create a UserMenuNode for a menu node.
   *
   * The UserMenuNode contains the tree of all menu items accesible by the
   * user, as determined by the filterer.
   *
   * @param theMenuNode The menu node. It cannot be null.
   *
   * @param filterer Decides if a leaf node is accessible or not. This cannot
   * be null.
   */
  public UserMenuNode(final MenuNode theMenuNode,
      final MenuAccessFilterer filterer) {

    Validate.notNull(theMenuNode, "A menu node must be provided to create a"
        + " display node");
    Validate.notNull(filterer, "The menu filterer cannot be null");

    menuNode = theMenuNode;
    children = getChildren(menuNode, filterer);
  }

  /** Create a UserMenuNode for a leaf menu node.
   *
   * @param leafNode The menu node. It must be a non null leaf node.
   */
  private UserMenuNode(final MenuNode leafNode) {
    Validate.notNull(leafNode, "The menu node cannot be null.");
    Validate.isTrue(leafNode.isLeaf(), "The menu node must be a leaf.");
    menuNode = leafNode;
  }

  /** Create a UserMenuNode for a non-leaf menu node, with some children.
   *
   * @param node The menu node. It cannot be a leaf node and it cannot be null.
   *
   * @param theChildren The wrapped children of the node. It cannot be null.
   */
  private UserMenuNode(final MenuNode node,
      final List<UserMenuNode> theChildren) {
    Validate.notNull(node, "The menu node cannot be null.");
    Validate.isTrue(!node.isLeaf(), "The menu node cannot be a leaf.");
    Validate.notNull(theChildren, "The children nodes cannot be null.");
    menuNode = node;
    children = theChildren;
  }

  /** Wraps the user accessible children of the provided parent node in a
   * UserMenuNode.
   *
   * This class returns a list of UserMenuNode that wraps the children of the
   * parent node, keeping its order. The resulting list only contains nodes
   * that has, as descendent, at least one accessible leaf node, as decided by
   * the filterer.
   *
   * @param parentNode The node that contains the children to wrap. It must be
   * a non null, non leaf node.
   *
   * @param filterer Decides if a leaf node is accessible or not. This cannot
   * be null.
   *
   * @return a list of UserMenuNode that wraps the accessible children of
   * parentNode. It never returns null.
   */
  private List<UserMenuNode> getChildren(final MenuNode parentNode,
      final MenuAccessFilterer filterer) {
    Validate.notNull(parentNode, "The menu node cannot be null.");
    Validate.isTrue(!parentNode.isLeaf(), "The menu node cannot be a leaf.");
    List<UserMenuNode> result = new LinkedList<UserMenuNode>();
    List<UserMenuNode> currentChildren;
    for (MenuNode node : parentNode.getChildNodes()) {
      if (node.isLeaf()) {
        if (filterer.isAccessible(node)) {
          result.add(new UserMenuNode(node));
        }
      } else {
        currentChildren = getChildren(node, filterer);
        if (currentChildren.size() > 0) {
          result.add(new UserMenuNode(node, currentChildren));
        }
      }
    }
    return result;
  }

  /** Obtains the wrapped menu node.
   *
   * @return the menu node. It never returns null.
   */
  public MenuNode getMenuNode() {
    return menuNode;
  }

  /** Obtains the wrapped children of this user menu node.
   *
   * @return the list of children, never null.
   */
  public List<UserMenuNode> getChildren() {
    return children;
  }
}

