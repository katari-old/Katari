package com.globant.katari.report.domain;

import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.lang.Validate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.OrPredicate;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleSecurityUtils;

/** Security utilities used by the report commands to check access permission.
 *
 * @author gerardo.bercovich
 */
public final class ReportSecurityUtils {

  /** Private constructor, for an utility class. */
  private ReportSecurityUtils() {
  }

  /** Verify if the given report definition can be viewed by the current user.
   * @param definition the report definition to check access to. It cannot be
   * null.
   * @return true if the current user can view the given report definition.
   */
  public static boolean isAccesible(final ReportDefinition definition) {
    Validate.notNull(definition, "The report definition cannot be null.");
    boolean hasPermisson = canAccessAllReports();
    if (!hasPermisson) {
      hasPermisson = CollectionUtils.containsAny(definition.getRoles(),
          getCurrentUserRoles());
    }
    return hasPermisson;
  }
  /** Verify if the current user can create new reports.
   * @return true if the current user has permission to create reports.
   */
  public static boolean canCreateReports() {
    return canAccessAllReports();
  }

  /** Returns all the reports accessible by the current user.
   * @param repository needs the report repository. It cannot be null.
   * @return the list of accessible reports, it never returns null.
   */
  public static List<ReportDefinition> getAccesibleReports(
      final JasperReportRepository repository) {
    Validate.notNull(repository, "The report repository cannot be null.");
    final List<ReportDefinition> definitions;

    if (canAccessAllReports()) {
      definitions = repository.getReportList();
    } else {
      definitions = repository.findReportsByRole(getCurrentUserRoles());
    }
    return definitions;
  }

  /** Verify if the current user can access all reports.
   * @return true if the current user can access to all reports
   */
  private static boolean canAccessAllReports() {
    Set<Role> roles = getCurrentUserRoles();
    Predicate isReportAdmin = roleNamePredicate("REPORT_ADMIN");
    Predicate isAdmin = roleNamePredicate("ADMINISTRATOR");
    Predicate canAccess = OrPredicate.getInstance(isReportAdmin, isAdmin);
    return CollectionUtils.exists(roles, canAccess);
  }

  /**
   * Creates a predicate that checks if the name property of an object has the
   * specified value.
   *
   * This predicate is used to filter roles by name.
   *
   * @param roleName name of the role. It cannot be null.
   *
   * @return the role name predicate. Never returns null.
   */
  private static BeanPropertyValueEqualsPredicate roleNamePredicate(
      final String roleName) {
    Validate.notNull(roleName, "The role name cannot be null.");
    return new BeanPropertyValueEqualsPredicate("name", roleName);
  }

  /** Returns the roles of the currently logged on user.
   *
   * @return a set the currently logged on user. It returns null only if the
   * user has not logged in yet.
   */
  private static Set<Role> getCurrentUserRoles() {
    return RoleSecurityUtils.getCurrentUserRoles();
  }
}

