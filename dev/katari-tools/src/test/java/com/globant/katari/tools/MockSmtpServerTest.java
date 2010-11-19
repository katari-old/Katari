/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;


public class MockSmtpServerTest {

  PrintStream originalOut;
  
  ByteArrayOutputStream dataSent;

  int port;

  @Before
  public void setUp() throws IOException {
    originalOut = System.out;
    dataSent = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(dataSent);
    System.setOut(ps);
    ServerSocket serverSocket = new ServerSocket(0);
    port = serverSocket.getLocalPort();
    serverSocket.setReuseAddress(true);
    serverSocket.close();
  }

  @After
  public void tearDown() {
    if (originalOut != null) {
      System.setOut(originalOut);
    }
  }

  @Test
  public void testSend() throws Exception {
    Thread smtpThread = new Thread(new Runnable() {
      public void run() {
        try {
          MockSmtpServer.main(new String[]{String.valueOf(port), "1"});
        } catch (Exception e) {
        }
        return;
      }
    });
    smtpThread.start();

    int count = 5;
    while( ! isOpened(port) && count > 0) {
      count --;
      Thread.sleep(10);
    }

    // send a mail and verify that it is received.
    Properties props = new Properties();
    props.put("mail.smtp.host", "localhost");
    props.put("mail.smtp.port", String.valueOf(port));

    Session mailSession = Session.getInstance(props);
    Message sentMessage = new MimeMessage(mailSession);
    sentMessage.setFrom(new InternetAddress("from@blah"));
    sentMessage.setRecipient(RecipientType.TO, new InternetAddress("to@blah"));
    sentMessage.setSubject("the subject");
    sentMessage.setText("the body");
    Transport.send(sentMessage);

    Thread.sleep(500);
    
    assertThat(dataSent.toString(), containsString("the subject"));
    assertThat(dataSent.toString(), containsString("the body"));
  }

  public static boolean isOpened(int port) throws IOException {
    ServerSocket ssocket = null;
    try {
      ssocket = new ServerSocket(port);
      return false;
    } catch (IOException ex) {
      return true;
    } finally {
      if (ssocket != null) {
        ssocket.close();
      }
    }
  }
}

