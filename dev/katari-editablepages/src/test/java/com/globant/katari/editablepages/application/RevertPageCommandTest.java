/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.application;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import com.globant.katari.editablepages.TestUtils;

import com.globant.katari.editablepages.domain.Page;
import com.globant.katari.editablepages.domain.PageRepository;

public class RevertPageCommandTest {

  private RevertPageCommand command;

  private PageRepository repository;

  private String siteName;

  @Before
  public final void setUp() {
    repository = (PageRepository) TestUtils
      .getServletBeanFactory().getBean("pageRepository");
    command = (RevertPageCommand) TestUtils
      .getServletBeanFactory().getBean("revertPageCommand");
    siteName = TestUtils.getSiteName();

    TestUtils.deleteTestPages();

    // Adds a pair of pages to be used in the tests.
    Page page;

    // Creates two sample pages.
    page = new Page("first.last", "page-1", "title", "content");
    repository.save(siteName, page);
    page = new Page("first.last", "page-2", "title", "content - published");
    page.publish();
    page.modify("first.last", "page-2", "title", "content - modified");
    repository.save(siteName, page);
  }

  @Test
  public final void testExecute() {
    Page page;
    page = repository.findPageByName(siteName, "page-2");
    assertNotNull(page.getContent());

    command.setId(page.getId());
    assertEquals("page-2", command.execute());

    page = repository.findPageByName(siteName, "page-2");
    assertEquals("content - published", page.getContent());
  }
}

