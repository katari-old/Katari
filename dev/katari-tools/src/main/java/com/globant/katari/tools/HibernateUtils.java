/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.lang.Validate;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.jdbc.util.Formatter;
import org.hibernate.jdbc.util.DDLFormatterImpl;

import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/** Utility class that creates a ddl file from an application context
 * configuration file.
 *
 * @author pablo.saavedra
 */
public final class HibernateUtils {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(HibernateUtils.class);

  /** Private contructor, this is a utility class.
   */
  private HibernateUtils() {
  }

  /** The main method, to be invoked directly by the maven-exec-plugin.
   *
   * It creates a ddl file from an application context configuration file. It
   * looks for a bean named 'katari.sessionFactory', that must be of type
   * LocalSessionFactoryBean.
   *
   * @param arguments An array of arguments. The first element is the
   * application context configuration file, the second is the ddl output file.
   * It cannot be null.
   *
   * @throws Exception in case of error.
   */
  public static void main(final String[] arguments) throws Exception {
    try {
      Validate.notNull(arguments);
      createDdlScript(arguments[1], arguments[0]);
    } catch (final Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /** Uses hibernate to create the ddl script for the mapped classes, and saves
   * it on the given fileName.
   *
   * @param fileName The output file name. It cannot be null.
   *
   * @param springConfig The spring configuration file name. It cannot be null.
   *
   * @throws IOException in case of io error.
   */
  public static void createDdlScript(final String fileName, final
      String springConfig) throws IOException {

    Validate.notNull(fileName, "The ddl output file name cannot be null.");
    Validate.notNull(springConfig, "The xml spring configuration file name"
        + " cannot be null.");

    BufferedWriter writer = null;
    FileSystemXmlApplicationContext beanFactory = null;
    try {
      writer = new BufferedWriter(new FileWriter(fileName));

      beanFactory = new FileSystemXmlApplicationContext(
          springConfig.split(","));

      LocalSessionFactoryBean sessionFactory = (LocalSessionFactoryBean)
        beanFactory.getBean("&katari.sessionFactory");

      createDdlScript(writer, sessionFactory.getConfiguration());
    } finally {
      if (writer != null) {
        IOUtils.closeQuietly(writer);
      }
      if (beanFactory != null) {
        beanFactory.close();
      }
    }
  }

  /** Generates the DDL script for the given Hibernate configuration and writes
   * it to the given {@link Writer}.
   *
   * @param out The writer to output the DDL to. It cannot be null.
   *
   * @param cfg The hibernate configuration. It cannot be null.
   *
   * @throws IOException in case of an error when writing the output.
   */
  private static void createDdlScript(final Writer out, final Configuration
      cfg) throws IOException {

    Validate.notNull(out, "The output writer cannot be null.");
    Validate.notNull(cfg, "The hibernate configuration cannot be null.");

    log.trace("Entering createDdlScript");
    String[] ddlScript = cfg.generateSchemaCreationScript(Dialect
        .getDialect(cfg.getProperties()));
    Formatter formatter = new DDLFormatterImpl();
    for (String sentence : ddlScript) {
      sentence = formatter.format(sentence);
      IOUtils.write(sentence + ";" + IOUtils.LINE_SEPARATOR, out);
    }
    log.trace("Leaving createDdlScript");
  }
}

