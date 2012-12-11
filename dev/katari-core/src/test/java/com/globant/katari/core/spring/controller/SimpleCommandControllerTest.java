package com.globant.katari.core.spring.controller;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.globant.katari.core.SpringTestUtils;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class SimpleCommandControllerTest {

  private SimpleCommandController controller;

  @Before
  public void setUp() throws Exception {
    controller = (SimpleCommandController)
        SpringTestUtils.getBean("/sampleSimpleCommandController.do");
  }

  @Test
  public void testHandleRequestInternal() throws Exception {

    Long birthDay = TimeUnit.DAYS.toMillis(360 * 28);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.addParameter("name", "waabox");
    request.addParameter("age", "28");
    request.addParameter("birthday", birthDay.toString());
    MockHttpServletResponse response = new MockHttpServletResponse();
    ModelAndView mav = controller.handleRequest(request, response);

    KatariSampleCommand command;
    command = (KatariSampleCommand) mav.getModel().get("command");

    assertThat(command.getAge(), is(28));
    assertThat(command.getName(), is("waabox"));

    assertTrue(command.getBirthday().before(
        new Date(System.currentTimeMillis())));

    assertThat((ServletOutputStream) command.getOutputStream(),
        is(response.getOutputStream()));

  }

}
