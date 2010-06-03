/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.application;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import com.globant.katari.editablepages.TestUtils;

import com.globant.katari.editablepages.domain.Page;
import com.globant.katari.editablepages.domain.PageRepository;

public class SavePageCommandTest {

  private SavePageCommand command;

  private PageRepository repository;

  private String siteName;

  @Before
  public final void setUp() {
    repository = (PageRepository) TestUtils
      .getServletBeanFactory().getBean("pageRepository");
    command = (SavePageCommand) TestUtils
      .getServletBeanFactory().getBean("editPageCommand");
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

  /* Tests the init operation.
   */
  @Test
  public final void testInit() {
    Page page;
    page = repository.findPageByName(siteName, "page-1");

    command.setId(page.getId());
    command.init();
    assertEquals("content", command.getPageContent());
    assertEquals("page-1", command.getName());
    assertEquals("title", command.getTitle());
    assertEquals("page-1", command.getOriginalName());
  }

  /* Edit the name and content of an existing unpublished page.
   */
  @Test
  public final void testExecute_editUnpublishedPage() {
    Page page;
    page = repository.findPageByName(siteName, "page-1");

    command.setId(page.getId());
    command.init();
    assertEquals("content", command.getPageContent());

    command.setName("new non-existing page name");
    command.setPageContent("new content");
    command.execute();

    page = repository.findPageByName("default", "new non-existing page name");
    assertNotNull(page);
    assertNull(page.getContent());
    assertEquals("new content", page.getUnpublishedContent());
    command.execute();

    command.init();
    assertEquals("new content", command.getPageContent());
  }

  /* Create
   */
  @Test
  public final void testExecute_create() {
    command.init();

    command.setName("a new inexisting page");
    command.setTitle("new title");
    command.setPageContent("The content");

    command.execute();

    Page page;
    page = repository.findPageByName(siteName, "a new inexisting page");
    assertNotNull(page);
    assertEquals("The content", page.getUnpublishedContent());
  }
}

