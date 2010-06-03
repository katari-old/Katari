package com.globant.katari.report.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import com.globant.katari.report.ReportsTestSupport;
import com.globant.katari.report.application.GenerateReportCommand;
import com.globant.katari.report.domain.ReportType;
import com.globant.katari.tools.FreemarkerTestEngine;

public class EditParametersFtlTest extends TestCase {

  public final void testFtl() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*<title>Edit Parameters Descriptors</title>.*");
    valid.add(".*TEST_PARAM_1.*");
    valid.add(".*TEST_PARAM_3.*");
    valid.add(".*TEST_PARAM_4.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add(".*TEST_PARAM_2.*");
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/report/view", Locale.ENGLISH, buildModel());
    engine.runAndValidate("editParameters.ftl", valid, invalid);
  }

  private Map<String, Object> buildModel() {
    // Building Model
    GenerateReportCommand command = (GenerateReportCommand)
      ReportsTestSupport.getApplicationContext().getBean(
          "generateReportCommand");

    long id = ReportsTestSupport.createSampleReport().getId();

    command.setReportId(id);
    command.init();

    Map<String, Object> model = new HashMap<String, Object>();
    model.put("command", command);
    model.put("baseweb", "/");

    ReportType[] reportTypesArray = ReportType.values();
    model.put("reportTypes", reportTypesArray);

    return model;
  }
}

