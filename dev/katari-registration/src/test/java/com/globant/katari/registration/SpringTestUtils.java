package com.globant.katari.registration;

import javax.servlet.ServletContext;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.globant.katari.email.application.EmailSender;
import com.globant.katari.tools.DummySmtpServer;
import com.globant.katari.tools.SpringTestUtilsBase;

/**
 * Container for the spring module application context.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class SpringTestUtils extends SpringTestUtilsBase {

  private static final String MODULE = "classpath:applicationContext.xml";

  /** The static instance for the singleton.*/
  private static SpringTestUtils instance;

  private SpringTestUtils() {
    super(new String[] {MODULE}, null);
  }

  /** Retrieves the intance.
   * @return the instance, never null.
   */
  public static synchronized SpringTestUtils get() {
    if (instance == null) {
      instance = new SpringTestUtils();
    }
    return instance;
  }

  /** Create a new instance of the DumySmtpServer and also configure
   * the katari's email sender to point to the active smtp port binded by
   * the smtp server.
   * @return a new instance of the dummySmtpServer.
   */
  public static final DummySmtpServer createSmtpServer() {
    // the email sender is singleton
    EmailSender emailSender = (EmailSender) get().getBean(
        "katari.emailSender");
    DummySmtpServer smtpServer = DummySmtpServer.start(0);
    DirectFieldAccessor accessor = new DirectFieldAccessor(emailSender);
    accessor.setPropertyValue("smtpPort", smtpServer.getPortNumber());
    return smtpServer;
  }
}
