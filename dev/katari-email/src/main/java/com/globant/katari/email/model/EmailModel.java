package com.globant.katari.email.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

/**
 * Model of the email.
 * This class has all the information necessary to send a new template email.
 * Also contains information related to sender's / receivers's email.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class EmailModel {

  /** The model for the template. It's never null. */
  private final Map<String, Object> templateModelValues;

  /** The sender's email. It's never null. */
  private final String sender;

  /** The receivers's email. It's never null. */
  private final List<String> receivers;

  /** The email's subject. It's never null. */
  private final String subject;

  /**
   * Message if the receivers can't see HTML in the email's client.
   * Can be blank.
   */
  private String plainTextMessage;

  /** Builds a new instance of the model.
   * @param theEmailFrom the email direction of the sender. Cannot be null.
   * @param theReceivers the emails directions of the receivers. Cannot be null.
   * @param values the model (like spring mvc) for the template. Cannot be null.
   * @param plainMessage the alternative message. Cannot be empty.
   * @param emailsubject the email subject. Cannot be null.
   */
  public EmailModel(final String theEmailFrom, final List<String> theReceivers,
      final Map<String, Object> values, final String plainMessage,
      final String emailsubject) {
    Validate.notNull(theEmailFrom, "The email from cannot be null");
    Validate.notNull(theReceivers, "The email to cannot be null");
    Validate.notNull(values, "The email template model from cannot be null");
    Validate.notEmpty(plainMessage, "The plain message cannot be empty.");
    sender = theEmailFrom;
    receivers = theReceivers;
    templateModelValues = values;
    subject = emailsubject;
    plainTextMessage = plainMessage;
  }

  /** Builds a new instance of the model.
   * @param theEmailFrom the email direction of the sender. Cannot be null.
   * @param theEmailTo the email direction of the receivers. Cannot be null.
   * @param values the model (like spring mvc) for the template. Cannot be null.
   * @param plainMessage the alternative message. Cannot be empty.
   * @param emailsubject the email subject. Cannot be null.
   */
  public EmailModel(final String theEmailFrom, final String theEmailTo,
      final Map<String, Object> values, final String plainMessage,
      final String emailsubject) {
    this(theEmailFrom, buildReceiverList(theEmailTo), values, plainMessage,
        emailsubject);
  }

  /**Create a new list of email addresses with only one element.
   * @param emailTo the email to. It cannot be null.
   * @return a new list of string with only one address.
   */
  private static List<String> buildReceiverList(final String emailTo) {
    Validate.notNull(emailTo, "The receiver email address cannot be null.");
    List<String> list = new ArrayList<String>(1);
    list.add(emailTo);
    return list;
  }

  /** Returns the email template model.
   * @return the email template model. Never returns null.
   */
  public Map<String, Object> getModel() {
    return templateModelValues;
  }

  /** Returns the sender's email.
   * @return the sender's email direction. Never returns null.
   */
  public String getSender() {
    return sender;
  }

  /** Returns the reciver's email.
   * @return the receivers's email. Never returns null.
   */
  public List<String> getRecipments() {
    return receivers;
  }

  /** Returns the alternative message.
   * @return the alternative message. Never returns null.
   */
  public String getPlainTextMessage() {
    return plainTextMessage;
  }

  /** Returns the subject.
   *  @return the subject of the email. Never returns null.
   */
  public String getSubject() {
    return subject;
  }

}
