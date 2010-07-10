/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/** Implements the katari:properties spring extension.
 *
 * This class parses the properties element from the katari namespace. The
 * properties file to be loaded is specified in the resource attribute in the
 * same way as for any other Spring resource (e.g.:
 * classpath:/com/katari/ui/ui.properties).
 *
 * These properties are used by the import element, also defined in the katari
 * namespace, for condition evaluation. Also, if the name attribute is
 * specified, the resulting properties object (instance of
 * java.util.Properties) is stored as a bean in the Spring application context
 * and available for later use.
 *
 * The bean's name is defined in the name attribute of the properties element.
 *
 * An import element from the katari namespace can refer to one of this
 * properties instance by setting the bean name in the properties-ref attribute.
 */
public class PropertiesParser implements BeanDefinitionParser {

  /** Constant for the properties element. */
  private static final String PROPERTIES = "properties";

  /** Constant for the name attribute. */
  private static final String NAME = "name";

  /** Constant for the location attribute. */
  private static final String LOCATION = "location";

  /** The class logger. */
  private static Logger log = LoggerFactory.getLogger(PropertiesParser.class);

  /** The map holding all the named katari:properties.
   *
   * It is never null.
   */
  private Map<String, Properties> propertiesMap = new LinkedHashMap<String,
    Properties>();

  /** The list of all the katari:properties, in the reverse order found.
   *
   * It is never null.
   */
  private List<Properties> propertiesList = new LinkedList<Properties>();

  /** We should fail if an import is found after a katari:import definition.
   *
   * This attribute is set to true by the import element parsing (see
   * katariImportStarted.
   */
  public boolean katariImportStarted = false;

  /** {@inheritDoc}
   *
   * This should never appear after a katari:import element.
   *
   * @param element the element. It must correspond to 'import'.
   *
   * @return this implementation always returns null.
   */
  public BeanDefinition parse(final Element element, final ParserContext
      parserContext) {

    Validate.isTrue(DomUtils.nodeNameEquals(element, PROPERTIES));
    log.trace("Entering parse");

    String name = element.getAttribute(NAME);
    if (katariImportStarted) {
      throw new RuntimeException("katari:properties [" + name
          + "] found after a katari:import");
    }

    String location = element.getAttribute(LOCATION);
    Resource resource = parserContext.getReaderContext().getResourceLoader()
        .getResource(location);
    // create the properties object
    PropertiesFactoryBean propertiesFactory = new PropertiesFactoryBean();
    propertiesFactory.setLocation(resource);
    try {
      propertiesFactory.afterPropertiesSet();
      log.trace("Katari properties loaded: bean name={} values [{}]", name,
          propertiesFactory.getObject());
      addProperties(name, (Properties) propertiesFactory.getObject());

      if (name != null) {
        // create a bean definition to get these properties available in the
        // application context
        AbstractBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(PropertiesFactoryBean.class);
        MutablePropertyValues values = new MutablePropertyValues();
        values.addPropertyValue("location", resource);
        def.setPropertyValues(values);
        parserContext.getRegistry().registerBeanDefinition(name, def);
      }
    } catch (IOException e) {
      throw new RuntimeException("Error getting katari properties", e);
    }
    log.trace("Leaving parse");
    return null;
  }

  /** Records the properties found.
   *
   * If the name is not null, it adds the properties instance to the map of all
   * the properties instances.
   *
   * @param name the properties name. If null, this properties cannot be search
   * by name.
   *
   * @param properties the properties. It cannot be null.
   */
  private void addProperties(final String name, final Properties properties) {
    if (name != null) {
      propertiesMap.put(name, properties);
    }
    propertiesList.add(0, properties);
  }

  /** Gets the property value from the katari:properties element.
   *
   * @param refProperties the properties bean to be used, if null, all
   * available properties are used
   *
   * @param propertyName the propertyName value. It cannot be null
   *
   * @return the property value, never null.
   *
   * @TODO analyze using default values for missing properties
   */
  public String getProperty(final String refProperties,
      final String propertyName) {
    Validate.notNull(propertyName, "The property name cannot be null.");
    // if a properties bean has been referenced, just look for the property in
    // it
    if (!StringUtils.isEmpty(refProperties)) {
      Properties properties = propertiesMap.get(refProperties);
      if (properties == null) {
        throw new RuntimeException("Properties bean [" + refProperties
            + "] couldn't be found");
      }
      String propertyValue = properties.getProperty(propertyName);
      if (StringUtils.isEmpty(propertyValue)) {
        throw new RuntimeException("Couldn't get value for [" + propertyName
            + "] in properties bean [" + refProperties + "]");
      }
      return propertyValue;
    }

    // no reference to a specific properties bean, use all available
    for (Properties properties : propertiesList) {
      if (properties.containsKey(propertyName)) {
        return properties.getProperty(propertyName);
      }
    }
    throw new RuntimeException("Couldn't get value for [" + propertyName
        + "] in any of the properties beans");
  }

  /** Notifies this object that the parser found an import element in the
   * application context.
   */
  public void katariImportStarted() {
    this.katariImportStarted = true;
  }
}

