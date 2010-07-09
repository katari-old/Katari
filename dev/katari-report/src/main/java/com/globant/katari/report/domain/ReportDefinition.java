/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.domain;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.base.JRBaseParameter;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.Validate;

import com.globant.katari.hibernate.coreuser.domain.Role;

/**
 * The <code>ReportDefinition</code> class represents a Report XML Template.
 *
 * @author sergio.sobek
 */
@Entity
@Table(name = "report_definitions")
public class ReportDefinition {

  /**
   * Maximum file size.
   */
  private static final int MAX_TEMPLATE_SIZE = 1024000;

  /**
   * The report definition id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private long id;

  /**
   * The report definition name.
   *
   * It is never null.
   */
  @Column(name = "name", unique = true, nullable = false)
  private String name;

  /**
   * The report definition description.
   * It is never null.
   */
  @Column(name = "description", nullable = false)
  private String description;

  /** The report XML template byte array.
   *
   * TODO Rename this to something more reasonable, like 'report template'.
   */
  @Lob
  // lazy by definition.
  @Column(length = MAX_TEMPLATE_SIZE, name = "report_content")
  private byte[] reportContent;

  /** The projects associated with this client. */
  @Transient
  private List<ParameterDefinition> parameterDefinitions = null;

  /** The roles that users must have to view the report.
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "report_required_roles")
  private Set<Role> roles = new HashSet<Role>();

  /**
   * Default Constructor.
   */
  protected ReportDefinition() {
  }

  /**
   * Constructor.
   *
   * This operation does not validate that the report template is a jrxml valid
   * file. We leave this responsibility to the caller.
   *
   * @param aName the name of the report. It cannot be null.
   * @param aDescription the description of the report. It cannot be null.
   * @param aReportTemplate the report template as a byte array. It cannot be
   * null.
   */
  public ReportDefinition(final String aName, final String aDescription,
      final byte[] aReportTemplate) {
    Validate.notNull(aName, "The name cannot be null.");
    Validate.notNull(aDescription, "The description cannot be null.");
    Validate.notNull(aReportTemplate,
        "The report template content cannot be null.");
    name = aName;
    description = aDescription;
    reportContent = aReportTemplate.clone();
  }

  /**
   * Modifies the report definition name.
   *
   * @param aName The name for the report definition. It cannot be null.
   * @param aDescription The description for the report definition. It cannot be
   * null.
   */
  public void modify(final String aName, final String aDescription) {
    Validate.notNull(aName,
        "the new name of the report definition cannot be null.");
    Validate.notNull(aDescription,
        "the new description of the report definition cannot be null.");
    name = aName;
    description = aDescription;
  }

  /**
   * Gets the id of the report definition.
   *
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * Gets the parameters.
   * @return the parameters. It never returns null.
   */
  public List<ParameterDefinition> getParameterDefinitions() {
    if (parameterDefinitions == null) {
      parameterDefinitions = buildParameters();
    }
    return parameterDefinitions;
  }

  /**
   * Gets for the report definition name.
   *
   * @return the report definition name.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets for the report definition description.
   *
   * @return the report definition description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets of the report template XML byte array.
   *
   * @return the report template as a byte array. Never returns null.
   */
  public byte[] getReportContent() {
    return reportContent;
  }

  /**
   * Modifies the report definition.
   *
   * This operation does not validate that the content is a jrxml valid file.
   * We leave this responsibility to the caller.
   *
   * @param aContent The content to be associated with the report definition.
   * It cannot be null.
   */
  public void setReportContent(final byte[] aContent) {
    Validate.notNull(aContent,
      "the new conntent of the report definition cannot be null.");
    reportContent = aContent.clone();
    parameterDefinitions = null;
  }

  /**
   * Parses the Jasper XML Template file retrieving a list of defined
   * parameters.
   *
   * @return a list of parameters descriptors. If the report does not define
   * any parameter, it returns an empty list.
   *
   * TODO Get rid of the sax parser and use JasperDesign instead.
   */
  @SuppressWarnings("unchecked")
  private List<ParameterDefinition> buildParameters() {
    List<ParameterDefinition> result = new ArrayList<ParameterDefinition>();
    String dropdownQuery;
    boolean optional = false;
    try {
      JasperDesign design;
      design = JRXmlLoader.load(new ByteArrayInputStream(reportContent));
      List<JRBaseParameter> parameters = design.getParametersList();

      // parses all the parameters
      for (JRBaseParameter parameter : parameters) {
        // the parameter must not be system defined and must be for prompting
        if (!parameter.isSystemDefined() && parameter.isForPrompting()) {
          // get the property dropdown: if it doesn't exists it is null
          dropdownQuery = parameter.getPropertiesMap().getProperty("dropdown");
          String optionalValue = parameter.getPropertiesMap().getProperty(
              "optional");
          // 'true','yes' or 'on' values makes the parameter optional. Every
          // other thing, including null, is false.
          optional = BooleanUtils.toBoolean(optionalValue);
          result.add(new ParameterDefinition(parameter.getName(),
              parameter.getValueClassName(), optional, dropdownQuery));
        }
      }
    } catch (JRException ex) {
      throw new RuntimeException("Error loading report definition", ex);
    }

    return result;
  }

  /**
   * Adds the specified role.
   *
   * Ignores duplicates.
   *
   * @param theRole The role to add. It cannot be null.
   *
   * @return true if the role was added
   */
  public boolean addRole(final Role theRole) {
    Validate.notNull(theRole, "The role cannot be null");
    return roles.add(theRole);
  }

  /**
   * Removes the specified role.
   *
   * @param theRole The role to be deleted, it cannot be null.
   *
   * @return true if the list contained the specified role
   */
  public boolean removeRole(final Role theRole) {
    Validate.notNull(theRole, "The role cannot be null");
    return roles.remove(theRole);
  }

  /**
   * Returns the roles of the report.
   *
   * @return the role set, it is never null.
   */
  public Set<Role> getRoles() {
    return roles;
  }
}
