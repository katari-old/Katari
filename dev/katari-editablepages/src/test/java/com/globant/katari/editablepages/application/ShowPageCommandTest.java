/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.application;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import com.globant.katari.editablepages.TestUtils;

import com.globant.katari.editablepages.domain.Page;
import com.globant.katari.editablepages.domain.PageRepository;

public class ShowPageCommandTest {

  private ShowPageCommand command;

  private PageRepository repository;

  private String siteName;

  @Before
  public final void setUp() {
    repository = (PageRepository) TestUtils
      .getServletBeanFactory().getBean("pageRepository");
    command = (ShowPageCommand) TestUtils
      .getServletBeanFactory().getBean("showPageCommand");

    siteName = TestUtils.getSiteName();
    
    TestUtils.deleteTestPages();

    // Adds a page to be used in the tests.
    Page page;
   
    // Creates a sample page.
    page = new Page("first.last", "page-2", "title", "content - 2");
    page.publish();
    repository.save(siteName, page);
  }

  /* Tests the init operation.
   */
  @Test
  public final void testInit() {
    Page page;
    page = repository.findPageByName(siteName, "page-2");

    command.setInstance(page.getName());
    command.init();
    assertNull(command.getUnpublishedContent());
    assertEquals("content - 2", command.getContent());
    assertEquals("title", command.getTitle());
    assertEquals("page-2", command.getName());
  }
}

