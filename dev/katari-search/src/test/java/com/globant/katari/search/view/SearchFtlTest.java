/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.view;

import com.globant.katari.tools.FreemarkerTestEngine;

import org.junit.Test;
import org.junit.Before;
import static org.easymock.classextension.EasyMock.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.mock.web.MockHttpServletRequest;

import com.globant.katari.core.security.SecureUrlAccessHelper;

import com.globant.katari.tools.SecurityTestUtils;

import com.globant.katari.search.application.SearchCommand;

import com.globant.katari.search.domain.SearchResultElement;
import com.globant.katari.search.domain.Action;

public class SearchFtlTest {

  @Before
  public final void setUp() {
    SecurityTestUtils.fakeUser("admin", "ROLE_ADMINISTRATOR");
  }

  /** Tests that the search.ftl shows all the results.
   */
  @Test
  public final void testSearchFtl() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*/user\\.do\\?id=1.*");
    valid.add(".*/userEdit\\.do\\?id=1.*");
    valid.add(".*/userDelete\\.do\\?id=1.*");
    valid.add(".*/user\\.do\\?id=2.*");
    valid.add(".*/userEdit\\.do\\?id=2.*");
    valid.add(".*/userDelete\\.do\\?id=2.*");
    valid.add(".*User - name: name 1, email: email 1.*");
    valid.add(".*User - name: name 2, email: email 2.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/search/view", Locale.ENGLISH, buildModel(true));
    engine.runAndValidate("search.ftl", valid, invalid);
  }

  /** Tests that the search.ftl does not show actions not allowed.
   */
  @Test
  public final void testSearchFtl_notAllowed() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*/user\\.do\\?id=1.*");
    valid.add(".*/user\\.do\\?id=2.*");
    valid.add(".*User - name: name 1, email: email 1.*");
    valid.add(".*User - name: name 2, email: email 2.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");
    invalid.add(".*/userEdit\\.do\\?id=1.*");
    invalid.add(".*/userDelete\\.do\\?id=1.*");
    invalid.add(".*/userEdit\\.do\\?id=2.*");
    invalid.add(".*/userDelete\\.do\\?id=2.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/search/view", Locale.ENGLISH, buildModel(false));
    engine.runAndValidate("search.ftl", valid, invalid);
  }

  /** Builds a model for a search result with 2 users (name 1 and name 2), and
   * the SearchCommand.
   */
  private Map<String, Object> buildModel(final boolean allow) {
    Map<String, Object> model = new HashMap<String, Object>();

    ArrayList<Action> actions;
    actions = new ArrayList<Action>();
    actions.add(new Action("Edit", null, "/userEdit.do?id=1"));
    actions.add(new Action("Delete", null, "/userDelete.do?id=1"));
    SearchResultElement element1 = new SearchResultElement("User", "name 1",
      "User - name: name 1, email: email 1", "/user.do?id=1", actions, 100);

    actions = new ArrayList<Action>();
    actions.add(new Action("Edit", null, "/userEdit.do?id=2"));
    actions.add(new Action("Delete", null, "/userDelete.do?id=2"));
    SearchResultElement element2 = new SearchResultElement("User", "name 2",
      "User - name: name 2, email: email 2", "/user.do?id=2", actions, 100);

    List<SearchResultElement> elements= new ArrayList<SearchResultElement>();
    elements.add(element1);
    elements.add(element2);

    SearchCommand command = createMock(SearchCommand.class);
    expect(command.getQuery()).andReturn("user").anyTimes();
    expect(command.getPageNumber()).andReturn(1).anyTimes();
    expect(command.getTotalPages()).andReturn(2).anyTimes();
    replay(command);

    model.put("command", command);
    model.put("searchResults", elements);

    SecureUrlAccessHelper helper = createMock(SecureUrlAccessHelper.class);
    expect(helper.canAccessUrl(isA(String.class), isA(String.class)))
      .andReturn(allow).anyTimes();
    replay(helper);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("secureUrlHelper", helper);
    request.setRequestURI("/a/module/search/search.do");
    model.put("request", request);

    return model;
  }
}

