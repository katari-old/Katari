package com.globant.katari.core.security;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.List;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;

import com.globant.katari.core.web.MenuNode;
import com.globant.katari.tools.ListFactory;

/**
 * This class is for testing the behavior of the MenuAccessFilterer.
 * @author ulises.bocchio
 *
 */
public class MenuAccessFiltererTest extends TestCase {

  private static final String DENIED_URL = "/module/mocked-module/denied.do";
  private static final String GRANTED_URL = "/module/mocked-module/granted.do";

  /**
   * The MenuAccessFilterer.
   */
  private MenuAccessFilterer filterer;

  /**
   * The {@link SecureUrlAccessHelper}.
   * It is never null.
   */
  private SecureUrlAccessHelper accessHelper;

  /**
   * The set up method for testing MenuAccessFilterer.
   * @throws Exception
   *           the exeption could be thrown by calling this method.
   */
  @Override
  protected void setUp() throws Exception {
    accessHelper = EasyMock.createMock(SecureUrlAccessHelper.class);
    EasyMock.expect(accessHelper.canAccessUrl(null, "/dummy-ctx" + GRANTED_URL))
        .andReturn(true).anyTimes();
    EasyMock.expect(accessHelper.canAccessUrl(null, "/dummy-ctx" + DENIED_URL))
        .andReturn(false).anyTimes();
    EasyMock.replay(accessHelper);
  }

  /**
   * It test the normal behavior of the MenuAccessFilterer.
   */
  public void testFilteredMenuNodes() {
    MenuNode node1 = createMock(MenuNode.class);
    MenuNode node2 = createMock(MenuNode.class);
    MenuNode node3 = createMock(MenuNode.class);
    MenuNode node4 = createMock(MenuNode.class);

    expect(node1.getLinkPath()).andReturn(GRANTED_URL);
    expect(node1.isLeaf()).andReturn(true);
    expect(node2.getLinkPath()).andReturn(GRANTED_URL);
    expect(node2.isLeaf()).andReturn(true);
    expect(node3.getLinkPath()).andReturn(GRANTED_URL);
    expect(node3.isLeaf()).andReturn(true);
    expect(node4.getLinkPath()).andReturn(GRANTED_URL);
    expect(node4.isLeaf()).andReturn(true);

    replay(node1);
    replay(node2);
    replay(node3);
    replay(node4);

    List<MenuNode> nodes = ListFactory.create(node1, node2, node3, node4);
    filterer = new MenuAccessFilterer(accessHelper);
    List<MenuNode> filteredNodes = filterer.filterMenuNodes(nodes);

    verify(node1);
    verify(node2);
    verify(node3);
    verify(node4);

    assertEquals(4, filteredNodes.size());
    assertEquals(filteredNodes.get(0), node1);
    assertEquals(filteredNodes.get(1), node2);
    assertEquals(filteredNodes.get(2), node3);
    assertEquals(filteredNodes.get(3), node4);

  }

  public void testFilteredMenuNodes_BranchWithDeniedChildren() {
    MenuNode deniedRootNode1 = createMock(MenuNode.class);
    MenuNode grantedRootNode2 = createMock(MenuNode.class);
    MenuNode grantedRootNode3 = createMock(MenuNode.class);
    MenuNode grantedRootNode4 = createMock(MenuNode.class);
    MenuNode emptyBranchNode5 = createMock(MenuNode.class);
    MenuNode deniedChildNode5_1 = createMock(MenuNode.class);
    MenuNode deniedChildNode5_2 = createMock(MenuNode.class);

    expect(deniedRootNode1.getLinkPath()).andReturn(DENIED_URL);
    expect(deniedRootNode1.isLeaf()).andReturn(true);
    expect(grantedRootNode2.getLinkPath()).andReturn(GRANTED_URL);
    expect(grantedRootNode2.isLeaf()).andReturn(true);
    expect(grantedRootNode3.getLinkPath()).andReturn(GRANTED_URL);
    expect(grantedRootNode3.isLeaf()).andReturn(true);
    expect(grantedRootNode4.getLinkPath()).andReturn(GRANTED_URL);
    expect(grantedRootNode4.isLeaf()).andReturn(true);
    expect(deniedChildNode5_1.getLinkPath()).andReturn(DENIED_URL);
    expect(deniedChildNode5_1.isLeaf()).andReturn(true);
    expect(deniedChildNode5_2.getLinkPath()).andReturn(DENIED_URL);
    expect(deniedChildNode5_2.isLeaf()).andReturn(true);
    expect(emptyBranchNode5.isLeaf()).andReturn(false);
    List<MenuNode> children = ListFactory.create(deniedChildNode5_1,
        deniedChildNode5_2);
    expect(emptyBranchNode5.getChildNodes()).andReturn(children);

    replay(deniedRootNode1);
    replay(grantedRootNode2);
    replay(grantedRootNode3);
    replay(grantedRootNode4);
    replay(emptyBranchNode5);
    replay(deniedChildNode5_1);
    replay(deniedChildNode5_2);

    List<MenuNode> nodes = ListFactory.create(deniedRootNode1,
        grantedRootNode2, grantedRootNode3, grantedRootNode4, emptyBranchNode5);
    filterer = new MenuAccessFilterer(accessHelper);
    List<MenuNode> filteredNodes = filterer.filterMenuNodes(nodes);

    verify(deniedRootNode1);
    verify(grantedRootNode2);
    verify(grantedRootNode3);
    verify(grantedRootNode4);
    verify(emptyBranchNode5);
    verify(deniedChildNode5_1);
    verify(deniedChildNode5_2);

    assertEquals(3, filteredNodes.size());
    assertEquals(filteredNodes.get(0), grantedRootNode2);
    assertEquals(filteredNodes.get(1), grantedRootNode3);
    assertEquals(filteredNodes.get(2), grantedRootNode4);
  }

  public void testFilteredMenuNodes_BranchWithGrantedAndDeniedChildren() {
    MenuNode deniedRootNode1 = createMock(MenuNode.class);
    MenuNode grantedRootNode2 = createMock(MenuNode.class);
    MenuNode grantedRootNode3 = createMock(MenuNode.class);
    MenuNode grantedRootNode4 = createMock(MenuNode.class);
    MenuNode grantedBranchNode5 = createMock(MenuNode.class);
    MenuNode deniedChildNode5_1 = createMock(MenuNode.class);
    MenuNode deniedChildNode5_2 = createMock(MenuNode.class);
    MenuNode grantedChildNode5_3 = createMock(MenuNode.class);

    expect(deniedRootNode1.getLinkPath()).andReturn(DENIED_URL);
    expect(deniedRootNode1.isLeaf()).andReturn(true);
    expect(grantedRootNode2.getLinkPath()).andReturn(GRANTED_URL);
    expect(grantedRootNode2.isLeaf()).andReturn(true);
    expect(grantedRootNode3.getLinkPath()).andReturn(GRANTED_URL);
    expect(grantedRootNode3.isLeaf()).andReturn(true);
    expect(grantedRootNode4.getLinkPath()).andReturn(GRANTED_URL);
    expect(grantedRootNode4.isLeaf()).andReturn(true);
    expect(deniedChildNode5_1.getLinkPath()).andReturn(DENIED_URL).anyTimes();
    expect(deniedChildNode5_1.isLeaf()).andReturn(true).anyTimes();
    expect(deniedChildNode5_2.getLinkPath()).andReturn(DENIED_URL).anyTimes();
    expect(deniedChildNode5_2.isLeaf()).andReturn(true).anyTimes();
    expect(grantedChildNode5_3.getLinkPath()).andReturn(GRANTED_URL).anyTimes();
    expect(grantedChildNode5_3.isLeaf()).andReturn(true).anyTimes();
    expect(grantedBranchNode5.isLeaf()).andReturn(false).anyTimes();

    List<MenuNode> children = ListFactory.create(deniedChildNode5_1,
        deniedChildNode5_2, grantedChildNode5_3);
    expect(grantedBranchNode5.getChildNodes()).andReturn(children).anyTimes();

    replay(deniedRootNode1);
    replay(grantedRootNode2);
    replay(grantedRootNode3);
    replay(grantedRootNode4);
    replay(grantedBranchNode5);
    replay(deniedChildNode5_1);
    replay(deniedChildNode5_2);
    replay(grantedChildNode5_3);

    List<MenuNode> nodes = ListFactory.create(deniedRootNode1,
        grantedRootNode2, grantedRootNode3, grantedRootNode4,
        grantedBranchNode5);
    filterer = new MenuAccessFilterer(accessHelper);
    List<MenuNode> filteredNodes = filterer.filterMenuNodes(nodes);

    assertEquals(4, filteredNodes.size());
    assertEquals(filteredNodes.get(0), grantedRootNode2);
    assertEquals(filteredNodes.get(1), grantedRootNode3);
    assertEquals(filteredNodes.get(2), grantedRootNode4);
    assertEquals(filteredNodes.get(3), grantedBranchNode5);

    List<MenuNode> filteredChildNodes = filterer
        .filterMenuNodes(grantedBranchNode5.getChildNodes());

    assertEquals(1, filteredChildNodes.size());
    assertEquals(filteredChildNodes.get(0), grantedChildNode5_3);

    verify(deniedRootNode1);
    verify(grantedRootNode2);
    verify(grantedRootNode3);
    verify(grantedRootNode4);
    verify(grantedBranchNode5);
    verify(deniedChildNode5_1);
    verify(deniedChildNode5_2);
    verify(grantedChildNode5_3);
  }

  public void testFilteredMenuNodes_DeniedLeaf() {
    MenuNode deniedRootNode1 = createMock(MenuNode.class);
    MenuNode grantedRootNode2 = createMock(MenuNode.class);
    MenuNode grantedRootNode3 = createMock(MenuNode.class);
    MenuNode grantedRootNode4 = createMock(MenuNode.class);

    expect(deniedRootNode1.getLinkPath()).andReturn(DENIED_URL);
    expect(deniedRootNode1.isLeaf()).andReturn(true);
    expect(grantedRootNode2.getLinkPath()).andReturn(GRANTED_URL);
    expect(grantedRootNode2.isLeaf()).andReturn(true);
    expect(grantedRootNode3.getLinkPath()).andReturn(GRANTED_URL);
    expect(grantedRootNode3.isLeaf()).andReturn(true);
    expect(grantedRootNode4.getLinkPath()).andReturn(GRANTED_URL);
    expect(grantedRootNode4.isLeaf()).andReturn(true);

    replay(deniedRootNode1);
    replay(grantedRootNode2);
    replay(grantedRootNode3);
    replay(grantedRootNode4);

    List<MenuNode> nodes = ListFactory.create(deniedRootNode1,
        grantedRootNode2, grantedRootNode3, grantedRootNode4);
    filterer = new MenuAccessFilterer(accessHelper);
    List<MenuNode> filteredNodes = filterer.filterMenuNodes(nodes);

    verify(deniedRootNode1);
    verify(grantedRootNode2);
    verify(grantedRootNode3);
    verify(grantedRootNode4);

    assertEquals(3, filteredNodes.size());
    assertEquals(filteredNodes.get(0), grantedRootNode2);
    assertEquals(filteredNodes.get(1), grantedRootNode3);
    assertEquals(filteredNodes.get(2), grantedRootNode4);
  }
}
