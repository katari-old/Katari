/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.springframework.beans.factory.FactoryBean;

public class NullFactoryBeanTest {

  @Test public void getObject() {
    NullFactoryBean factory = new NullFactoryBean();
    assertThat(factory.getObject(), is(nullValue()));
  }

  @Test public void getObjectType() {
    NullFactoryBean factory = new NullFactoryBean();
    assertThat(factory.getObjectType(), is(nullValue()));
  }

  @Test public void isSingleton() {
    NullFactoryBean factory = new NullFactoryBean();
    assertThat(factory.isSingleton(), is(true));
  }
}

