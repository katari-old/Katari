/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.ehcache;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

/** Tests for the cache manager factory.
 * This test double checks that the disk store path its used by the cache.
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class CacheManagerFactoryTest {

  private String cacheName = "theCacheName";
  private String cacheEntryKey = "[hello :-)]";

  private File cacheDirectory;

  @Before
  public void setUp() throws Exception {
    cacheDirectory = File.createTempFile("folder"
		    + System.currentTimeMillis(), "k");

    if(!cacheDirectory.delete()) {
      throw new RuntimeException("Could not delete temp file: "
          + cacheDirectory.getAbsolutePath());
    }

    if(!cacheDirectory.mkdir()) {
      throw new RuntimeException("Could not create temp directory: "
          + cacheDirectory.getAbsolutePath());
    }
  }

  @Test
  public void test() throws Exception {

    CacheManager.getInstance().shutdown();

    String diskStorePath = cacheDirectory.getAbsolutePath();

    List<CacheConfiguration> configurations;
    configurations = new ArrayList<CacheConfiguration>();

    CacheConfiguration oneCache = new CacheConfiguration();
    oneCache.setName(cacheName);
    oneCache.setMaxEntriesLocalHeap(1);
    oneCache.setTimeToLiveSeconds(100000L);
    oneCache.setOverflowToDisk(true);
    oneCache.setEternal(true);
    configurations.add(oneCache);

    CacheConfiguration defaultCacheConfiguration;
    defaultCacheConfiguration = new CacheConfiguration();
    defaultCacheConfiguration.setEternal(true);
    defaultCacheConfiguration.setMaxEntriesLocalHeap(1);
    defaultCacheConfiguration.setOverflowToDisk(true);
    defaultCacheConfiguration.setTimeToLiveSeconds(100000L);

    CacheManager manager = CacheManagerFactory.create(
        defaultCacheConfiguration, configurations, diskStorePath, true);

    assertThat(manager.getName(), is("_KATARI_CACHE_MANAGER_"));
    assertThat(manager.getDiskStorePath(), is(diskStorePath));

    assertThat(cacheDirectory.listFiles()[0].getName(),
        is(cacheName + ".data"));
    assertThat(cacheDirectory.listFiles()[0].length(), is(0L));

    String cachedElement = "one entry";

    Element element = new Element(cacheEntryKey, cachedElement);

    Cache cache = manager.getCache(cacheName);

    cache.put(element);

    Element fromCacheElement = cache.get(cacheEntryKey);
    String fromCacheObject = (String) fromCacheElement.getValue();

    assertThat(fromCacheObject, is(cachedElement));

    assertThat(manager.getDiskStorePath(), is(diskStorePath));

    addRandomElements(cache);

    Thread.sleep(TimeUnit.SECONDS.toMillis(2));

    assertTrue(cacheDirectory.listFiles()[0].length() > 1);
  }

  @After
  public void tearDown() {
    if (cacheDirectory != null) {
      cacheDirectory.delete();
    }
  }

  /** Adds random elements to the cache.
   * @param cache the cache.
   */
  private void addRandomElements(final Cache cache) {
    for (int i=0; i<=2000; i++) {
      Element element = new Element("a_" + i, "ab_" + i);
      cache.put(element);
    }
  }
}

