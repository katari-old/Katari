/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Test;

import java.util.List;
import java.util.LinkedList;
import java.io.File;
import java.io.StringWriter;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.globant.katari.shindig.domain.Application;

import com.globant.katari.gadgetcontainer.domain.ApplicationRepository;

import com.globant.katari.gadgetcontainer.application.ListApplicationsCommand;

public class ListApplicationsCommandTest {

  private String gadgetXmlUrl1 = "file:///" + new File(
      "target/test-classes/SampleGadget.xml").getAbsolutePath();

  private String gadgetXmlUrl2 = "file:///" + new File(
      "target/test-classes/SampleGadget2.xml").getAbsolutePath();

  @Test
  public void testExecute() throws Exception {

    List<Application> applications = new LinkedList<Application>();
    applications.add(new Application(gadgetXmlUrl1));
    applications.add(new Application(gadgetXmlUrl2));

    ApplicationRepository repository = createMock(ApplicationRepository.class);
    expect(repository.findAll()).andReturn(applications);
    replay(repository);

    ListApplicationsCommand command;
    command = new ListApplicationsCommand(repository);

    assertThat(command.execute().write(new StringWriter()).toString(),
        is(baselineJson()));

    verify(repository);
  }

  /** Creates the baseline json string, a string with a sample json object.
   *
   * @return the json string.
   *
   * @throws JSONException
   */
  private String baselineJson() throws JSONException {
    JSONArray applications = new JSONArray();

    JSONObject application = new JSONObject();
    application.put("id", 0);
    application.put("title", "Test title");
    application.put("url", gadgetXmlUrl1);
    applications.put(application);

    application = new JSONObject();
    application.put("id", 0);
    application.put("title", "Test title 2");
    application.put("url", gadgetXmlUrl2);
    applications.put(application);

    return applications.toString();
  }
}

