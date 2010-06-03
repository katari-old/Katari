/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

import com.globant.katari.tools.FreemarkerTestEngine;

import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.mock.web.MockHttpServletRequest;

import com.globant.katari.editablepages.domain.Page;
import com.globant.katari.editablepages.TestUtils;

public class PageFtlTest {

  /** Tests that the page.ftl shows all the menu items.
   */
  @Test
  public final void testPageFtl_administrator() throws Exception {

    TestUtils.setRole("ROLE_ADMINISTRATOR");

    List<String> valid = new ArrayList<String>();
    valid.add(".*editable-pages/edit/create.do\".*");
    valid.add(".*editable-pages/edit/edit.do\\?id=[0-9]+\".*");
    valid.add(".*editable-pages/edit/remove.do\\?id=[0-9]+\".*");
    valid.add(".*pageName.*page-1.*");
    valid.add(".*moduleName.*editable-pages.*");
    valid.add(".*<title>title</title>.*");
    valid.add(".*The Content.*");

    List<String> invalid = new ArrayList<String>();
    // This page is published
    invalid.add(".*editable-pages/edit/publish.do.*");
    invalid.add(".*editable-pages/edit/revert.do.*");
    invalid.add(".*New content.*");
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/editablepages/view", Locale.ENGLISH,
        buildModel(true));
    engine.runAndValidate("page.ftl", valid, invalid);
  }

  /** Tests that the page.ftl shows all the menu items.
   */
  @Test
  public final void testPageFtl_administratorPublish() throws Exception {

    TestUtils.setRole("ROLE_ADMINISTRATOR");

    List<String> valid = new ArrayList<String>();
    valid.add(".*editable-pages/edit/create.do\".*");
    valid.add(".*editable-pages/edit/edit.do\\?id=[0-9]+\".*");
    valid.add(".*editable-pages/edit/remove.do\\?id=[0-9]+\".*");
    valid.add(".*editable-pages/edit/publish.do\\?id=[0-9]+\".*");
    valid.add(".*editable-pages/edit/revert.do\\?id=[0-9]+'.*");
    valid.add(".*New content.*");
    valid.add(".*The Content.*");
    valid.add(".*pending-publication.*");

    List<String> invalid = new ArrayList<String>();
    // This page is published
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/editablepages/view", Locale.ENGLISH,
        buildModel(false));
    engine.runAndValidate("page.ftl", valid, invalid);
  }

  /** Tests that the page.ftl only shows the menu items and pending publication
   * for an editor.
   */
  @Test
  public final void testPageFtl_editor() throws Exception {

    TestUtils.setRole("ROLE_EDITOR");

    List<String> valid = new ArrayList<String>();
    valid.add(".*editable-pages/edit/create.do\".*");
    valid.add(".*editable-pages/edit/edit.do\\?id=[0-9]+\".*");
    valid.add(".*editable-pages/edit/remove.do\\?id=[0-9]+\".*");
    valid.add(".*editable-pages/edit/revert.do\\?id=[0-9]+'.*");
    valid.add(".*The Content.*");
    valid.add(".*<div[^<]*class='pending-publication'>[^<]*New content[^<]*</div>.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");
    invalid.add(".*editable-pages/edit/publish.do.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/editablepages/view", Locale.ENGLISH,
        buildModel(false));
    engine.runAndValidate("page.ftl", valid, invalid);
  }

  /** Tests that the page.ftl shows the menu items and pending publication for
   * a publisher.
   */
  @Test
  public final void testPageFtl_publisher() throws Exception {

    TestUtils.setRole("ROLE_PUBLISHER");

    List<String> valid = new ArrayList<String>();
    valid.add(".*editable-pages/edit/publish.do\\?id=[0-9]+\".*");
    valid.add(".*editable-pages/edit/revert.do\\?id=[0-9]+'.*");
    valid.add(".*New content.*");
    valid.add(".*The Content.*");
    valid.add(".*pending-publication.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");
    invalid.add(".*editable-pages/edit/create.do.*");
    invalid.add(".*editable-pages/edit/edit.do.*");
    invalid.add(".*editable-pages/edit/remove.do.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/editablepages/view", Locale.ENGLISH,
        buildModel(false));
    engine.runAndValidate("page.ftl", valid, invalid);
  }

  /** Tests that the page.ftl only does not show the menu for a user.
   */
  @Test
  public final void testPageFtl_finalUser() throws Exception {

    TestUtils.setRole("ROLE_SOMEROLE");

    List<String> valid = new ArrayList<String>();
    valid.add(".*The Content.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");
    invalid.add(".*editable-pages/edit/create.do.*");
    invalid.add(".*editable-pages/edit/edit.do.*");
    invalid.add(".*editable-pages/edit/remove.do.*");
    invalid.add(".*editable-pages/edit/publish.do.*");
    invalid.add(".*editable-pages/edit/revert.do.*");
    invalid.add(".*New content.*");
    invalid.add(".*pending-publication.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/editablepages/view", Locale.ENGLISH,
        buildModel(false));
    engine.runAndValidate("page.ftl", valid, invalid);
  }

  /** Builds a model for a published page called page-1, with 'title' and 'New
   * content' as title and content, and modifies the page with 'New content' if
   * required.
   *
   * @param publish true if the page will have modified unpublished content.
   */
  private Map<String, Object> buildModel(final boolean publish) {
    // Building Model
    Map<String, Object> model = new HashMap<String, Object>();
    Page page = new Page("zzz", "page-1", "title", "The Content");
    page.publish();
    if (!publish) {
      page.modify("zzz", "page-1", "title", "New content");
    }
    model.put("page", page);

    Object accessHelper = TestUtils.getServletBeanFactory()
      .getBean("katari.secureUrlAccessHelper");

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("secureUrlHelper", accessHelper);
    request.setRequestURI("/a/module/editable-pages/page/page-1");
    model.put("request", request);
    model.put("base", "/a/module/editable-pages");
    model.put("elementId", 1);

    return model;
  }
}

