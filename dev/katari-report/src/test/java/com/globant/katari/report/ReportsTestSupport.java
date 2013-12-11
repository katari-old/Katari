/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.easymock.EasyMock;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleDetails;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.katari.report.domain.JasperReportGenerator;
import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ReportDefinition;
import com.globant.katari.tools.SpringTestUtilsBase;

/**
 * Report Test utility.
 * @author gerardo.bercovich
 */
public class ReportsTestSupport extends SpringTestUtilsBase {

  /** The static instance for the singleton.*/
  private static ReportsTestSupport instance;

  /** The name of the report used for testing. */
  public static final String REPORT_NAME = "Test Project Report";

  /** The name of the report used for testing. */
  public static final String REPORT_DESCRIPTION = "The report description";

  /** The report content source file. */
  private static final String REPORT_XML_SAMPLE_PATH
    = "src/test/resources/report-sample.jrxml";

  /**
   * @param theGlobalConfigurationFiles
   * @param theServletConfigurationFiles
   */
  protected ReportsTestSupport(final String[] theGlobalConfigurationFiles,
      final String[] theServletConfigurationFiles) {
    super(
        new String[] {
            "classpath:/applicationContext.xml",
            "classpath:/com/globant/katari/report/module.xml"
        },
        new String[] {
        "classpath:/com/globant/katari/report/view/spring-servlet.xml"
        }
    );
  }

  /** Retrieves the intance.
   * @return the instance, never null.
   */
  public static synchronized ReportsTestSupport get() {
    if (instance == null) {
      instance = new ReportsTestSupport(null, null);
    }
    return instance;
  }


  /** initialize Acegi security context with mock user with the given roles.
   * @param roleNames for user mockup. it can be empty but cannot be null.
   */
  public static void initTestReportSecurityContext(
      final String... roleNames) {

    ReportsTestSupport.get().beginTransaction();

    RoleRepository roleRepository = (RoleRepository)
        get().getBean("coreuser.roleRepository");

    Set<Role> roles = new HashSet<Role>();
    for (String roleName : roleNames) {
      roleRepository.save(new Role(roleName));
      roles.add(roleRepository.findRoleByName(roleName));
    }

    RoleDetails roleDetailsMock = EasyMock.createMock(RoleDetails.class);
    EasyMock.expect(roleDetailsMock.getUserRoles()).andReturn(roles).anyTimes();
    EasyMock.replay(roleDetailsMock);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(roleDetailsMock, "admin");
    SecurityContextHolder.getContext().setAuthentication(authentication);

    ReportsTestSupport.get().endTransaction();
  }

  /**
   * Method gets a file as a byte array.
   *
   * @param file the file, it cannot be null.
   * @return the byte array.
   */
  public static byte[] getBytes(final File file) {
    BufferedReader reader = null;
    StringBuilder sb = null;

    try {
      reader = new BufferedReader(new FileReader(file));

      sb = new StringBuilder();
      String str;

      while ((str = reader.readLine()) != null) {
        sb.append(str);
      }
    } catch(Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch(Exception e) {
        }
      }
    }
    return sb.toString().getBytes();
  }

  /**
   * Returns sample report byte array for testing. TODO see if the xml external
   * resource can be packaged with the module and NOT on global resources.
   * @return sample report byte array
   * @throws Exception
   */
  public static byte[] getSampleReportBytes() throws Exception{
    final File file = new File("src/test/resources/report-sample.jrxml");
    return ReportsTestSupport.getBytes(file);
  }

  /** Creates a sample report definition.
   *
   * It removes all report definitions before creating the new one.
   *
   * @param repository The report repository. It cannot be null.
   *
   * @return Returns the report definition of the new report.
   */
  public static ReportDefinition createSampleReport() {

    JasperReportRepository repository = getRepository();

    // Removes the unneeded reports.
    for (ReportDefinition report : repository.getReportList()) {
      repository.remove(report);
    }

    // add one report
    ReportDefinition testReport = new ReportDefinition(REPORT_NAME,
        REPORT_DESCRIPTION, getBytes(new File(REPORT_XML_SAMPLE_PATH)));
    repository.save(testReport);

    DataSource dataSource = (DataSource) get().getBean("dataSource");

    Connection connection = null;
    Statement stmt = null;
    try {
      try {
        connection = dataSource.getConnection();
        stmt = connection.createStatement();
      } catch(Exception e) {
        throw new RuntimeException(e);
      }

      try {
        stmt.execute("CREATE TABLE users (id INTEGER, name VARCHAR(50), "
            + "email VARCHAR(50), password VARCHAR(50));");
      } catch (SQLException ignored) {
        // Table might have already been created.
      }
    } finally {
      try {
        connection.close();
      } catch(Exception e) {
        throw new RuntimeException(e);
      }
    }

    return repository.findReportDefinition(REPORT_NAME);
  }

  /** Obtains the jasper repository.
   *
   * @return Returns the jasper repository, never null.
   */
  public static JasperReportRepository getRepository() {
    return (JasperReportRepository) get().getBean(
        "jasperReportRepository");
  }

  /** Obtains the jasper report generator.
   *
   * @return Returns the jasper report generator, never null.
   */
  public static JasperReportGenerator getGenerator() {
    return (JasperReportGenerator) get().getBean(
        "jasperReportGenerator");
  }

}

