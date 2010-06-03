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

import com.globant.katari.editablepages.TestUtils;

public class PreviewButtonFtlTest {

  /** Tests that the previewButton.ftl shows the preview button for an admin.
   */
  @Test
  public final void testPreviewButton_administrator() throws Exception {

    TestUtils.setRole("ROLE_ADMINISTRATOR");

    List<String> valid = new ArrayList<String>();
    valid.add(".*<div class='preview-button'>.*");
    valid.add(".*<a class='published'.*");
    valid.add(".*<a class='pending-publication'.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/editablepages/view", Locale.ENGLISH,
        buildModel());
    engine.runAndValidate("previewButton.ftl", valid, invalid);
  }

  /** Tests that the previewButton.ftl does not show the preview button for a
   * normal user.
   */
  @Test
  public final void testPreviewButton_user() throws Exception {

    TestUtils.setRole("SOME_ROLE");

    List<String> valid = new ArrayList<String>();

    List<String> invalid = new ArrayList<String>();
    invalid.add(".*<div class='preview-button'>.*");
    invalid.add(".*<a class='published'.*");
    invalid.add(".*<a class='pending-publication'.*");
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/editablepages/view", Locale.ENGLISH,
        buildModel());
    engine.runAndValidate("previewButton.ftl", valid, invalid);
  }

  private Map<String, Object> buildModel() {
    // Building Model
    Map<String, Object> model = new HashMap<String, Object>();

    Object accessHelper = TestUtils.getServletBeanFactory()
      .getBean("katari.secureUrlAccessHelper");

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("secureUrlHelper", accessHelper);
    request.setRequestURI("/a/module/editable-pages/page/page-1");
    model.put("request", request);
    model.put("base", "/a/module/editable-pages");

    return model;
  }
}

