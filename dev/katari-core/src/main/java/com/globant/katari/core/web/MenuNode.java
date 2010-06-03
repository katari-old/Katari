/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This represents a node item in a menu, a node item could be either a
 * container or a leaf.
 *
 * A container means that this menu item can hold either other containers or
 * leaves inside, creating a tree.
 *
 * @author mariano.nardi
 */
public class MenuNode implements Comparable<MenuNode> {

  /** The class logger.
   */
  private final Logger log = LoggerFactory.getLogger(MenuNode.class);

  /** The node display name to the customer.
   *
   * It cannot be null.
   */
  private String displayName = "";

  /** The node identifier name.
   *
   * it cannot be null and cannot contain empty spaces.
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

  /** false if this node allows children on it, true if it is at the bottom
   * level.
   */
  private boolean isLeaf = false;

  /** A List containing all the child nodes for this node.
   *
   * It is never null, just empty for leaf nodes.
   */
  private final List<MenuNode> childNodes = new ArrayList<MenuNode>();

  /** The node parent, null means a top level node but this will be only valid
   * for menu bars.
   */
  private MenuNode parent = null;

  /** The home node, this will be selected option when the container is
   * selected.
   *
   * It is null only for leafs and empty containers.
   */
  private MenuNode home = null;

  /**
   * Creates a new <code>MenuNode</code> container.
   *
   * @param theParent the parent for this node, it cannot be null. if you need a
   * top level container a MenuBar should be used.
   * @param theDisplayName the node display name to the customer. It cannot be
   * null.
   * @param theName the node identifier name. It cannot be null and cannot
   * contain empty spaces.
   * @param thePosition the node position in the container. It only applies for
   * non top level nodes and can be either negative or positive.
   * @param theToolTip the menu tooltip, a null tooltip means that no tooltip
   * will be rendered.
   */
  public MenuNode(final MenuNode theParent, final String theDisplayName,
      final String theName, final int thePosition, final String theToolTip) {

    Validate.notNull(theParent, "The parent cannot be null, if you need "
        + "a top level node use a MenuBar");
    Validate.notEmpty(theDisplayName, "You have to specify the display name");
    Validate.notEmpty(theName, "You have to specify the identifier name");
    Validate.isTrue(theName.indexOf(' ') == -1,
        "the name cannot contain empty spaces");

    this.isLeaf = false;

    this.parent = theParent;
    this.parent.childNodes.add(this);

    this.displayName = theDisplayName;
    this.name = theName;
    this.position = thePosition;
    this.toolTip = theToolTip;
  }

  /**
   * Creates a new <code>MenuNode</code> leaf.
   *
   * @param theParent the parent for this node, since is a leaf it cannot be
   * null.
   * @param theDisplayName the node display name to the client.
   * @param theName the node identifier name. It cannot be null and cannot
   * contain empty spaces.
   * @param thePosition the node position in the container. It only applies for
   * non top level nodes and can be either negative or positive.
   * @param theToolTip the menu tooltip, a null tooltip means that no tooltip
   * will be rendered.
   * @param theLinkPath the leaf link, since is a leaf it cannot be null.
   */
  public MenuNode(final MenuNode theParent, final String theDisplayName,
      final String theName, final int thePosition,
      final String theToolTip, final String theLinkPath) {

    Validate.notNull(theParent, "A Leaf should always have a parent");
    Validate.notEmpty(theDisplayName, "You have to specify the display name");
    Validate.notEmpty(theName, "You have to specify the identifier name");
    Validate.isTrue(theName.indexOf(' ') == -1,
        "the name cannot contain empty spaces");
    Validate.notNull(theLinkPath, "A Leaf should always have a link path");

    this.isLeaf = true;

    this.parent = theParent;
    this.parent.childNodes.add(this);
    this.parent.setHome(this);

    this.displayName = theDisplayName;
    this.name = theName;
    this.position = thePosition;
    this.toolTip = theToolTip;
    this.linkPath = theLinkPath;
  }

  /**
   * Returns the node display name.
   *
   * @return the node display name, it is never null.
   */
  public String getDisplayName() {
    return this.displayName;
  }

  /**
   * Returns the node identifier name.
   *
   * @return the node identifier name, it is never null and cannot contain empty
   * spaces.
   */
  public String getName() {
    return this.name;
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

  /** Returns the node link path where this menu element links to.
   *
   * @return the link path for this node. If this is a container, it returns
   * the link of the home menu, if specified. Otherwise, it returns null.
   */
  public String getLinkPath() {
    if (this.isLeaf) {
      return this.linkPath;
    } else if (this.home != null) {
      return this.home.getLinkPath();
    }
    return null;
  }

  /**
   * Returns the node tooltip.
   *
   * @return the menu tooltip. A null tooltip means that no tooltip will be
   * rendered.
   */
  public String getToolTip() {
    return this.toolTip;
  }

  /**
   * Returns the parent <code>MenuNode</code> of the receiver.
   *
   * @return the parent container for this node. A null parent means a top level
   * node (a MenuBar).
   */
  public MenuNode getParent() {
    return this.parent;
  }

  /**
   * Returns the home <code>MenuNode</code> of the container.
   *
   * @return the home leaf of this container. the first added child will be
   * assumed as home until a new home leaf is explicitly added. Null if the node
   * has no chidren.
   */
  public MenuNode getHome() {
    return this.home;
  }

  /** Sets the home <code>MenuNode</code> of the container and all it's
   * ancestors.
   *
   * This operation guarantees that every node in the menu hierarchy has a
   * home. If the home was already set, this operation does nothing.
   *
   * @param theHome the home leaf of this container. the first added child will
   * be assumed as home until a new home leaf is explicitly added. It cannot be
   * specified as null but it will be null while this node contains no children.
   */
  private void setHome(final MenuNode theHome) {
    Validate.notNull(theHome, "you can't specify a null home node");
    if (home == null) {
      home = theHome;
      if (parent != null) {
        parent.setHome(theHome);
      }
    }
  }

  /**
   * Returns true if this menu represents a leaf.
   *
   * @return true if this is a leaf. That means that no child can be added to
   * this node.
   */
  public boolean isLeaf() {
    return this.isLeaf;
  }

  /** Returns the children of the receiver as an <code>List</code>.
   *
   * This is only valid if the node is a container, means not a leaf.
   *
   * @return a List containing the children of this node.
   */
  public List<MenuNode> getChildNodes() {
    Validate.isTrue(!this.isLeaf,
        "This node is a leaf, so it contains no children");
    return Collections.unmodifiableList(childNodes);
  }

  /**
   * Returns the node's path signature.
   *
   * The path creation is delegated into the parents recursively.
   *
   * @return the node's path signature. This is not null.
   */
  public String getPath() {
    if (this.getParent() == null) {
      return "/" + this.name;
    } else {
      return this.getParent().getPath() + "/" + this.name;
    }
  }

  /** Merge all the children of the other node into this node.
   *
   * The merge operation consists of taking all the children of the other node
   * and adding them to this node. If two nodes have the same name and one is a
   * leaf node, the merge fails. Otherwise, the merge is reapplied between both
   * nodes.
   *
   * Notice that the other node is not included in the merge, only its
   * children.
   *
   * Also, given that merging adds child nodes, this node cannot be a leaf.
   *
   * @param other the node whose children need to be merged with the ones on
   * this node that should be merged. It cannot be null.
   *
   * @param variables a map of variable names to variables. These variables can
   * be referenced in the menu link as ${variable-name}. It cannot be null.
   *
   * @param prefix a prefix to add to relative menu link. It cannot be null.
   */
  public void merge(final MenuNode other,
      final Map<String, String> variables, final String prefix) {

    log.trace("Entering <{}>->merge('{}', ...", name, other.name);

    Validate.notNull(other, "The menu to merge cannot be null");
    Validate.notNull(variables, "The variables map cannot be null");
    Validate.notNull(prefix, "The prefix cannot be null");
    Validate.isTrue(!isLeaf, "You cannot merge with a leaf");

    other.transformAllLinks(variables, prefix);

    if (this.childNodes.size() > 0) {
      for (MenuNode otherCurrNode : other.childNodes) {
        int pos = this.childNodes.indexOf(otherCurrNode);
        if (pos != -1) {
          MenuNode thisCurrNode = this.childNodes.get(pos);
          Validate.isTrue(!thisCurrNode.isLeaf() && !otherCurrNode.isLeaf(),
              "You cannot have a leaf node with the same name as other menu"
              + " node. The problematic menu is " + otherCurrNode.getPath());
          thisCurrNode.merge(otherCurrNode, variables, prefix);
          // thisCurrNode.home = null;
        } else {
          this.childNodes.add(otherCurrNode);
        }
      }
    } else {
      this.childNodes.addAll(other.getChildNodes());
    }
    Collections.sort(this.childNodes);
    boolean mustSetHome = (home == null && childNodes.size() > 0);
    if (mustSetHome) {
      home = childNodes.get(0);
    }
    log.trace("Leaving merge");
  }

  /** Transforms all the links in the menu node (the current node and all its
   * children.
   *
   * @param variables a map of variable names to variables. These variables can
   * be referenced in the menu link as ${variable-name}. It cannot be null.
   *
   * @param prefix a prefix to add to relative menu link. It cannot be null.
   */
  private void transformAllLinks(final Map<String, String> variables,
      final String prefix) {

    Validate.notNull(variables, "The variables map cannot be null");
    Validate.notNull(prefix, "The prefix cannot be null");

    if (this.isLeaf) {
      transformLink(variables, prefix);
    } else {
      for (MenuNode node : childNodes) {
        node.transformAllLinks(variables, prefix);
      }
    }
  }

  /** Transforms the menu link replacing all variables and adding the prefix if
   * necessary.
   *
   * The prefix is added when the link is relative. If a variable could not be
   * replace, this method throws an exception. It can only be called on leaf
   * nodes.
   *
   * @param variables a map of variable names to variables. These variables can
   * be referenced in the menu link as ${variable-name}. It cannot be null.
   *
   * @param prefix a prefix to add to relative menu links. It cannot be null.
   */
  private void transformLink(final Map<String, String> variables,
      final String prefix) {

    log.trace("Entering transformLink(..., '{}')", prefix);

    Validate.notNull(variables, "The variables map cannot be null");
    Validate.notNull(prefix, "The prefix cannot be null");
    Validate.isTrue(isLeaf, "transformLink can only be called on leaf nodes");

    StringBuffer result = new StringBuffer();

    // Replace the variables. \$\{([^}]+)\} matches ${variable-name}.
    Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
    Matcher matcher = pattern.matcher(linkPath);
    log.debug("Matching {} against var", linkPath);
    while (matcher.find()) {
      String variableName = matcher.group(1);
      String value = variables.get(variableName);
      Validate.notNull(value, "Could not find variable " + variableName
            + " transforming menu linkPath " + linkPath);
      matcher.appendReplacement(result, "/" + value);
    }
    matcher.appendTail(result);

    String finalResult = result.toString();
    // Check if it is an absolute linkPath. An absolute linkPath starts with /
    // or a protocol specification.
    if (!finalResult.startsWith("/") && !finalResult.matches("\\w+//:")) {
      // We must add the prefix
      if (finalResult.length() == 0) {
        finalResult = "/" + prefix + "/";
      } else {
        finalResult = "/" + prefix + "/" + finalResult;
      }
    }

    linkPath = finalResult;

    log.trace("Leaving transformLink setting linkpath to '{}'", linkPath);
  }

  /**
   * Overriding to make the two node comparation by using the node path.
   *
   * @see java.lang.Object#equals(java.lang.Object)
   *
   * @param obj The object to compare to. If null, it returns false.
   *
   * @return true if the nodes have the same path.
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof MenuNode)) {
      return false;
    }
    MenuNode other = (MenuNode) obj;
    return getPath().equals(other.getPath());
  }

  /** The node hashcode.
   *
   * It returns the hascode of its path, to be consistent with equals.
   *
   * @return the node hashcode.
   */
  @Override
  public int hashCode() {
    return getPath().hashCode();
  }

  /**
   * Creates a top level MenuNode.
   *
   * This is used to support the MenuBar class. Beeing a top level node means
   * that it does not have a parent and public access is granted. It also have
   * no tooltip.
   *
   * @param theDisplayName the node display name to the customer. It cannot be
   * null.
   * @param theName the node identifier name. It cannot be null and cannot
   * contain empty spaces.
   */
  protected MenuNode(final String theDisplayName, final String theName) {
    Validate.notEmpty(theDisplayName, "You have to specify the display name");
    Validate.notEmpty(theName, "You have to specify the identifier name");
    Validate.isTrue(theName.indexOf(' ') == -1,
        "the name cannot contain empty spaces");

    displayName = theDisplayName;
    name = theName;
  }

  /**
   * Compares this object with the specified object for order.
   *
   * Returns a negative integer, zero, or a positive integer as this object is
   * less than, equal to, or greater than the specified object.<p>
   *
   * @param theOther the other menu node to be compared.
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   *
   * @return an integer specifying how this compares to the other.
   */
  public int compareTo(final MenuNode theOther) {
    return this.position - theOther.position;
  }
}

