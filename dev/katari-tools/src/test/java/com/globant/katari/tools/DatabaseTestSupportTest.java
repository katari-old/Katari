/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.sql.Connection;
import java.util.Properties;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.hibernate.cfg.Configuration;

import static org.easymock.EasyMock.*;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.globant.katari.tools.database.MySqlDropAllObjects;

public class DatabaseTestSupportTest {

  @Test
  public void testCreate() {

    Properties properties = new Properties();
    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

    Configuration hibernateConfig = createMock(Configuration.class);
    expect(hibernateConfig.getProperties()).andReturn(properties);
    replay(hibernateConfig);

    /* EasyMock did not work here because getConfiguration is final. */
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    new DirectFieldAccessor(sessionFactory).setPropertyValue("configuration",
        hibernateConfig);

    /*
    DatabaseTestSupport dbSupport = new DatabaseTestSupport() {
      protected void doDropAll(final Connection connection, final String
          markerTable) throws Exception {
      }
    };
    */
    assertThat(DatabaseTestSupport.create(sessionFactory),
        instanceOf(MySqlDropAllObjects.class));
  }

  @Test
  public void testCreate_customDialect() {

    Properties properties = new Properties();
    properties.put("hibernate.dialect", "test");

    Configuration hibernateConfig = createMock(Configuration.class);
    expect(hibernateConfig.getProperties()).andReturn(properties);
    replay(hibernateConfig);

    /* EasyMock did not work here because getConfiguration is final. */
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    new DirectFieldAccessor(sessionFactory).setPropertyValue("configuration",
        hibernateConfig);

    DatabaseTestSupport dbSupport = new DatabaseTestSupport() {
      protected void doDropAll(final Connection connection, final String
          markerTable) throws Exception {
      }
      protected void doDeleteAll(final Connection connection, final String
          markerTable) throws Exception {
      }
      protected void doInitializeAutoincrement(final Connection connection,
          final int initialValue) throws Exception {
        throw new UnsupportedOperationException();
      }
    };
    DatabaseTestSupport.registerDialect("test", dbSupport);
    assertThat(DatabaseTestSupport.create(sessionFactory),
        instanceOf(DatabaseTestSupport.class));
  }
}

