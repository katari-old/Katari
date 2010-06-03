/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

import java.util.Map;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.globant.katari.editablepages.TestUtils;

import org.springframework.validation.Errors;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.globant.katari.editablepages.domain.PageRepository;
import com.globant.katari.editablepages.domain.Page;

import com.globant.katari.editablepages.application.SavePageCommand;

public class PageEditControllerTest {

  private PageRepository repository;

  private PageEditController controller;

  private SavePageCommand command;

  @Before
  public void setUp() throws Exception {
    repository = TestUtils.getPageRepository();

    TestUtils.deleteTestPages();

    Page page;
    // Creates two sample pages.
    page = new Page("first.last", "page-1", "title", "content");
    repository.save("site", page);
    page = new Page("first.last", "page-2", "title", "content - 2");
    page.publish();
    repository.save("site", page);

    command = createMock(SavePageCommand.class);

    controller = new PageEditController(new FckEditorConfiguration()) {
      protected Object createCommandBean() {
        return command;
      }
    };
  }

  /* Simulates a GET request and tests the result.
   */
  @Test
  public void testGet() throws Exception {

    // Sets up the request.
    Page page = repository.findPageByName("site","page-2");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.addParameter("id", String.valueOf(page.getId()));

    // Sets up the response.
    MockHttpServletResponse response = new MockHttpServletResponse();

    // Prepares the command.
    command.init();
    // BE VERY CAREFUL. If you remove this, verify wont complain, probably
    // because spring uses reflection to invoke getId();
    command.setId(page.getId());
    replay(command);
    controller.handleRequest(request, response);
    verify(command);
  }

  /* Simulates a POST request and tests the result.
   */
  @Test
  public void testPost() throws Exception {

    // Sets up the request.
    Page page = repository.findPageByName("site","page-2");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("POST");
    request.addParameter("id", String.valueOf(page.getId()));

    // Sets up the response.
    MockHttpServletResponse response = new MockHttpServletResponse();

    expect(command.execute()).andReturn("page-2");
    // BE VERY CAREFUL. If you remove this, verify wont complain, probably
    // because spring uses reflection to invoke getId();
    command.setId(page.getId());
    replay(command);
    controller.handleRequest(request, response);
    verify(command);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void onSubmit_elementId() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    Errors errors = createNiceMock(Errors.class);

    Map model;

    model = controller.referenceData(request, command, errors);
    assertEquals(1l, model.get("elementId"));

    model = controller.referenceData(request, command, errors);
    assertEquals(2l, model.get("elementId"));
  }
}

