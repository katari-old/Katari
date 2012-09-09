/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.ehcache;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;

/** Factory utility for the EhCache Cache Manager.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public final class CacheManagerFactory {

  /** The cache manager name.*/
  private static final String CACHE_MANAGER_NAME = "_KATARI_CACHE_MANAGER_";

  /** Default private constructor, for this utility class.*/
  private CacheManagerFactory() {
  }

  /** The class logger. */
  private static Logger log = LoggerFactory.getLogger(
      CacheManagerFactory.class);

  /** Creates a new instance of the Cache Manager.
   *
   * @param defaultConfiguration the default configuration for caches created
   * on-demand. It cannot be null.
   *
   * @param cacheConfigurations the configurations for pre-configured caches.
   * It cannot be null.
   *
   * @param diskStorePath the file system path to store objects that overflow
   * the memory cache. It cannot be null.
   *
   * @param recordStatistics true if the cache will record hit, miss and evict
   * statistics.
   *
   * @return the cache manager instance, never returns null.
   */
  public static CacheManager create(
      final CacheConfiguration defaultConfiguration,
      final List<CacheConfiguration> cacheConfigurations,
      final String diskStorePath,
      final boolean recordStatistics) {

    log.trace("Entering create");

    Configuration configuration = new Configuration();
    configuration.setName(CACHE_MANAGER_NAME);
    defaultConfiguration.setStatistics(recordStatistics);
    configuration.setDefaultCacheConfiguration(defaultConfiguration);

    for (CacheConfiguration cacheConfig : cacheConfigurations) {
      log.debug("adding the cache: " + cacheConfig.getName());
      cacheConfig.setStatistics(recordStatistics);
      configuration.addCache(cacheConfig);
    }

    DiskStoreConfiguration diskStoreConfiguration;
    diskStoreConfiguration = new DiskStoreConfiguration();
    diskStoreConfiguration.setPath(diskStorePath);

    configuration.addDiskStore(diskStoreConfiguration);

    CacheManager manager = CacheManager.create(configuration);

    log.trace("Leaving create");

    return manager;
  }
}

