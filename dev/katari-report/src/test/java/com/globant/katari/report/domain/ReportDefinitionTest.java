/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.domain;

import junit.framework.TestCase;

import com.globant.katari.report.ReportsTestSupport;

/**
 * Tests Report Definition.
 * @author gerardo.bercovich
 */
public class ReportDefinitionTest extends TestCase {

  /** Tests parsing of the XML file.
   */
  public void testXMLParameterParsing() throws Exception {

    // Creates a not initialized repository. The following call does not really
    // need arepo, so it does not matter.
    ReportDefinition rd = new ReportDefinition("Test_report", "description",
        ReportsTestSupport.getSampleReportBytes());
    assertEquals(4, rd.getParameterDefinitions().size());
  }

  public void testGetParameterDefinitions_reload() throws Exception {
    String report2 =  "<?xml version='1.0' encoding='UTF-8'  ?>"
      + "<!DOCTYPE jasperReport PUBLIC '//JasperReports//DTD"
      + " Report Design//EN'"
      + " 'http://jasperreports.sourceforge.net/dtds/jasperreport.dtd'>"
      + "<jasperReport name='test'>"
      + "<parameter name='p_1' isForPrompting='true' class='java.lang.String'/>"
      + "<parameter name='p_2' isForPrompting='true' class='java.lang.String'/>"
      + "</jasperReport>";
    ReportDefinition rd = new ReportDefinition("Test_report", "description",
        report2.getBytes());
    assertEquals(2, rd.getParameterDefinitions().size());

    String report1 =  "<?xml version='1.0' encoding='UTF-8'  ?>"
      + "<!DOCTYPE jasperReport PUBLIC '//JasperReports//DTD"
      + " Report Design//EN'"
      + " 'http://jasperreports.sourceforge.net/dtds/jasperreport.dtd'>"
      + "<jasperReport name='test'>"
      + "<parameter name='p_1' isForPrompting='true' class='java.lang.String'/>"
      + "</jasperReport>";
    rd.setReportContent(report1.getBytes());
    assertEquals(1, rd.getParameterDefinitions().size());
  }
}

