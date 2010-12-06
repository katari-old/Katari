package com.globant.katari.core.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/** Parses a katari:null element, an implementation for a null bean.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class NullPropertyParser implements BeanDefinitionParser, FactoryBean {

  /** The name of the katari null element.*/
  private static final String NULL = "null";

  /** {@inheritDoc}
   * @see org.springframework.beans.factory.xml.BeanDefinitionParser#parse(
   *  org.w3c.dom.Element,
   *  org.springframework.beans.factory.xml.ParserContext)
   */
  public BeanDefinition parse(final Element element,
      final ParserContext parserContext) {
    if(DomUtils.nodeNameEquals(element, NULL)) {
      String id = element.getAttribute("id");
      RootBeanDefinition beanDef = new RootBeanDefinition();
      beanDef.setBeanClass(NullPropertyParser.class);
      BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDef, id);
      BeanDefinitionReaderUtils.registerBeanDefinition(holder,
          parserContext.getRegistry());
      return beanDef;
    }
    return null;
  }

  /** {@inheritDoc}
   * @see org.springframework.beans.factory.FactoryBean#getObject()
   */
  public Object getObject() throws Exception {
    return null;
  }

  /** {@inheritDoc}
   * @see org.springframework.beans.factory.FactoryBean#getObjectType()
   */
  @SuppressWarnings("unchecked")
  public Class getObjectType() {
    return java.lang.Void.class;
  }

  /** {@inheritDoc}
   * @see org.springframework.beans.factory.FactoryBean#isSingleton()
   */
  public boolean isSingleton() {
    return true;
  }

}
