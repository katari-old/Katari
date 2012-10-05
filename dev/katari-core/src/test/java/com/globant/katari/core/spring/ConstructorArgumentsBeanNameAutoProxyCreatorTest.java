/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class ConstructorArgumentsBeanNameAutoProxyCreatorTest {

  private XmlBeanFactory beanFactory;
  private ConstructorArgumentsBeanNameAutoProxyCreator creator;

  public static class BeanSample {
    public BeanSample() {
    }
  }

  public static class BeanSampleWithBean {
    public BeanSampleWithBean(final int i, final BeanSample theValue) {
    }
    public BeanSampleWithBean(final BeanSample theValue) {
    }
    public BeanSampleWithBean(final String theValue) {
    }
  }

  public static class BeanSampleWithString {
    private String value;
    public BeanSampleWithString(final String theValue) {
      value = theValue;
    }
    public String getValue() {
      return value;
    }
  }

  public static class BeanSampleWithInt {
    private int value;
    public BeanSampleWithInt(final int theValue) {
      value = theValue;
    }
    public int getValue() {
      return value;
    }
  }

  public static class BeanSampleWithBoolean {
    private boolean value;
    public BeanSampleWithBoolean(final boolean theValue) {
      value = theValue;
    }
    public boolean getValue() {
      return value;
    }
  }

  @Before public void setUp() {
    final String beans =
      "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<!DOCTYPE beans PUBLIC '-//SPRING//DTD BEAN//EN'"
      + " 'http://www.springframework.org/dtd/spring-beans.dtd'>\n"
      + "<beans>\n"
      + " <bean class='com.globant.katari.core.spring"
      +      ".ConstructorArgumentsBeanNameAutoProxyCreatorTest"
      +      ".BeanSample'"
      + "   name='beanSample'/>\n"
      + "\n"
      + " <bean class='com.globant.katari.core.spring"
      +      ".ConstructorArgumentsBeanNameAutoProxyCreatorTest"
      +      ".BeanSampleWithBean'"
      + "   name='beanSampleWithBean'>\n"
      + "  <constructor-arg index='0' ref='beanSample'/>\n"
      + " </bean>\n"
      + "\n"
      + " <bean class='com.globant.katari.core.spring"
      +      ".ConstructorArgumentsBeanNameAutoProxyCreatorTest"
      +      ".BeanSampleWithString'"
      + "   name='beanSampleWithString'>\n"
      + "  <constructor-arg index='0' value='proxy'/>\n"
      + " </bean>\n"
      + "\n"
      + " <bean class='com.globant.katari.core.spring"
      +      ".ConstructorArgumentsBeanNameAutoProxyCreatorTest"
      +      ".BeanSampleWithInt'"
      + "   name='beanSampleWithInt'>\n"
      + "  <constructor-arg index='0' value='-1'/>\n"
      + " </bean>\n"
      + "\n"
      + " <bean class='com.globant.katari.core.spring"
      +      ".ConstructorArgumentsBeanNameAutoProxyCreatorTest"
      +      ".BeanSampleWithBoolean'"
      + "   name='beanSampleWithBoolean'>\n"
      + "  <constructor-arg index='0' value='false'/>\n"
      + " </bean>\n"
      + "</beans>\n";

    beanFactory = new XmlBeanFactory(new ByteArrayResource(beans.getBytes()));

    creator = new ConstructorArgumentsBeanNameAutoProxyCreator();
    creator.setBeanFactory(beanFactory);
    creator.setProxyTargetClass(true);
  }

  @Test public void createProxy_emptyConstructor() {
    creator.setBeanNames(new String[] {"beanSample"});

    BeanSample bean = new BeanSample();
    BeanSample beanProxy = (BeanSample) creator
      .postProcessAfterInitialization(bean, "beanSample");
    assertThat(beanProxy, is(notNullValue()));
    assertThat(beanProxy, is(not(bean)));
    assertThat(beanProxy, instanceOf(BeanSample.class));
    assertThat(beanProxy.getClass(), not(is(BeanSample.class)));
  }

  @Test public void createProxy_beanConstructor() {
    creator.setBeanNames(new String[] {"beanSampleWithBean"});

    BeanSampleWithBean bean = new BeanSampleWithBean(new BeanSample());
    BeanSampleWithBean beanProxy = (BeanSampleWithBean) creator
      .postProcessAfterInitialization(bean, "beanSampleWithBean");
    assertThat(beanProxy, is(notNullValue()));
    assertThat(beanProxy, is(not(bean)));
    assertThat(beanProxy, instanceOf(BeanSampleWithBean.class));
    assertThat(beanProxy.getClass(), not(is(BeanSampleWithBean.class)));
  }

  @Test public void createProxy_stringConstructor() {
    creator.setBeanNames(new String[] {"beanSampleWithString"});

    BeanSampleWithString bean = new BeanSampleWithString("bean");
    BeanSampleWithString beanProxy = (BeanSampleWithString) creator
      .postProcessAfterInitialization(bean, "beanSampleWithString");
    assertThat(beanProxy, is(notNullValue()));
    assertThat(beanProxy, is(not(bean)));
    assertThat(beanProxy, instanceOf(BeanSampleWithString.class));
    assertThat(beanProxy.getClass(), not(is(BeanSampleWithString.class)));
    assertThat(beanProxy.getValue(), is("bean"));
  }

  @Test public void createProxy_intConstructor() {
    creator.setBeanNames(new String[] {"beanSampleWithInt"});

    BeanSampleWithInt bean = new BeanSampleWithInt(10);
    BeanSampleWithInt beanProxy = (BeanSampleWithInt) creator
      .postProcessAfterInitialization(bean, "beanSampleWithInt");
    assertThat(beanProxy, is(notNullValue()));
    assertThat(beanProxy, is(not(bean)));
    assertThat(beanProxy, instanceOf(BeanSampleWithInt.class));
    assertThat(beanProxy.getClass(), not(is(BeanSampleWithInt.class)));
    assertThat(beanProxy.getValue(), is(10));
  }

  @Test public void createProxy_booleanConstructor() {
    creator.setBeanNames(new String[] {"beanSampleWithBoolean"});

    BeanSampleWithBoolean bean = new BeanSampleWithBoolean(true);
    Object beanProxy = creator.postProcessAfterInitialization(
        bean, "beanSampleWithBoolean");
    assertThat(beanProxy, is(notNullValue()));
    assertThat(beanProxy, instanceOf(BeanSampleWithBoolean.class));
    assertThat(beanProxy.getClass(), not(is(BeanSampleWithBoolean.class)));
  }
}

