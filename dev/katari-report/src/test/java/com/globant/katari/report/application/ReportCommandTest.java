/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.application;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.katari.report.ReportsTestSupport;
import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ReportDefinition;

/** Tests Report Commands.  @author gerardo.bercovich
 */
public class ReportCommandTest extends
    AbstractTransactionalDataSourceSpringContextTests {

  /** The Report repository. */
  private JasperReportRepository reportRepository;

  /** The Role repository. */
  private RoleRepository roleRepository;

  @Override
  protected void onSetUp() throws Exception {
    ReportsTestSupport.initTestReportSecurityContext("ADMINISTRATOR");
  }

  /**
   * Tests the deletion of a report.
   */
  public void testReportDeletion() throws Exception {
    int reportsCount = reportRepository.getReportList().size();

    saveTestReportForAdmin("test_ReportTest", "Report Description");

    ReportDefinition rd = reportRepository
        .findReportDefinition("test_ReportTest");
    DeleteReportCommand deleteReportCmd = (DeleteReportCommand) this
        .getApplicationContext().getBean("deleteReportCommand");
    deleteReportCmd.setReportId(rd.getId());
    deleteReportCmd.execute();

    assertEquals(reportsCount, reportRepository.getReportList().size());
  }

  public void testReportsCommand() throws Exception {
    int reportsCount = reportRepository.getReportList().size();
    saveTestReportForAdmin("Test report 1", "Report Description");
    saveTestReportForAdmin("Test report 2", "Report Description");
    ReportsCommand command = (ReportsCommand) this
    .getApplicationContext().getBean("reportsCommand");
    final List<ReportDefinition> reports = command.execute();
    assertEquals(reportsCount + 2, reports.size());
  }

  public void testSaveReportCommand() throws Exception {
    saveTestReportForAdmin("Test report 7", "Report Description 7");
    SaveReportCommand command = (SaveReportCommand)
    getApplicationContext().getBean("saveReportCommand");
    long reportId = reportRepository.findReportDefinition("Test report 7")
        .getId();
    command.setReportId(reportId);
    command.init();
    assertEquals("Test report 7", command.getName());
    assertEquals("Report Description 7", command.getDescription());
  }

  /**
   * Save a report with the {@link SaveReportCommand}.
   */
  private void saveTestReportForAdmin(final String reportName,
      final String description) throws Exception {
    SaveReportCommand command = (SaveReportCommand) this
        .getApplicationContext().getBean("saveReportCommand");
    command.init();
    command.setName(reportName);
    command.setDescription(description);
    command.setReportContent(ReportsTestSupport.getSampleReportBytes());
    final Map<String, String> availableRoles = command.getAvailableRoles();
    final String firstId = availableRoles.keySet().iterator().next();
    final LinkedList<String> linkedList = new LinkedList<String>();
    linkedList.add(firstId);
    command.setRoleIds(linkedList);
    command.execute();
  }

  public void testInit_inexistentReport() throws Exception {
    SaveReportCommand command = (SaveReportCommand) this
        .getApplicationContext().getBean("saveReportCommand");
    // Inexistent ID
    command.setReportId(Long.MAX_VALUE);
    try {
      command.init();
      fail("If specified report id does not exist, that must cause "
          + "an Exception");
    } catch (RuntimeException e) {
    }
  }

  public void testValidate_valid() throws Exception {
    SaveReportCommand command = (SaveReportCommand)
      getApplicationContext().getBean("saveReportCommand");

    command.setName("a name");
    command.setDescription("a description");
    command.setReportContent(ReportsTestSupport.getSampleReportBytes());
    Errors errors = new BindException(command, "command");
    command.validate(errors);
    assertTrue(!errors.hasErrors());
  }

  /* Creates a new report and uploads an empty file.
   */
  public void testValidate_emptyContent() throws Exception {
    SaveReportCommand command = (SaveReportCommand)
      getApplicationContext().getBean("saveReportCommand");

    command.setName("a name");
    command.setDescription("a description");
    command.setReportContent(new byte[0]);
    Errors errors = new BindException(command, "command");
    command.validate(errors);
    assertTrue(errors.hasErrors());
  }

  public void testValidate_invalidContent() throws Exception {
    SaveReportCommand command = (SaveReportCommand)
      getApplicationContext().getBean("saveReportCommand");
    command.setName("a name");
    command.setDescription("a description");
    command.setReportContent(new byte[]{'a', 'b'});
    Errors errors = new BindException(command, "command");
    command.validate(errors);
    assertTrue(errors.hasErrors());
  }

  public void testValidateModify_emptyContent() throws Exception {

    saveTestReportForAdmin("Test report 3", "Report Description");
    long reportId = reportRepository.findIdForName("Test report 3");

    SaveReportCommand command = (SaveReportCommand)
      getApplicationContext().getBean("saveReportCommand");

    command.setReportId(reportId);
    command.setName("a name");
    command.setDescription("a description");
    Errors errors = new BindException(command, "command");
    command.validate(errors);
    assertTrue(!errors.hasErrors());
  }

  // ***********SPRING ACCESSORS*****************

  public JasperReportRepository getReportRepository() {
    return reportRepository;
  }

  public void setReportRepository(final JasperReportRepository reportRepository) {
    this.reportRepository = reportRepository;
  }

  public RoleRepository getRoleRepository() {
    return roleRepository;
  }

  public void setRoleRepository(final RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  protected ConfigurableApplicationContext loadContext(final Object key) throws
      Exception {
    return ReportsTestSupport.getApplicationContext();
  }

  @Override
  protected Object contextKey() {
    return "notnull";
  }
}

