/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.lang;

import java.util.List;
import java.util.LinkedList;

import org.junit.Test;

import  org.apache.shindig.protocol.ProtocolException;

public class ValidateTest {

  @Test(expected = ProtocolException.class)
  public void testNotNull_null() throws ProtocolException {
    Validate.notNull((Object) null, "Message");
  }

  @Test
  public void testNotNull_notNull() throws ProtocolException {
    Validate.notNull("not null", "Message");
  }

  @Test(expected = ProtocolException.class)
  public void testIsTrue_false() throws ProtocolException {
    Validate.isTrue(false, "Message");
  }

  @Test
  public void testIsTrue_true() throws ProtocolException {
    Validate.isTrue(true, "Message");
  }

  @Test(expected = ProtocolException.class)
  public void testNotEmpty_nullString() throws ProtocolException {
    Validate.notEmpty((String) null, "Message");
  }

  @Test(expected = ProtocolException.class)
  public void testNotEmpty_emptyString() throws ProtocolException {
    Validate.notEmpty("", "Message");
  }

  @Test
  public void testNotEmpty_notEmptyString() throws ProtocolException {
    Validate.notEmpty("not empty", "Message");
  }

  @Test(expected = ProtocolException.class)
  public void testNotEmpty_nullCollection() throws ProtocolException {
    Validate.notEmpty((List<String>) null, "Message");
  }

  @Test(expected = ProtocolException.class)
  public void testNotEmpty_emptyCollection() throws ProtocolException {
    Validate.notEmpty(new LinkedList<String>(), "Message");
  }

  @Test
  public void testNotEmpty_notEmptyCollection() throws ProtocolException {
    List<String> l = new LinkedList<String>();
    l.add("entry");
    Validate.notEmpty(l, "Message");
  }
}

