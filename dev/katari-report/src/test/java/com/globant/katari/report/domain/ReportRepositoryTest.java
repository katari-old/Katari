/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.domain;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import org.apache.commons.collections.MapUtils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.katari.report.ReportsTestSupport;

/** Test class for reports.
 * @author andres.ventura
 */
public class ReportRepositoryTest {

  private JasperReportRepository reportRepository;

  private RoleRepository roleRepository;

  private Session session;

  @Before
  public void setUp() throws Exception {
    reportRepository = ReportsTestSupport.getRepository();
    roleRepository = (RoleRepository) ReportsTestSupport
      .getApplicationContext().getBean("coreuser.roleRepository");

    session = ((SessionFactory) ReportsTestSupport.getApplicationContext()
        .getBean("katari.sessionFactory")).openSession();

    session.createSQLQuery("delete from report_required_roles")
      .executeUpdate();
    session.createQuery("delete ReportDefinition").executeUpdate();
    session.createQuery("delete Role").executeUpdate();

    roleRepository.save(new Role("ADMINISTRATOR"));
    roleRepository.save(new Role("REPORT_ADMIN"));
    roleRepository.save(new Role("GUEST"));

    createSampleReport();
  }

  /** Creates a sample report called test.
   */
  private void createSampleReport() throws Exception {
    ReportDefinition definition = new ReportDefinition("test", "description",
        ReportsTestSupport.getSampleReportBytes());
    Role admin = roleRepository.findRoleByName("ADMINISTRATOR");
    definition.addRole(admin);
    reportRepository.save(definition);
  }

  @Test
  public void testFindReportsByRole() throws Exception {
    final List<Role> roles = roleRepository.getRoles();
    List<ReportDefinition> reports;

    reports = reportRepository.findReportsByRole(roles);
    assertNotNull(reports);
    assertEquals(1, reports.size());

    reports = reportRepository.findReportsByRole(new LinkedList<Role>());
    assertNotNull(reports);
    assertEquals(0, reports.size());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetDropdownOptions() throws Exception {
    ParameterDefinition parameterDefinition =
      new ParameterDefinition("test_param",String.class.getName(), true,
          "select id as value, name as label from roles");
    List<DropdownOptions> options =
      reportRepository.getDropdownOptions(
        parameterDefinition, MapUtils.EMPTY_MAP);
    assertEquals(3, options.size());
  }

  @Test
  public void testFindReportDefinition() throws Exception {
    ReportDefinition testReport = reportRepository.findReportDefinition("test");
    assertNotNull(testReport);
  }

  @After
  public void tearDown() {
    session.close();
  }
}

