/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.util.Iterator;

import com.dumbster.smtp.SmtpMessage;

/** A mock smtp server that does not deliver mail.
 *
 * This is used to start an smtp server that simply logs all received mail to
 * the terminal.
 *
 * It is intended to be started from the command line with:
 *
 * mvn exec:java -Dexec.mainClass=com.globant.katari.tools.MockSmtpServer
 *
 * The server accepts two parameters: the port to listen on, and the dump
 * interval, in milliseconds. If no port number is specified, it listens on
 * port 3525.  If no dump interval is specified, it dumps the received email
 * every 5 seconds.
 *
 * It will start an smtp that will dump the received emails every 5 seconds.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public final class MockSmtpServer {

  /** Will dump the received emails after this many milliseconds. */
  private static final int MAIL_DUMP_INTERVAL = 5000;

  /** Will listen by default on this port. */
  private static final int DEFAULT_PORT = 3525;

  /** The smtp server, null until the call to main.
   */
  private static DummySmtpServer server = null;

  /** Private constructor, no instances of this class should be created.
   */
  private MockSmtpServer() {
  }

  /** Entry point to start the mail server.
   *
   * @param args It expects the port number (optional, defaults to 3525), and
   * the dump interval (optional, defaults to 5 seconds) in milliseconds.
   *
   * @throws Exception in case of error.
   */
  public static void main(final String[] args) throws Exception {
    int port = DEFAULT_PORT;
    int dumpInterval = MAIL_DUMP_INTERVAL;

    if(args.length > 0) {
      port = Integer.valueOf(args[0]);
    }
    if(args.length > 1) {
      dumpInterval = Integer.valueOf(args[1]);
    }

    server = DummySmtpServer.start(port);
    System.out.println("Smtp server started, listening on port: "
        + server.getPortNumber());
    int lastSize = 0;
    while(true) {
      if(server.getReceivedEmailSize() > lastSize) {
        Iterator<SmtpMessage> it = server.iterator();
        while (it.hasNext()) {
          SmtpMessage smtpMessage = (SmtpMessage) it.next();
          System.out.println("###################################");
          System.out.println(smtpMessage.getHeaderValue("Subject"));
          System.out.println("-----------------------------------");
          System.out.println(smtpMessage.getBody());
          System.out.println("###################################");
        }
      }
      lastSize = server.getReceivedEmailSize();
      Thread.sleep(dumpInterval);
    }
  }

  /** Stops the server, only useful when testing this class.
   */
  static void stop() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  /** Obtains the port number where the server bound to.
   *
   * This operation is specially useful when the server is asked to start in
   * port 0, where the operating system selects the port number.
   *
   * If you call this operation before main, it returns 0.
   *
   * @return an integer with the port number, or 0 if the mailer has not been
   * started.
   */
  public static int getPortNumber() {
    if (server == null) {
      return 0;
    } else {
      return server.getPortNumber();
    }
  }
}

