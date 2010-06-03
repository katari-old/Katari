package com.globant.katari.report.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import com.globant.katari.report.ReportsTestSupport;
import com.globant.katari.report.application.SaveReportCommand;
import com.globant.katari.tools.FreemarkerTestEngine;

public class EditReportFtlTest extends TestCase {

  public final void testFtl() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*<title>Add Report Definition</title>.*");
    valid.add(".*Report Name.*");
    valid.add(".*Report Description.*");
    valid.add(".*Select File Location.*");
    valid.add(".*Roles.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/report/view", Locale.ENGLISH, buildModel());
    engine.runAndValidate("editReport.ftl", valid, invalid);
  }

  private Map<String, Object> buildModel() {
    // Building Model
    SaveReportCommand command = (SaveReportCommand)
      ReportsTestSupport.getApplicationContext().getBean(
          "saveReportCommand");

    ReportsTestSupport.createSampleReport().getId();

    command.init();

    Map<String, Object> model = new HashMap<String, Object>();
    model.put("command", command);
    model.put("baseweb", "/");

    return model;
  }
}

