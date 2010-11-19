/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.application;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.CamelContext;

import org.junit.Test;
import org.junit.Before;

import com.globant.katari.user.SpringTestUtils;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.hibernate.coreuser.DeleteMessage;

public class DeleteEventTest {
  
  private CamelContext context;
  
  @Before
  public void setUp() throws Exception {
    context = (CamelContext) SpringTestUtils.getBean("katari.eventBus");
  }

  @Test
  public void test() throws Exception {
    ProducerTemplate template = context.createProducerTemplate();
    DeleteMessage response = (DeleteMessage) template.requestBody(
        "direct:katari.user.deleteUser", new DeleteMessage(-100));
    assertThat(response.canDelete(), is(false));
  }

  public static class Listener1 {
    public DeleteMessage a(final DeleteMessage message) {
      return message.goAhead();
    }
  }
  public static class Listener2 {
    public DeleteMessage a(final DeleteMessage message) {
      if (message.getUserId() == -100) {
        return message.reject("No, I say you can't delete user.");
      } else {
        return message.goAhead();
      }
    }
  }
  public static class Listener3 {
    public DeleteMessage a(final DeleteMessage message) {
      if (message.getUserId() == -100) {
        return message.reject("You can't delete user.");
      } else {
        return message.goAhead();
      }
    }
  }
}

