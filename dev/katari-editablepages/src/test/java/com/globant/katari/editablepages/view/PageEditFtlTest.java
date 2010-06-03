/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

import com.globant.katari.tools.FreemarkerTestEngine;

import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

import static org.easymock.classextension.EasyMock.*;

import org.springframework.mock.web.MockHttpServletRequest;

import com.globant.katari.editablepages.TestUtils;
import com.globant.katari.editablepages.application.SavePageCommand;
import com.globant.katari.editablepages.domain.PageRepository;

public class PageEditFtlTest {

  @Test
  public final void testPageEditFtl() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*pageName.*TestPage.*");
    valid.add(".*moduleName.*editable-pages.*");
    valid.add(".*<title>TestTitle</title>.*");
    valid.add(".*input.*name=.name.*");
    valid.add(".*input.*name=.title.*");
    valid.add(".*textarea.*name=.pageContent.*");
    valid.add(".*Test content.*");

    valid.add(".*editor.Config\\['CustomConfigurationsPath'\\].*");
    valid.add(".*= '/katari/base'.*");
    valid.add(".*editor.ToolbarSet = 'toolbar'.*");
    valid.add(".*editor.Height = '500'.*");
    valid.add(".*editor.Width = '80%'.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/editablepages/view", Locale.ENGLISH,
        buildModel(false));
    engine.runAndValidate("pageEdit.ftl", valid, invalid);
  }

  @Test
  public final void testPageEditFtl_noConfig() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*editor.ToolbarSet = 'EditablePagesMain'.*");
    valid.add(".*editor.Config\\['CustomConfigurationsPath'\\].*");
    valid.add(".*/module/editable-pages/asset/js/fckconfig.js.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");
    invalid.add(".*= '/katari/base'.*");
    invalid.add(".*editor.Height.*");
    invalid.add(".*editor.Width.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/editablepages/view", Locale.ENGLISH,
        buildModel(true));
    engine.runAndValidate("pageEdit.ftl", valid, invalid);
  }

  private Map<String, Object> buildModel(final boolean defaultConfig) {
    // Building Model
    Map<String, Object> model = new HashMap<String, Object>();

    PageRepository repository = createMock(PageRepository.class);
    String siteName = TestUtils.getSiteName();
    SavePageCommand command = new SavePageCommand(repository, siteName);
    command.setId(10000);
    command.setName("TestPage");
    command.setTitle("TestTitle");
    command.setPageContent("Test content");

    model.put("command", command);
    FckEditorConfiguration config = new FckEditorConfiguration();
    if (!defaultConfig) {
      config.setConfigurationUrl("/base");
      config.setToolbarSet("toolbar");
      config.setWidth("80%");
      config.setHeight("500");
    }
    model.put("fckEditorConfiguration", config);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("contextPath", "/katari-sample/module/editable-pages/");
    model.put("request", request);

    return model;
  }
}

