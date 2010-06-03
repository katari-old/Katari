package com.globant.katari.console.application;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.Validate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/** This class provides the means to execute a Groovy script. It also exposes
 *  the application context as a script binding.
 */
public class ScriptingEngine implements ApplicationContextAware {

  /** UTF-8 encoding string, default for every encoding operation. */
  private static final String DEFAULT_ENCODING = "UTF-8";

  /** Auto flush boolean value for stream operations. */
  private static final boolean AUTO_FLUSH = true;

  /** The Spring application context to be bound to the script. */
  private ApplicationContext applicationContext;

  /** Executes the code provided as a <code>String</code> with a Groovy shell.
   * @param code The Groovy code to be executed.
   * @param output The result stream produced by the script
   * execution. It can be left empty if the script generates an error.
   * @param error The error stream produced by the script
   * execution. It will be left empty if the script doesn't generate an error.
   */
  public void execute(final String code, final OutputStream output,
      final OutputStream error) {
    // We save the original out and error streams, as groovy will be modifying
    // them.
    PrintStream originalOut = System.out;
    PrintStream originalErr = System.err;

    PrintStream outputPrintStream = null;
    PrintStream errorPrintStream = null;
    try {
      outputPrintStream = new PrintStream(output, AUTO_FLUSH,
          DEFAULT_ENCODING);
      errorPrintStream = new PrintStream(error, AUTO_FLUSH,
          DEFAULT_ENCODING);
    } catch (UnsupportedEncodingException cause) {
      throw new RuntimeException("Couldn't create the output print streams"
          + " due to the " + DEFAULT_ENCODING + " encoding not being"
          + " supported.", cause);
    }

    System.setOut(outputPrintStream);
    System.setErr(errorPrintStream);

    Binding binding = new Binding();
    binding.setProperty("applicationContext", applicationContext);
    binding.setProperty("out", outputPrintStream);
    binding.setProperty("err", errorPrintStream);

    GroovyShell groovyShell = new GroovyShell(getClass().getClassLoader(),
        binding);

    Object result = null;
    try {
      result = groovyShell.evaluate(code);
    } catch (Throwable cause) {
      errorPrintStream.println(cause.getMessage());
    } finally {
      System.setOut(originalOut);
      System.setErr(originalErr);
    }

    if (null != result) {
      outputPrintStream.println(result);
    }
  }

  /**
   * Sets the applicationContext.
   * @param theApplicationContext The application context to be bound to the
   * scripting shell. It can't be null.
   */
  public void setApplicationContext(
      final ApplicationContext theApplicationContext) {
    Validate.notNull(theApplicationContext, "The application context can't be"
    		+ " null");
    applicationContext = theApplicationContext;
  }
}