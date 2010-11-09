/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.email.application;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.globant.katari.email.model.EmailModel;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/** Class to send template based emails.
 *
 * With this class, you can send emails in html and plain text, based in a
 * freemarker template.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class EmailSender {

  /** The smtp hostname, never null. */
  private final String hostname;

  /** The smtp post, never null. */
  private final Integer smtpPort;

  /** The account name, never null. */
  private final String username;

  /** The account password, never null. */
  private final String password;

  /** true if the server is secure TLS. */
  private final boolean secure;

  /** Freemarker configuration. */
  private final Configuration freemarkerConfiguration;

  /** Builds a new instance of the configurer with all his dependencies.
   *
   * @param host the smtp hostname. Cannot be null.
   * @param port the smtp port.
   * @param user the email account user. Cannot be null.
   * @param passwd the password of the email account. Cannot be null.
   * @param isSecure true if the server is TLS.
   *
   * @param templateConfig the Freemarker freemarkerConfiguration. It cannot be
   * null.
   */
  public EmailSender(final String host, final Integer port,
      final String user, final String passwd, final boolean isSecure,
      final Configuration templateConfig) {
    Validate.notNull(host, "The hostname cannot be null");
    Validate.notNull(port, "The port cannot be null");
    Validate.notNull(user, "The username cannot be null");
    Validate.notNull(passwd, "The password cannot be null");
    Validate.notNull(templateConfig,
        "The freemarker configuration cannot be null");
    hostname = host;
    smtpPort = port;
    username = user;
    password = passwd;
    secure = isSecure;
    freemarkerConfiguration = templateConfig;
  }

  /** Create a new instance of an email with all the server information.
   * If the boolean value "send" is set to true, will invoke the email's send
   * method.
   *
   * @param emailModel the model of the email. Cannot be null.
   * @param template the template name. Cannot be null.
   */
  public void send(final EmailModel emailModel, final String template) {
    Validate.notNull(emailModel, "The email model cannot be null");
    Validate.notNull(template, "The template cannot be null");
    HtmlEmail email = new HtmlEmail();
    try {
      email.setHostName(hostname);
      email.setSmtpPort(smtpPort);
      email.setAuthenticator(new DefaultAuthenticator(username, password));
      email.setTLS(secure);
      for(String recipment : emailModel.getRecipments()) {
        email.addTo(recipment);
      }
      email.setSubject(emailModel.getSubject());
      email.setFrom(emailModel.getSender());
      email.setHtmlMsg(createHtml(template, emailModel.getModel()));
      email.setTextMsg(emailModel.getPlainTextMessage());
    } catch (final EmailException e) {
      throw new RuntimeException(e);
    }
    try {
      email.send();
    } catch (final EmailException e) {
      throw new RuntimeException("Can not send the email", e);
    }
  }

  /** Generates a new HTML representation from the given model and template.
   *
   * @param templateName the template name.
   * @param model the model of the template.
   * @return the result of the template's processing.
   */
  private String createHtml(final String templateName,
      final Map<String, Object> model) {
    try {
      StringWriter writer = new StringWriter();
      Template template = freemarkerConfiguration.getTemplate(templateName);
      template.process(model, writer);
      String out = writer.toString();
      IOUtils.closeQuietly(writer);
      return out;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    }
  }
}

