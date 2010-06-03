/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.lang.Validate;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;

import org.springframework.context.ApplicationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Support class to test wicket pages integrated in katari with spring.
 *
 * This class also dumps the generated html to target/wicket-test. The file
 * name is composed by the calling class name, the calling method and the
 * wicket page name.
 */
public class KatariWicketTester extends WicketTester {

  /** The class logger.
   */
  private static Logger log =
    LoggerFactory.getLogger(KatariWicketTester.class);

  /** Prepares a wicket tester to use a spring application context to inject
   * dependencies into pages.
   *
   * @param applicationContext The spring application context, usually an
   * instance of ApplicationContextMock. It cannot be null.
   */
  public KatariWicketTester(final ApplicationContext applicationContext) {
    Validate.notNull(applicationContext,
        "The application context cannot be null.");
    getApplication().addComponentInstantiationListener(
      new SpringComponentInjector(getApplication(), applicationContext, true));
  }

  /** Overridden to generate the html dump of processed pages.
   *
   * {@inheritDoc}
   */
  @Override
  public final void processRequestCycle(final WebRequestCycle cycle) {
    super.processRequestCycle(cycle);
    dumpResult(getServletResponse().getDocument());
  }

  /** Dumps the html result to a file named after the test method that uses
   * this WicketTester.
   *
   * @param output The html output to dump. It cannot be null.
   */
  private void dumpResult(final String output) {
    log.trace("Entering dumpResult");
    Validate.notNull(output, "The output cannot be null");

    // Creates the output directory if it does not exist.
    File directory = new File("target/wicket-test");
    directory.mkdirs();

    // Iterates the stack trace of the current thread.
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    // The first element corresponds to getStackTrace.
    int i = 0;
    // Skips until a stack frame in this class.
    for (; i < stack.length; ++i) {
      if (stack[i].getClassName().equals(getClass().getName())) {
        break;
      }
    }
    for (; i < stack.length; ++i) {
      String name = buildOutputName(stack[i]);
      log.debug("Candidate html dump file name {}", name);
      // Finds the dumpResult method call in the stack trace.
      if (isTestMethod(stack[i])) {
        // Writes the output to the file.
        name = buildOutputName(stack[i]);
        FileOutputStream stream = null;
        try {
          try {
            stream = new FileOutputStream("target/wicket-test/" + name);
            stream.write(output.getBytes());
          } finally {
            if (stream != null) {
              stream.close();
            }
          }
        } catch (RuntimeException e) {
          throw e;
        } catch (Exception e) {
          throw new RuntimeException("Error creating html dump file.", e);
        }
        break;
      }
    }
    log.trace("Leaving dumpResult");
  }

  /** Determines if the stack entry corresponds to a candidate test method.
   *
   * This implementation simply checks that the package is not
   * org.apache.wicket.util.tester and the class is not this one.
   *
   * @param entry The stack trace element used to generate the file name. It
   * cannot be null.
   *
   * @return true if the stack trace element corresponds to a calling test
   * method.
   */
  private boolean isTestMethod(final StackTraceElement entry) {
    if (entry.getClassName().equals(getClass().getName())) {
      return false;
    }
    if (entry.getClassName().startsWith("org.apache.wicket.util.tester")) {
      return false;
    }
    return true;
  }

  /** Builds an output file name base on a stack trace element.
   *
   * The name is the fully qualified class name, followed by a dot, followed by
   * the method name, with an '.html' extension.
   *
   * @param entry The stack trace element used to generate the file name. It
   * cannot be null.
   *
   * @return a string with the output file name, never returns null.
   */
  private String buildOutputName(final StackTraceElement entry) {
    Validate.notNull(entry, "The stack trace entry cannot be null.");
    return entry.getClassName() + "." + entry.getMethodName() + "."
      + getLastRenderedPage().getClass().getName() + ".html";
  }
}

