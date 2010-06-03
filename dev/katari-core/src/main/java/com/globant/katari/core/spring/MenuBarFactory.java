/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.FactoryBean;

import com.globant.katari.core.web.MenuBar;
import com.globant.katari.core.web.MenuNode;

/** A menu bar factory.
 *
 * This class is used to create menu bars with its menus. It is initialized
 * from MenuBarBeanDefinitionParser with all the information to create a
 * menubar.
 */
public class MenuBarFactory implements FactoryBean {

  /** The created menubar.
   */
  private MenuBar menuBar = null;

  /** The list of menu nodes contained in the menubar.
   *
   * It is never null.
   */
  private List<MenuNodeHolder> children = new LinkedList<MenuNodeHolder>();

  /** Sets the menu bar.
   *
   * @param theMenuBar the menu bar. It cannot be null.
   */
  public void setMenuBar(final MenuBar theMenuBar) {
    Validate.notNull(theMenuBar, "The menu bar cannot be null.");
    menuBar = theMenuBar;
  }

  /** Sets the menu bar nodes.
   *
   * @param theChildren a list with the menu bar children. It cannot be null.
   */
  public void setChildren(final List<MenuNodeHolder> theChildren) {
    Validate.notNull(theChildren, "The menu bar children cannot be null.");
    children = theChildren;
  }

  /** Creates a menubar based on the supplied information.
   *
   * It builds menu nodes from the supplied MenuNodeHolder list and adds them
   * to the menubar. It can only be called after setMenuBar.
   *
   * @return the menubar. It never returns null.
   */
  public Object getObject() {
    Validate.notNull(menuBar, "Must first call setMenuBar().");
    createChildren(menuBar, children);
    return menuBar;
  }

  /** Creates the child nodes and adds them to the parent.
   *
   * @param parent The parent node. It cannot be null.
   *
   * @param childHolders The list of child holders. It cannot be null.
   */
  private void createChildren(final MenuNode parent,
      final List<MenuNodeHolder> childHolders) {

    Validate.notNull(parent, "The parent cannot be null");
    Validate.notNull(childHolders, "The child holders cannot be null");

    for (MenuNodeHolder holder : childHolders) {
      // Replaces the display name for the node name if the display name is
      // empty.
      String displayName = holder.getDisplayName();
      if ("".equals(displayName)) {
        displayName = holder.getName();
      }
      if (holder.getLinkPath() == null) {
        // Loading a menu container.
        MenuNode node = new MenuNode(parent, displayName,
            holder.getName(), holder.getPosition(), holder.getToolTip());
        createChildren(node, holder.getChildren());
      } else {
        // Loading a menu leaf.
        new MenuNode(parent, displayName, holder.getName(),
            holder.getPosition(), holder.getToolTip(), holder.getLinkPath());
      }
    }
  }

  /** Returns the object type created by this factory.
   *
   * @return It returns MenuBar.class.
   */
  @SuppressWarnings("unchecked")
  public Class getObjectType() {
    return MenuBar.class;
  }

  /** Specifies if this factory refers to a singleton.
   *
   * @return always returns true.
   */
  public boolean isSingleton() {
    return true;
  }
}

