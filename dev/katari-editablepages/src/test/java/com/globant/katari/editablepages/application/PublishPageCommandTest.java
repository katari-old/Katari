/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.application;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import com.globant.katari.editablepages.TestUtils;

import com.globant.katari.editablepages.domain.Page;
import com.globant.katari.editablepages.domain.PageRepository;

public class PublishPageCommandTest {

  private PublishPageCommand command;

  private PageRepository repository;

  private String siteName;

  @Before
  public final void setUp() {
    repository = (PageRepository) TestUtils
      .getServletBeanFactory().getBean("pageRepository");
    command = (PublishPageCommand) TestUtils
      .getServletBeanFactory().getBean("publishPageCommand");
    siteName = TestUtils.getSiteName();

    TestUtils.deleteTestPages();

    // Adds a pair of pages to be used in the tests.
    Page page;

    // Creates two sample pages.
    page = new Page("first.last", "page-1", "title", "content");
    repository.save(siteName, page);
    page = new Page("first.last", "page-2", "title", "content - 2");
    page.publish();
    repository.save(siteName, page);
  }

  /* Publishes a page.
   */
  @Test
  public final void testExecute() {
    Page page;
    page = repository.findPageByName(siteName,"page-1");
    assertNull(page.getContent());

    command.setId(page.getId());
    command.execute();

    page = repository.findPageByName(siteName,"page-1");
    assertEquals("content", page.getContent());
  }
}

