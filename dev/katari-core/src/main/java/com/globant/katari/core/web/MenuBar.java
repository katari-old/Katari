package com.globant.katari.core.web;

/**
 * The menu bar is specifically a menu node with the explicit fact of not
 * having a parent. This class is passed trought the context to the registrar in
 * order to be populated with each module specific menues.
 *
 * @author mariano.nardi
 */
public class MenuBar extends MenuNode {

  /**
   * Creates a new <code>MenuBar</code> container.
   *
   * That means that this is a top level menu node. Beeing a top level node
   * means that it does not have a parent and public access is granted. It also
   * have no tooltip.
   *
   * @param theDisplayName the node display name to the customer. It cannot be
   * null.
   * @param theName the node identifier name. It cannot be null and cannot
   * contain empty spaces.
   */
  public MenuBar(final String theDisplayName, final String theName) {
    super(theDisplayName, theName);
  }

  /**
   * Creates a new <code>MenuBar</code> container, named 'root' and displaying
   * 'root' as display name.
   *
   * That means that this is a top level menu node. Beeing a top level node
   * means that it does not have a parent and public access is granted. It also
   * have no tooltip.
   */
  public MenuBar() {
    super("root", "root");
  }
}
