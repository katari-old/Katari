package com.globant.katari.email.application;

import static com.globant.katari.email.SpringTestUtils.getContext;
import static junit.framework.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.email.model.EmailModel;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class EmailSenderTest {

  /**  */
  private EmailSender emailSender;

  @Before
  public void setUp() throws Exception {
    emailSender = (EmailSender) getContext().getBean("katari.emailFactory");
  }

  @Test
  public void testGenerateEmail() throws Exception {
    Map<String, Object> values = new HashMap<String, Object>();
    values.put("oneKey", "a value");
    EmailModel model = new EmailModel("emiliano.arango@gmail.com", 
        "waabox@gmail.com", values, "test the empty message", "just a test");
    try {
      emailSender.send(model, "templateTestEmail.ftl");
      fail("we expect a runtime exception.");
    } catch (Exception e) {
      assertEquals("Can not send the email", e.getMessage());
    }
  }

  @Test
  public void testGenerateEmail_fail() throws Exception {
    Map<String, Object> values = new HashMap<String, Object>();
    values.put("oneKey", "a value");
    EmailModel model = new EmailModel("emiliano.arango@gmail.com", 
        "waabox@gmail.com", values, "test the empty message", "just a test");
    try {
      emailSender.send(model, null);
      fail("should fail because the view is null");
    } catch (IllegalArgumentException e) {
      
    }
    try {
      emailSender.send(null, "");
      fail("should fail because the model is null");
    } catch (IllegalArgumentException e) {
      
    }
  }

  @Test
  public void testCreateHTML() throws Exception {
    Map<String, Object> values = new HashMap<String, Object>();
    String valueToModel = "some value";
    values.put("oneKey", valueToModel);
    String output = emailSender.createHtml("templateTestEmail.ftl", values);
    assertTrue(output.contains(valueToModel));
  }

}
