/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.globant.katari.editablepages.TestUtils;

public class PageRepositoryTest {

  private PageRepository pageRepository;

  @Before
  public final void setUp() throws Exception {

    pageRepository = (PageRepository) TestUtils.getServletBeanFactory()
      .getBean("pageRepository");

    TestUtils.deleteTestPages();

    // Adds a pair of pages to be used in the tests.
    Page page;

    // Creates two sample pages.
    page = new Page("first.last", "page-1", "title", "content");
    pageRepository.save("site", page);
    page = new Page("first.last", "page-2", "title", "content - 2");
    page.publish();
    pageRepository.save("site", page);
  }

  /* Searches a page by an existing site and name.
   */
  @Test
  public final void testFindByName() {
    assertNotNull(pageRepository.findPageByName("site", "page-1"));
  }

  /* Searches a page by an existing id.
   */
  @Test
  public final void findPage() {
    long id = pageRepository.findPageByName("site", "page-1").getId();
    assertNotNull(pageRepository.findPage(id));
  }

  /* Gets a page by name and removes it, then it should no longer exits
   */
  @Test
  public final void testRemovePage() {
    Page page = pageRepository.findPageByName("site", "page-1");
    pageRepository.remove(page);
    assertNull(pageRepository.findPageByName("site", "page-1"));
  }
}

