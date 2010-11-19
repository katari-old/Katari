/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.application;

import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.user.application.DeleteMessage;

public class DeleteMessageTest {

  @Test
  public void testGetUserId() {
    DeleteMessage message = new DeleteMessage(1);
    assertThat(message.getUserId(), is(1l));
  }

  @Test
  public void testGoAhead() {
    DeleteMessage message = new DeleteMessage(1);
    assertThat(message.goAhead().canDelete(), is(true));
  }

  @Test
  public void testReject() {
    DeleteMessage message = new DeleteMessage(1);
    message = message.reject("No, you can't");
    assertThat(message.canDelete(), is(false));
    assertThat(message.getMessage("<", ">"), is("<No, you can't>"));
  }

  @Test
  public void testAggregate_reject_goAhead() {
    DeleteMessage message = new DeleteMessage(1);
    DeleteMessage aggregated = message.reject("No, you can't");
    DeleteMessage message1 = message.goAhead();

    aggregated = aggregated.aggregate(message1);

    assertThat(aggregated.canDelete(), is(false));
    assertThat(aggregated.getMessage("<", ">"), is("<No, you can't>"));
  }

  @Test
  public void testAggregate_goAhead_reject() {
    DeleteMessage message = new DeleteMessage(1);
    DeleteMessage aggregated = message.goAhead();
    DeleteMessage message1 = message.reject("No, you can't");

    aggregated = aggregated.aggregate(message1);

    assertThat(aggregated.canDelete(), is(false));
    assertThat(aggregated.getMessage("<", ">"), is("<No, you can't>"));
  }

  @Test
  public void testAggregate_multipleRejects() {
    DeleteMessage message = new DeleteMessage(1);
    DeleteMessage aggregated = message.reject("Nope");
    DeleteMessage message1 = message.reject("No, you can't");
    DeleteMessage message2 = message.reject("No.");

    aggregated = aggregated.aggregate(message1);
    aggregated = aggregated.aggregate(message2);

    assertThat(aggregated.canDelete(), is(false));
    assertThat(aggregated.getMessage("<", ">"),
        is("<Nope><No, you can't><No.>"));
  }
}

