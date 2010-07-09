/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.domain;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.MapUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.katari.report.ReportsTestSupport;

/**
 * Test class for reports.
 * @author andres.ventura
 */
public class ReportRepositoryTest
  extends AbstractTransactionalDataSourceSpringContextTests {

  private JasperReportRepository reportRepository;

  private RoleRepository roleRepository;

  /** Setups Testing roles.
   */
  @Override
  public final void onSetUpInTransaction() throws Exception {
    deleteFromTables(new String[] {
        "report_required_roles", "report_definitions", "roles"}
    );
    roleRepository.save(new Role("ADMINISTRATOR"));
    roleRepository.save(new Role("REPORT_ADMIN"));
    roleRepository.save(new Role("GUEST"));
    createSampleReport();
  }

  /**
   * Tests the creation of a report.
   */
  private void createSampleReport() throws Exception {
    ReportDefinition definition = new ReportDefinition("test", "description",
        ReportsTestSupport.getSampleReportBytes());
    Role admin = roleRepository.findRoleByName("ADMINISTRATOR");
    definition.addRole(admin);
    reportRepository.save(definition);
  }

  /**
   * Tests find by roles.
   */
  public void testFindByRoles() throws Exception {
    final List<Role> roles = roleRepository.getRoles();
    List<ReportDefinition> reports;

    reports = reportRepository.findReportsByRole(roles);
    assertNotNull(reports);
    assertEquals(1, reports.size());

    reports = reportRepository.findReportsByRole(new LinkedList<Role>());
    assertNotNull(reports);
    assertEquals(0, reports.size());
  }

  /**
   * Test get drop down options.
   */
  @SuppressWarnings("unchecked")
  public void testGetDropdownOptions() throws Exception {
    ParameterDefinition parameterDefinition =
      new ParameterDefinition("test_param",String.class.getName(), true,
          "select id as value, name as label from roles");
    List<DropdownOptions> options =
      reportRepository.getDropdownOptions(
        parameterDefinition, MapUtils.EMPTY_MAP);
    assertEquals(3, options.size());
  }

  /** Tests find by Name.
   * @throws Exception
   */
  public void testFindByName() throws Exception {
    ReportDefinition testReport = reportRepository.findReportDefinition("test");
    assertNotNull(testReport);
  }

  //***********SPRING ACCESSORS*****************
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

