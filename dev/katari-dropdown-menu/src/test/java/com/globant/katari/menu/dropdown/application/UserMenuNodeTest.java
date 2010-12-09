/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.menu.dropdown.application;

import static org.easymock.classextension.EasyMock.createNiceMock;

import java.util.List;
import java.util.LinkedList;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.core.security.MenuAccessFilterer;
import com.globant.katari.core.web.MenuBar;
import com.globant.katari.core.web.MenuNode;

import com.globant.katari.core.security.SecureUrlAccessHelper;

/* Test case for the menu bar component
 */
public class UserMenuNodeTest {

  private MenuBar menuBar;

  private SecureUrlAccessHelper helper
    = createNiceMock(SecureUrlAccessHelper.class);

  @Before
  public void setUp() {

    menuBar = new MenuBar("root", "root");

    // create child nodes
    /*                 root           0     */
    /*                /    \                */
    /*               a      b         1     */
    /*              / \   / | \             */
    /*             a   b a  bb b      2     */
    /*            /   /     | / \           */
    /*           a   a      ba   b    3     */
    MenuNode a = new MenuNode(menuBar, "a", "a", 0, null);
    MenuNode b = new MenuNode(menuBar, "b", "b", 0, null);

    MenuNode a_a = new MenuNode(a, "a", "a", 0, null);
    MenuNode a_b = new MenuNode(a, "b", "b", 0, null);

    new MenuNode(b, "a", "a", 0, null, "link_b_a");
    MenuNode b_bb = new MenuNode(b, "bb", "bb", 0, null);
    MenuNode b_b = new MenuNode(b, "b", "b", 0, null);

    new MenuNode(a_a, "a", "a", 0, null, "link_a_a");
    new MenuNode(a_b, "a", "a", 0, null, "link_a_b");

    new MenuNode(b_bb, "b", "b", 0, null, "link_b_bb_b");

    new MenuNode(b_b, "a", "a", 0, null, "link_b_b_a");
    new MenuNode(b_b, "b", "b", 0, null, "link_b_b_b");
  }

  @Test
  public void testUserMenuNode_all() {

    MenuAccessFilterer filterer = new MenuAccessFilterer(helper) {
      public boolean isAccessible(final MenuNode node) {
        return true;
      }
    };

    UserMenuNode rootNode = new UserMenuNode(menuBar, filterer);
    // First level
    assertThat(rootNode.getChildren().size(), is(2));
    // Second level
    UserMenuNode a = rootNode.getChildren().get(0);
    UserMenuNode b = rootNode.getChildren().get(1);
    assertThat(a.getChildren().size(), is(2));
    assertThat(b.getChildren().size(), is(3));

    assertThat(a.getChildren().get(0).getChildren().size(), is(1));
    assertThat(a.getChildren().get(1).getChildren().size(), is(1));
    assertThat(b.getChildren().get(0).getChildren().size(), is(0));
    assertThat(b.getChildren().get(1).getChildren().size(), is(1));
    assertThat(b.getChildren().get(2).getChildren().size(), is(2));
  }

  @Test
  public void testUserMenuNode_none() {
    MenuAccessFilterer filterer = new MenuAccessFilterer(helper) {
      public boolean isAccessible(final MenuNode node) {
        return false;
      }
    };

    UserMenuNode rootNode = new UserMenuNode(menuBar, filterer);
    // First level
    assertThat(rootNode.getChildren().size(), is(0));
  }
}

