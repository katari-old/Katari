package com.globant.katari.report.application;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;

import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ReportDefinition;

/**
 * Tests Report Commands.
 * @author gerardo.bercovich
 */
public class RetrieveReportContentCommandTest extends TestCase {

  /** The Report repository. */
  private JasperReportRepository reportRepository;

  /** The Report definition. */
  private ReportDefinition reportDefinition;

  /** The Report content. */
  private byte[] reportContent;

  /** The Report command. */
  RetrieveReportContentCommand command;

  @Override
  protected void setUp() throws Exception {
    reportRepository = EasyMock.createNiceMock(JasperReportRepository.class);
    reportDefinition = EasyMock.createMock(ReportDefinition.class);
    reportContent = new byte[12];
    EasyMock.expect(reportDefinition.getReportContent()).andReturn(
        reportContent);
    EasyMock.expect(reportDefinition.getName()).andReturn("reporte_test");
    EasyMock.expect(reportRepository.findReportDefinitionById(12L)).andReturn(
        reportDefinition);
    EasyMock.replay(reportDefinition);
    EasyMock.replay(reportRepository);
    command = new RetrieveReportContentCommand(reportRepository);
  }

  public void testExecute_sameContent() throws Exception {
    long reportId = 12L;
    command.setReportId(reportId);
    byte[] content = command.execute();
    assertNotNull(content);
    assertSame(reportContent, content);
  }

  public void testExecute_invalidId() throws Exception {
    command.setReportId(0L);
    try {
      command.execute();
      fail("Exception expected.");
    } catch (IllegalArgumentException e) {
      // do nothing
    }
  }

}
