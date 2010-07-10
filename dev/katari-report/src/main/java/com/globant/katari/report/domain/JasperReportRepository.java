package com.globant.katari.report.domain;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.globant.katari.hibernate.coreuser.domain.Role;

/**
 * The Client repository. It provides access to the report definitions.
 *
 * @author sergio.sobek
 */
public class JasperReportRepository extends HibernateDaoSupport {

  /** The class logger. */
  private static Log log = LogFactory.getLog(JasperReportRepository.class);

  /** The entity class name. */
  private static final String CLASS_NAME = ReportDefinition.class.getName();

  /**
   * Gets a list with all the report definitions.
   *
   * @return a list with all the report definitions
   */
  @SuppressWarnings("unchecked")
  public List<ReportDefinition> getReportList() {
    log.trace("getReportTemplateList");
    return getHibernateTemplate().find("from ReportDefinition");
  }

  /**
   * Finds a ReportDefinition given its id.
   *
   * @param anId the id of the ReportDefinition to be found.
   * @return the ReportDefinition that has the specified id or null if no such
   * ReportDefinition exists. It must be greater than zero.
   */
  public ReportDefinition findReportDefinitionById(final long anId) {
    log.trace("findReportTemplateById");
    Validate.isTrue(anId > 0, "The id must be greater than zero.");
    return (ReportDefinition) getHibernateTemplate().get(CLASS_NAME, anId);
  }

  /**
   * Given a name, it finds a Report Definition.
   *
   * @param aName the name of the ReportDefinition to be found.
   * @return the ReportDefinition that has the specified name or null if no such
   * ReportDefinition exists. It cannot be null.
   */
  @SuppressWarnings("unchecked")
  public ReportDefinition findReportDefinition(final String aName) {
    log.trace("findReportTemplateByName");
    Validate.notNull(aName, "The name cannot be null.");
    ReportDefinition result = null;

    String query = "from " + CLASS_NAME + " where name = ?";
    List<ReportDefinition> list = getHibernateTemplate().find(query, aName);

    if (!list.isEmpty()) {
      result = list.get(0);
    }

    return result;
  }

  /**
   * Finds a report with the given name and return its id.
   *
   * @param name the name of the ReportDefinition to be found. It cannot null.
   *
   * @return the long id of the report, 0 if no report found.
   */
  public long findIdForName(final String name) {
    Validate.notNull(name, "The name cannot be null.");
    String hqlQuery = "select definition.id from ReportDefinition definition"
        + " where name = ?";
    Query query = getSession().createQuery(hqlQuery);
    long retunId = 0;
    Long foundId = (Long) query.setParameter(0, name).uniqueResult();
    if (foundId != null) {
      retunId = foundId;
    }
    return retunId;
  }

  /**
   * Given a list of roles, it finds Report Definitions.
   *
   * @param roles list of roles. Cannot be null and cannot contain null
   * elements.
   *
   * @return the ReportDefinition that has one of the specified role or empty if
   * no such ReportDefinition exists. It never returns null.
   */
  @SuppressWarnings("unchecked")
  public List<ReportDefinition> findReportsByRole(
      final Collection<Role> roles) {
    Validate.notNull(roles, "The roles cannot be null.");
    Validate.noNullElements(roles, "The roles cannot has null element.");

    String hqlQuery;
    Query query;
    // if no role given return only reports without roles, a new query if used
    // because empty in() clause in sql is not allowed, another option is set a
    // negative default id, but i dont like it
    if (roles.isEmpty()) {
      hqlQuery = "select definition from"
          + " ReportDefinition definition left join definition.roles as role"
          + " where role is null";
      query = getSession().createQuery(hqlQuery);
    } else {
      hqlQuery = "select definition from"
          + " ReportDefinition definition left join definition.roles as role"
          + " where role in(:roles) or role is null";
      query = getSession().createQuery(hqlQuery);
      query.setParameterList("roles", roles);
    }
    return query.list();
  }

  /**
   * Saves a new Report Definition or updates an existing Report Definition to
   * the database.
   * @param aReportDefinition the ReportDefinition to be saved. It cannot be
   * null.
   */
  public void save(final ReportDefinition aReportDefinition) {
    log.trace("save");
    Validate.notNull(aReportDefinition, "the report definition cannot be null");
    getHibernateTemplate().saveOrUpdate(aReportDefinition);
  }

  /**
   * Deletes a ReportDefinition from the database.
   *
   * @param aReportDefinition the ReportDefinition to be deleted. It cannot be
   * null.
   */
  public void remove(final ReportDefinition aReportDefinition) {
    log.trace("remove");
    Validate.notNull(aReportDefinition, "the reportTemplate cannot be null");
    getHibernateTemplate().delete(aReportDefinition);
  }

  /** Builds a list of options used in a drop down tag from a sql query.
   *
   * The query must return two columns, aliased to value and label. The
   * label is shown to the user and the value gets post when the user selects
   * the corresponding option.
   *
   * @param parameter the parameter definition, it cannot be null.
   * @param parameterValues the parameter values, it cannot be null.
   *
   * @return a list of options.
   */
  @SuppressWarnings({ "unchecked", "deprecation" })
  public List<DropdownOptions> getDropdownOptions(final ParameterDefinition
      parameter, final Map<String, String> parameterValues) {
    Validate.notNull(parameter, "the parameter cannnot be null.");
    Validate.notNull(parameterValues, "the parameterValues cannnot be null.");
    Constructor constructor;
    try {
      constructor = DropdownOptions.class.getConstructor(
          new Class[] {String.class, String.class});
    } catch (NoSuchMethodException e) {
      log.error("Error obtaining constructor DropdownOptions(String, String)",
          e);
      throw new RuntimeException("Error obtaining constructor", e);
    }
    Query query = getSession().createSQLQuery(parameter.getDropdownQuery())
      .addScalar("value", Hibernate.STRING)
      .addScalar("label", Hibernate.STRING)
      .setResultTransformer(new AliasToBeanConstructorResultTransformer(
          constructor));

    for (String name : query.getNamedParameters()) {
      String value = parameterValues.get(name);
      if (value == null) {
        return ListUtils.EMPTY_LIST;
      }
      query.setParameter(name, value);
    }

    List<DropdownOptions> list = query.list();
    return list;
  }
}
