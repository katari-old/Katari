/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.trails;

import org.apache.commons.lang.Validate;

import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.util.ClasspathResource;
import org.apache.tapestry.INamespace;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.TapestryUtils;
import org.apache.tapestry.resolver.ISpecificationResolverDelegate;
import org.apache.tapestry.spec.IComponentSpecification;

/** Delegate interface used when a page or component specification can not be
 * found by the normal means.
 *
 * This allows hooks to support specifications from
 * unusual locations, or generated on the fly.<br>
 * The components and page specifications will be found in this locations:
 * <ol>
 * <li>The WEB-INF directory.</li>
 * <li>Any of the locations defined in the property
 * org.apache.tapestry.page-class-packages</li>
 * <li>Resources in the support packages:
 * com.globant.katari.trails.pages and
 * com.globant.katari.trails.components.
 * </ol>
 * @author pruggia
 */
public class ModuleTemplateSourceDelegate implements
    ISpecificationResolverDelegate {

  /** The comma-separated list of classpath locations of tapestry pages.
   */
  private String customPagesLocation = null;

  /** The comma-separated list of classpath locations of tapestry components.
   */
  private String customComponentsLocation = null;

  /**
   * {@inheritDoc}
   */
  public IComponentSpecification findPageSpecification(final IRequestCycle
      cycle, final INamespace namespace, final String simplePageName) {
    Resource specResource = findSpecificationResource(cycle, namespace,
        simplePageName, ".page", customPagesLocation,
        "org.apache.tapestry.page-class-packages");
    if (specResource.getResourceURL() != null) {
      return cycle.getInfrastructure().getSpecificationSource()
          .getPageSpecification(specResource);
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
  public IComponentSpecification findComponentSpecification(
      final IRequestCycle cycle, final INamespace namespace,
      final String type) {
    Resource specResource = findSpecificationResource(cycle, namespace, type,
        ".jwc", customComponentsLocation,
        "org.apache.tapestry.component-class-packages");
    if (specResource.getResourceURL() != null) {
      return cycle.getInfrastructure().getSpecificationSource()
          .getComponentSpecification(specResource);
    } else {
      return null;
    }
  }

  /** Searches for resources that describes the component or page.
   *
   * @param cycle Controller object that manages a single request cycle. A
   * request cycle is one 'hit' on the web server.
   *
   * @param namespace Organizes different libraries of Tapestry pages,
   * components and services into "frameworks", used to disambiguate names.
   *
   * @param name Name of the component or page.
   *
   * @param specExtension .jwc for components, .page for pages.
   *
   * @param additionalLocation Additional classpath location to look for pages
   * and components. Ignored if null.
   *
   * @param packagePropertyValue Name of tapestry configuration of the packages
   * to look at to find component and page classes.
   *
   * @return Always a valid or invalid Resource. Check if that resource exist
   * by doing resource.getResourceURL() and comparing it to null.
   */
  protected Resource findSpecificationResource(final IRequestCycle cycle,
      final INamespace namespace, final String name,
      final String specExtension, final String additionalLocation,
      final String packagePropertyValue) {

    DefaultClassResolver classResolver = new DefaultClassResolver();
    String packages = namespace.getPropertyValue(packagePropertyValue);
    if (additionalLocation != null) {
      packages = additionalLocation + ", " + packages;
    }

    String className = name.replace('/', '.');

    String[] packagesArray = TapestryUtils.split(packages, ',');

    Resource specResource = null;
    for (int i = 0; i < packagesArray.length; i++) {
      String fullName = packagesArray[i].trim() + "." + className;
      String fullPath = fullName.replace('.', '/') + specExtension;
      specResource = new ClasspathResource(classResolver, fullPath);
      if (specResource.getResourceURL() != null) {
        break;
      }
    }
    return specResource;
  }

  /** Sets the classpath location of the aplication specific trails pages.
   *
   * @param location The classpath location. It cannot be null.
   */
  public void setCustomPagesLocation(final String location) {
    Validate.notNull(location, "The pages location cannot be null.");
    if (!location.equals("NONE")) {
      customPagesLocation = location;
    } else {
      customPagesLocation = null;
    }
  }

  /** Sets the classpath location of the aplication specific trails components.
   *
   * @param location The classpath location. It cannot be null.
   */
  public void setCustomComponentsLocation(final String location) {
    Validate.notNull(location, "The components location cannot be null.");
    if (!location.equals("NONE")) {
      customComponentsLocation = location;
    } else {
      customComponentsLocation = null;
    }
  }
}

