/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.domain;

import org.junit.Test;
import static org.junit.Assert.*;

/* This class represents a TestCase of the page. In this class we will test all
 * the features of the page.
 */
public class PageTest {

  /* Create a page and validates that it is dirty.
   */
  @Test
  public final void createPage() {
    Page page = new Page("first.last", "title", "page1", "content");

    assertTrue(page.isDirty());
    assertNull(page.getContent());
    assertNull(page.getPublicationDate());
    assertEquals("content", page.getUnpublishedContent());
  }

  /* Publishes a page and verify that the publication information is correct.
   */
  @Test
  public final void testPublish() {
    Page page = new Page("first.last", "title", "page1", "content");
    page.publish();

    assertFalse(page.isDirty());
    assertNull(page.getUnpublishedContent());
    assertNotNull(page.getPublicationDate());
    assertEquals("content", page.getContent());
    assertEquals("first.last", page.getModifier());
  }

  /* Modify a published page, the unpublished data must change, the published
   * data must not change, the page is now dirty
   */
  @Test
  public final void testModify() {
    Page page = new Page("first.last", "title", "page1", "content");
    page.publish();
    page.modify("first1.last1", "page1", "title", "new content");

    assertEquals("content", page.getContent());
    assertEquals("new content", page.getUnpublishedContent());
    assertTrue(page.isDirty());
    assertEquals("first1.last1", page.getModifier());
  }

  @Test
  public final void testRevert() {
    Page page = new Page("first.last", "title", "page1", "content");
    page.publish();
    page.modify("first1.last1", "page1", "title", "new content");
    assertTrue(page.isDirty());
    page.revert();
    assertFalse(page.isDirty());
  }
}

