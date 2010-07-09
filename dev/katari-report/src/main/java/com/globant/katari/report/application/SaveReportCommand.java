/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.application;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRValidationFault;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.globant.katari.core.application.ValidatableCommand;
import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;
import com.globant.katari.report.domain.JasperReportRepository;
import com.globant.katari.report.domain.ReportDefinition;

/**
 * Command for saving or updating a Report Definitions.
 */
public class SaveReportCommand implements ValidatableCommand<Void> {

  /**
   * The class logger.
   */
  private static Log log = LogFactory.getLog(SaveReportCommand.class);

  /** The Report Definition repository. It is never null. */
  private JasperReportRepository jasperReportRepository;

  /** The Role repository. It is never null. */
  private RoleRepository roleRepository;

  /**
   * The report definition id. The default value (zero) represents that the
   * Report Definition a new one.
   */
  private long reportId = 0;

  /**
   * The report definition name. It is never null.
   */
  private String name = "";

  /**
   * The report definition description. It is never null.
   */
  private String description = "";

  /**
   * The byte array with the report XML template file. It is null when the user
   * does not specify a file to upload.
   */
  private byte[] reportContent = null;

  /**
   * The roles ids.
   */
  private List<String> roleIds = new ArrayList<String>();

  /**
   * The list of all roles available in the system. It is null .
   */
  private List<Role> availableRoles = null;

  /**
   * Creates a new SaveClientCommand object with a clientRepository dependency.
   *
   * @param theReportDefinition the client repository. It cannot be null.
   *
   * @param theRoleRepository The role repository. It cannot be null.
   */
  public SaveReportCommand(final JasperReportRepository theReportDefinition,
      final RoleRepository theRoleRepository) {
    Validate.notNull(theReportDefinition,
        "The report repository cannot be null");
    Validate.notNull(theRoleRepository, "The role repository cannot be null");
    jasperReportRepository = theReportDefinition;
    roleRepository = theRoleRepository;
  }

  /**
   * Gets the report id.
   *
   * @return the reportId.
   */
  public long getReportId() {
    return reportId;
  }

  /**
   * Sets the report id.
   *
   * @param theReportId the reportId to set. 0 for a new report.
   */
  public void setReportId(final long theReportId) {
    reportId = theReportId;
  }

  /**
   * Gets the report Content.
   *
   * @return the report Content, null for a new report or when the user is not
   * modifying the content.
   */
  public byte[] getReportContent() {
    return reportContent;
  }

  /**
   * Sets the byte[] from the report template XML file.
   *
   * @param content the array of bytes containing the report template XML file.
   * It can be null, but see validate().
   */
  public void setReportContent(final byte[] content) {
    reportContent = content.clone();
  }

  /**
   * Gets the name of the report.
   *
   * @return the name, it never returns null.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets of the name of the report definition.
   *
   * @param theName the name to set. It cannot be null.
   */
  public void setName(final String theName) {
    Validate.notNull(theName, "The report name cannot be null.");
    name = theName;
  }

  /**
   * Gets the description of the report.
   *
   * @return the report description, it never returns null.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets of the description of the report definition.
   *
   * @param theDescription the description to set. It cannot be null.
   */
  public void setDescription(final String theDescription) {
    Validate.notNull(theDescription, "The description cannot be null.");
    description = theDescription;
  }

  /**
   * Gets the ids of the roles.
   *
   * @return Returns the ids of the user's roles. It cannot be null.
   */
  public List<String> getRoleIds() {
    return roleIds;
  }

  /**
   * Sets the ids of the roles.
   *
   * @param theRoleIds The roles id. If it is null, the report has no roles.
   */
  public void setRoleIds(final List<String> theRoleIds) {
    roleIds = theRoleIds;
    if (roleIds == null) {
      roleIds = new ArrayList<String>();
    }
  }

  /** Obtains the a map of ids to its corresponding role.
   *
   * @return the map of roles, never returns null.
   */
  public Map<String, String> getAvailableRoles() {
    // User Roles.
    Map<String, String> rolesMap = new LinkedHashMap<String, String>();
    for (Role role : availableRoles) {
      rolesMap.put(String.valueOf(role.getId()), role.getName());
    }
    return rolesMap;
  }

  /**
   * Initializes the command.
   *
   * During initialization, if a report id is specified, the corresponding
   * report definition will be retrieved from the repository. This report must
   * exist in the repository.
   */
  public void init() {
    availableRoles = roleRepository.getRoles();
    if (reportId != 0) {
      ReportDefinition report;
      report = jasperReportRepository.findReportDefinitionById(reportId);
      if (report == null) {
        throw new RuntimeException("The specified report id could not be"
            + " found");
      }
      name = report.getName();
      description = report.getDescription();
      List<String> rolesId = new ArrayList<String>();
      for (Role role : report.getRoles()) {
        rolesId.add(String.valueOf(role.getId()));
      }
      setRoleIds(rolesId);
    }
  }

  /**
   * Saves the domain <code>ReportDefinition</code>.
   *
   * @return The return value is meaningless.
   */
  public Void execute() {
    log.trace("entering execute");

    ReportDefinition aReportDefinition;
    List<Role> newRoles = roleRepository.getRoles(roleIds);

    if (reportId == 0) {
      aReportDefinition =
        new ReportDefinition(name, description, reportContent);
    } else {
      aReportDefinition = jasperReportRepository
          .findReportDefinitionById(reportId);
      aReportDefinition.modify(name, description);
      if (reportContent != null) {
        // The user uploaded a file.
        aReportDefinition.setReportContent(reportContent);
      }
      // Remove existing roles.
      aReportDefinition.getRoles().clear();
    }

    // Add the new roles.
    for (Role role : newRoles) {
      aReportDefinition.addRole(role);
    }

    jasperReportRepository.save(aReportDefinition);

    return null;
  }

  /**
   * Validates this command.
   *
   * The name must not be empty. For a new report, the report content is
   * required.
   *
   * @param errors Contextual state about the validation process. It cannot be
   * null.
   */
  @SuppressWarnings("unchecked")
  public void validate(final Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.required");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description",
        "error.required");

    long existentId = jasperReportRepository.findIdForName(name);
    boolean nameIsTaken = existentId != 0 && reportId != existentId;
    if (nameIsTaken) {
      // The name is duplicated.
      errors.rejectValue("name", "error.usedName", new String[]{name}, "");
    }

    if (reportContent != null && reportContent.length == 0) {
      // The user uploaded an empty file.
      errors.rejectValue("reportContent", "error.notempty");
    }

    if (reportContent == null && reportId == 0) {
      // The user did not upload a file for a new report.
      errors.rejectValue("reportContent", "error.required");
    }

    if (reportContent != null) {
      // Verifies if the report can be parsed by jasper.
      try {
        // try to convert the file uploaded by the user into a JasperDesign.
        // If it not valid it will throw an JRExeption.
        JasperDesign design;
        design = JRXmlLoader.load(new ByteArrayInputStream(reportContent));

        // At this point we are sure that the file uploaded by the user is a
        // valid XML but it still can contains some design problems. Therefore
        // it is further validated.
        List<JRValidationFault> faults;
        faults = (List<JRValidationFault>) JRVerifier.verifyDesign(design);

        // it adds an error for every validation fault found by the former
        // validator.
        for (JRValidationFault fault : faults) {
          errors.rejectValue("reportContent", "error.invalidReport",
              new String[] {fault.getMessage()}, "error.invalidReport");
        }

      } catch (JRException e) {
        errors.rejectValue("reportContent", "error.invalidXml");
      }
    }
  }
}

