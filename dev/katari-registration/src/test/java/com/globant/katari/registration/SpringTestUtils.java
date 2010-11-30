package com.globant.katari.registration;

import javax.servlet.ServletContext;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.globant.katari.email.application.EmailSender;
import com.globant.katari.tools.DummySmtpServer;

/**
 * Container for the spring module application context.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class SpringTestUtils {

  private static final String MODULE = "classpath:applicationContext.xml";

  private static final SpringTestUtils INSTANCE = new SpringTestUtils();

  private final XmlWebApplicationContext appContext;

  private SpringTestUtils() {
    ServletContext sc;
    sc = new MockServletContext(".", new FileSystemResourceLoader());
    appContext = new XmlWebApplicationContext();
    appContext.setServletContext(sc);
    appContext.setConfigLocations(new String[] { MODULE });
    appContext.refresh();
  }

  /**
   * @return {@link XmlWebApplicationContext} the spring application context.
   */
  public static final XmlWebApplicationContext getContext() {
    return INSTANCE.appContext;
  }

  /** Create a new instance of the DumySmtpServer and also configure
   * the katari's email sender to point to the active smtp port binded by
   * the smtp server.
   * @return a new instance of the dummySmtpServer.
   */
  public static final DummySmtpServer createSmtpServer() {
    // the email sender is singleton
    EmailSender emailSender = (EmailSender) getContext().getBean(
        "katari.emailSender");
    DummySmtpServer smtpServer = DummySmtpServer.start(0);
    DirectFieldAccessor accessor = new DirectFieldAccessor(emailSender);
    accessor.setPropertyValue("smtpPort", smtpServer.getPortNumber());
    return smtpServer;
  }

}