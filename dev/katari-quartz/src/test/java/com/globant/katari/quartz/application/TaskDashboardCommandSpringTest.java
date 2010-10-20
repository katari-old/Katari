package com.globant.katari.quartz.application;

import java.io.StringWriter;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.*;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.core.application.JsonRepresentation;
import com.globant.katari.quartz.SpringTestUtils;

/** @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class TaskDashboardCommandSpringTest {

  private TaskDashboardCommand command;

  @Before
  public void setUp() throws Exception {
    command = (TaskDashboardCommand) SpringTestUtils.getContext().getBean(
        "taskDashboardCommand");
  }

  @Test
  public void testExecute() throws Exception {
    JsonRepresentation json = command.execute();
    StringWriter writer = new StringWriter();
    json.write(writer);
    assertThat(writer.toString(), containsString("Mock Impl"));
    assertThat(writer.toString(), containsString("friendlyName"));
  }
}

