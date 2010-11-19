/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate.coreuser;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class DeleteMessageAggregatorTest {

  @Test
  public void testAggregate_nullOld() {
    CamelContext camelContext = new DefaultCamelContext();

    DeleteMessage message = new DeleteMessage(1);
    Exchange newExchange = new DefaultExchange(camelContext);
    DefaultMessage body = new DefaultMessage();
    body.setBody(message);
    newExchange.setIn(body);

    DeleteMessageAggregator aggregator = new DeleteMessageAggregator();
    Exchange result = aggregator.aggregate(null, newExchange);

    DeleteMessage resultMessage = result.getIn().getBody(DeleteMessage.class);
    assertThat(resultMessage.canDelete(), is(true));
    assertThat(resultMessage.getUserId(), is(1l));
  }

  @Test
  public void testAggregate_nonNullOld() {
    CamelContext camelContext = new DefaultCamelContext();

    DeleteMessage oldMessage = new DeleteMessage(1).reject("No, you can't.");
    Exchange oldExchange = new DefaultExchange(camelContext);
    DefaultMessage oldBody = new DefaultMessage();
    oldBody.setBody(oldMessage);
    oldExchange.setIn(oldBody);

    DeleteMessage newMessage = new DeleteMessage(1);
    Exchange newExchange = new DefaultExchange(camelContext);
    DefaultMessage newBody = new DefaultMessage();
    newBody.setBody(newMessage);
    newExchange.setIn(newBody);

    DeleteMessageAggregator aggregator = new DeleteMessageAggregator();
    Exchange result = aggregator.aggregate(oldExchange, newExchange);

    DeleteMessage resultMessage = result.getIn().getBody(DeleteMessage.class);
    assertThat(resultMessage.canDelete(), is(false));
    assertThat(resultMessage.getUserId(), is(1l));
    assertThat(resultMessage.getMessage("", ""), is("No, you can't."));
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

