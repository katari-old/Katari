/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.editablepages.TestUtils;
import com.globant.katari.editablepages.domain.Page;
import com.globant.katari.editablepages.domain.PageRepository;
import com.globant.katari.hibernate.Transaction;

/* Tests the page controller.
 */
public class PageControllerTest {

  private PageRepository repository;

  private String siteName;

  private Transaction transaction;

  @Before
  public final void setUp() throws Exception {

    TestUtils.get().beginTransaction();

    transaction = (Transaction) TestUtils.get().getBean("katari.transaction");

    repository = TestUtils.getPageRepository();
    siteName = TestUtils.getSiteName();

    TestUtils.deleteTestPages();

    Page page;
    // Creates two sample pages.
    page = new Page("first.last", "page-1", "title", "content");
    repository.save(siteName, page);
    page = new Page("first.last", "page-2", "title", "content - 2");
    page.publish();
    repository.save(siteName, page);

    TestUtils.get().endTransaction();
  }

  /* Tests that the controller forwards the request with the correct page to
   * the correct view.
   */
  @Test
  public final void testhandleRequestInternal() throws Exception {
    PageController controller = new PageController(repository, siteName,
        transaction);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/page/page-2");
    MockHttpServletResponse response = new MockHttpServletResponse();

    ModelAndView mav = controller.handleRequestInternal(request, response);
    assertNotNull(mav);
    assertEquals("page", mav.getViewName());
    assertNotNull(mav.getModel());
    assertNotNull(mav.getModel().get("page"));
    assertEquals("page-2", ((Page) mav.getModel().get("page")).getName());
  }

  /* Tests that the controller throws an exception if the page was never
   * published.
   */
  @Test(expected = RuntimeException.class)
  public final void testhandleRequestInternal_notPublished() throws Exception {
    PageController controller = new PageController(repository, siteName,
        transaction);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/page/page-1");
    MockHttpServletResponse response = new MockHttpServletResponse();

    controller.handleRequestInternal(request, response);
  }

  /* Tests that the controller throws an exception if the page was not found.
   */
  @Test(expected = RuntimeException.class)
  public final void testhandleRequestInternal_pageNotFound() throws Exception {
    PageController controller = new PageController(repository, siteName,
        transaction);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/page/This page name is never found");
    MockHttpServletResponse response = new MockHttpServletResponse();

    controller.handleRequestInternal(request, response);
  }
}

