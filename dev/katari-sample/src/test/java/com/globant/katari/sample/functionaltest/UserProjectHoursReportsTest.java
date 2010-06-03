/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.functionaltest;

import junit.framework.TestCase;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;

/** Test the hours by user and project report page.
 *
 * @author roman.cunci
 */
public class UserProjectHoursReportsTest extends TestCase {

  /** Test the hours by user and project report page.
   *
   * It checks for the title and part of the footer to be found.
   * Fails if an exception is found.
   *
   * @throws Exception when the test fails.
   */
  public final void testReportPage() throws Exception {
    WebClient webClient = SimplePageVerifier.login(
        "/module/time/userProjectHoursReport.do");
    SimplePageVerifier.verifyPage(webClient,
        "/module/time/userProjectHoursReport.do", "", HttpMethod.GET,
        "User hours by project report",
        new String[] {
          "(?s).*User hours by project report.*"},
        new String[] {
          "(?s).*Exception.*",
          "(?s).*Not Found.*"});
  }
}

