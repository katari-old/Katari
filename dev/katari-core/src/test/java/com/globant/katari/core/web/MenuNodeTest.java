/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import static org.junit.Assert.*;

/* Test case for the menu node component
 */
public class MenuNodeTest {

  @Test
  public void testMenuNode_new() {
    // create the menu bar
    MenuBar menuBar = new MenuBar("root", "root");

    // create children nodes
    MenuNode childA = new MenuNode(menuBar, "admin", "Users", 0, null, "link");
    MenuNode childB = new MenuNode(menuBar, "admin", "Clients", 1, null,
        "link");

    assertEquals(menuBar, childA.getParent());
    assertEquals(menuBar, childB.getParent());
    assertEquals(2, menuBar.getChildNodes().size());
  }

  @Test
  public void testGetChildNodes_sorted() {
    // create the menu bar
    MenuBar menuBar = new MenuBar("root", "rott");

    // create children nodes
    MenuNode childA = new MenuNode(menuBar, "admin", "Users", 0, null, "link");
    MenuNode childB = new MenuNode(menuBar, "admin", "Clients", 1,
        null, "link");

    assertEquals(childA, menuBar.getChildNodes().get(0));
    assertEquals(childB, menuBar.getChildNodes().get(1));
  }

  @Test
  public void testGetChildNodes_count() {
    // create the menu bar
    MenuBar menuBar = new MenuBar("root", "root");

    // create children nodes
    new MenuNode(menuBar, "admin", "Users", 0, null, "link");
    new MenuNode(menuBar, "admin", "Clients", 1, null, "link");

    assertEquals(2, menuBar.getChildNodes().size());
  }

  @Test
  public void testGetChildNodes_index() {
    // create the menu bar
    MenuBar menuBar = new MenuBar("root", "root");

    // create children nodes
    MenuNode childA = new MenuNode(menuBar, "admin", "Users", 0, null,
        "link");
    MenuNode childB = new MenuNode(menuBar, "admin", "Clients", 1,
        null, "link");

    assertEquals(0, menuBar.getChildNodes().indexOf(childA));
    assertEquals(1, menuBar.getChildNodes().indexOf(childB));
  }

  @Test
  public void testGetPath() {
    // create the menu bar
    MenuBar menuBar = new MenuBar("root", "root");

    // create children nodes
    MenuNode levelA = new MenuNode(menuBar, "admin", "levelA", 0, null);
    MenuNode levelB = new MenuNode(levelA, "admin", "levelB", 0, null);
    MenuNode levelC = new MenuNode(levelB, "admin", "levelC", 0, null, "link");

    assertEquals("/root/levelA/levelB/levelC", levelC.getPath());
  }

  @Test
  public void testEquals() {
    // create the menu bar
    MenuBar menuBar = new MenuBar("root", "root");

    // create children nodes
    MenuNode levelA = new MenuNode(menuBar, "admin", "levelA", 0, null);
    MenuNode levelB1 = new MenuNode(levelA, "admin", "levelB", 0, null);
    MenuNode levelB2 = new MenuNode(levelA, "admin", "levelB", 0, null, "link");

    assertEquals(true, levelB1.equals(levelB2));
  }

  @Test
  public void testMerge() {
    // create the menu bar
    MenuBar menuBar1 = new MenuBar("root", "root");

    // create children nodes
    MenuNode levelA1 = new MenuNode(menuBar1, "admin", "levelA", 0, null);
    MenuNode levelB1 = new MenuNode(levelA1, "admin", "levelB", 0, null);
    MenuNode levelC1 = new MenuNode(levelB1, "admin", "levelC1", 0,
        null, "link");

    // create the menu bar
    MenuBar menuBar2 = new MenuBar("root", "root");

    // create children nodes
    MenuNode levelA2 = new MenuNode(menuBar2, "admin", "levelA", 0, null);
    MenuNode levelB2 = new MenuNode(levelA2, "admin", "levelB", 0, null);
    MenuNode levelC2 = new MenuNode(levelB2, "admin", "levelC2", 0,
        null, "link");

    // merge
    menuBar1.merge(menuBar2, new HashMap<String, String>(), "");

    assertEquals(1, menuBar1.getChildNodes().size());
    assertEquals(1, menuBar1.getChildNodes().get(0).getChildNodes()
        .size());
    assertEquals(2, menuBar1.getChildNodes().get(0).getChildNodes()
        .get(0).getChildNodes().size());
    assertEquals(levelB1, levelC1.getParent());
    assertEquals(levelB1, levelC2.getParent());
  }

  // Tests a merge with variable replacement.
  @Test
  public void testMergeWithReplacement() {
    MenuBar topBar = new MenuBar("root", "root");

    // create the menu bar
    MenuBar menuBar1 = new MenuBar("root", "root");

    // create children nodes
    new MenuNode(menuBar1, "admin", "levelA1", 0, null, "link-1");

    // create the menu bar
    MenuBar menuBar2 = new MenuBar("root", "root");

    // create children nodes
    new MenuNode(menuBar2, "admin", "levelA2", 0, null, "${var1}/link-2");

    Map<String, String> variables = new HashMap<String, String>();
    variables.put("var1", "value-1");

    // merge
    topBar.merge(menuBar1, variables, "prefix");
    topBar.merge(menuBar2, variables, "prefix");

    assertEquals(2, topBar.getChildNodes().size());

    assertEquals("/prefix/link-1", topBar.getChildNodes().get(0).getLinkPath());
    assertEquals("/value-1/link-2", topBar.getChildNodes().get(1).getLinkPath());
  }

  // Tests tha that merge correctly prepends the module prefix to the merged
  // nodes.
  @Test
  public void testMerge_rootModule() {
    // create the menu bar
    MenuBar menuBar1 = new MenuBar("root", "root");

    // create children nodes
    new MenuNode(menuBar1, "admin", "levelA", 0, null);
    new MenuNode(menuBar1, "admin", "levelB1", 0, null, "link");

    // create the menu bar
    MenuBar menuBar2 = new MenuBar("root", "root");

    // create children nodes
    new MenuNode(menuBar2, "admin", "levelA", 0, null);
    MenuNode levelB2 = new MenuNode(menuBar2, "admin", "levelB2", 0,
        null, "");

    // merge
    menuBar1.merge(menuBar2, new HashMap<String, String>(), "somepref");

    assertEquals("/somepref/", levelB2.getLinkPath());
  }

  // Tests tha that merge correctly adds the default home of a non leaf
  // node.
  @Test
  public void testMerge_defaultHome() {
    // create the menu bar
    MenuBar menuBar1 = new MenuBar("root", "root");

    // create children nodes
    MenuNode levelA1 = new MenuNode(menuBar1, "admin", "levelA", 0, null);
    new MenuNode(menuBar1, "admin", "levelB1", 0, null, "link");

    // create the menu bar
    MenuBar menuBar2 = new MenuBar("root", "root");

    // create children nodes
    new MenuNode(menuBar2, "admin", "levelA", 0, null);
    new MenuNode(menuBar2, "admin", "levelB2", 0, null, "");

    // merge
    menuBar1.merge(menuBar2, new HashMap<String, String>(), "somepref");

    menuBar1.getHome().equals(levelA1);
  }

  // Tests that merge correctly adds the default home of a non leaf node
  // when the target menu is empty.
  @Test
  public void testMerge_defaultHomeEmptyTarget() {
    // create the menu bar
    MenuBar menuBar1 = new MenuBar("root", "root");

    // create the menu bar
    MenuBar menuBar2 = new MenuBar("root", "root");

    // create children nodes
    MenuNode levelA2 = new MenuNode(menuBar2, "admin", "levelA", 0, null);
    new MenuNode(menuBar2, "admin", "levelB2", 0, null, "");

    // merge
    menuBar1.merge(menuBar2, new HashMap<String, String>(), "somepref");

    menuBar1.getHome().equals(levelA2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testChildrenNotAllowedInLeaf() {
    // create the menu bar
    MenuBar menuBar = new MenuBar("root", "root");

    // create children nodes
    MenuNode child = new MenuNode(menuBar, "admin", "Users", 0, null, "link");

    // Get the children. Should throw an exception.
    child.getChildNodes();
  }

  /* Tests that all ancestors of a node has the home correctly set.
   */
  @Test
  public void testHomeAncestors() {
    // create the menu bar
    MenuBar menuBar = new MenuBar("root", "root");

    // create children nodes
    MenuNode levelA = new MenuNode(menuBar, "admin", "levelA", 0, null);
    MenuNode levelB = new MenuNode(levelA, "admin", "levelB", 0, null);
    MenuNode levelC = new MenuNode(levelB, "admin", "levelC", 0, null, "link");

    assertEquals(levelC, levelB.getHome());
    assertEquals(levelC, levelA.getHome());
    assertEquals(levelC, menuBar.getHome());
  }
}

