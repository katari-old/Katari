/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.io.FileReader;

import junit.framework.TestCase;

/* Test the HibernateUtils.
 */
public class HibernateUtilsTest extends TestCase {

  public void testMain() throws Exception {
    HibernateUtils.main(new String[] {
      "classpath:/com/globant/katari/tools/applicationContext.xml",
        "target/test.ddl"});

    // Open the file and check for 'create table ...
    FileReader in = new FileReader("target/test.ddl");
    char[] buffer = new char[4096];
    in.read(buffer, 0, 4096);
    String file = new String(buffer);

    assertTrue(file.contains("create table clients ("));
    assertTrue(file.contains("create table projects ("));
    assertTrue(file.contains("create table activities ("));

    in.close();
  }
}

