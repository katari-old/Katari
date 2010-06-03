/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.io.File;

import org.apache.wicket.spring.test.ApplicationContextMock;

import org.junit.Test;
import static org.junit.Assert.*;

public class KatariWicketTesterTest {

  @Test
  public void dumpResultTest() throws Exception {

    ApplicationContextMock appctx=new ApplicationContextMock();

    KatariWicketTester app = new KatariWicketTester(appctx);

    app.startPage(new WicketTestPage());
    app.clickLink("users:1:delete");
    app.assertRenderedPage(WicketTestPage.class);

    String fileName = "target/wicket-test/"
      + KatariWicketTesterTest.class.getName()
      + ".dumpResultTest."
      + WicketTestPage.class.getName()
      + ".html";
    File output = new File(fileName);
    assertTrue("The file " + fileName + " was not found.", output.exists());
  }
}

