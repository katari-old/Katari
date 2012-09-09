/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.ehcache.hibernate;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;
import java.util.LinkedList;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.ehcache.SpringTestUtils;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class EhCacheRegionFactoryTest {

  private static final String QUERY_CACHE_NAME =
      "org.hibernate.cache.StandardQueryCache";

  private static final String OBJECT_CACHE_NAME =
      "com.globant.katari.ehcache.hibernate.OneHibernateEntity";

  private OneHibernateEntityRepository repository;

  @Before
  public void setUp() throws Exception {
    SpringTestUtils.destroy();
    repository = (OneHibernateEntityRepository)
        SpringTestUtils.getBean("oneHibernateEntityRepository");
  }

  @Test
  public void test() {
    SpringTestUtils.beginTransaction();
    for (int i=0; i<100; i++) {
      repository.save(new OneHibernateEntity("a_" + i));
    }
    SpringTestUtils.endTransaction();

    CacheManager manager = CacheManager.getInstance();
    Cache queryCache = manager.getCache(QUERY_CACHE_NAME);
    Cache objectCache = manager.getCache(OBJECT_CACHE_NAME);

    assertThat(queryCache.getSize(), is(0));

    repository.getAll();

    assertThat(queryCache.getSize(), is(1));

    repository.get(1);

    assertThat(objectCache.getSize(), is(100));
  }

  @Test
  public void testGetCache() {
    CacheManager manager = CacheManager.getInstance();
    EhCacheRegionFactory factory;
    factory = (EhCacheRegionFactory)
        SpringTestUtils.getBean("katari.regionFactory");
    int numberOfCaches = manager.getCacheNames().length;
    String newCacheName = "AA_" + System.currentTimeMillis();
    factory.getCache(newCacheName);
    assertThat(manager.getCacheNames().length, is(numberOfCaches + 1));
  }

  @Test
  public void testStart() {
    CacheManager manager = new CacheManager();
    try {
      EhCacheRegionFactory factory;
      List<CacheConfiguration> cacheConfigurations;
      cacheConfigurations = new LinkedList<CacheConfiguration>();

      factory = new EhCacheRegionFactory(manager, cacheConfigurations, true);
      factory.start(null, null);

      String[] names = manager.getCacheNames();
      for (String name : names) {
        boolean stats;
        stats = manager.getCache(name).getCacheConfiguration().getStatistics();
        assertThat(stats, is(true));
      }
    } finally {
      manager.shutdown();
    }
  }

  @Test
  public void testStop() {
    CacheManager manager = new CacheManager();
    try {
      EhCacheRegionFactory factory;
      List<CacheConfiguration> cacheConfigurations;
      cacheConfigurations = new LinkedList<CacheConfiguration>();

      factory = new EhCacheRegionFactory(manager, cacheConfigurations, true);
      factory.start(null, null);

      factory.stop();
      assertThat(manager.getStatus(), is(Status.STATUS_SHUTDOWN));
    } finally {
      manager.shutdown();
    }
  }
}

