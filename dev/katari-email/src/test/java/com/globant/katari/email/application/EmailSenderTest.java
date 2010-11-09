/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.email.application;

import static com.globant.katari.email.SpringTestUtils.getContext;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.springframework.beans.DirectFieldAccessor;

import com.globant.katari.tools.DummySmtpServer;
import com.globant.katari.email.model.EmailModel;

/** @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class EmailSenderTest {

  private static final String TEMPLATE = 
      "com/globant/katari/email/view/templateTestEmail.ftl";

  private DummySmtpServer smtpServer;
 
  private EmailSender emailSender;

  @Before
  public void setUp() throws Exception {
    emailSender = (EmailSender) getContext().getBean("katari.emailSender");
    smtpServer = DummySmtpServer.start(0);
    DirectFieldAccessor accessor = new DirectFieldAccessor(emailSender);
    accessor.setPropertyValue("smtpPort", smtpServer.getPortNumber());
  }

  @After
  public void tearDown() {
    smtpServer.stop();
  }

  @Test
  public void testSend_success() throws Exception {
    Map<String, Object> values = new HashMap<String, Object>();
    values.put("oneKey", "a value");
    EmailModel model = new EmailModel("emiliano.arango@gmail.com", 
        "waabox@gmail.com", values, "plain text body", "the subject");
    emailSender.send(model, TEMPLATE);

    assertThat(smtpServer.getReceivedEmailSize(), is(1));

    String mailBody = smtpServer.iterator().next().getBody();
    String subject = smtpServer.iterator().next().getHeaderValue("Subject");
    assertThat(mailBody, containsString("a value"));
    assertThat(mailBody, containsString("just a test"));
    assertThat(mailBody, containsString("plain text"));
    assertThat(subject, is("the subject"));
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
}

