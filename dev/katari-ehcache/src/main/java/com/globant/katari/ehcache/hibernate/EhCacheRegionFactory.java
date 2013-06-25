/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.ehcache.hibernate;

import java.util.List;
import java.util.Properties;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.hibernate.management.impl
  .ProviderMBeanRegistrationHelper;
import net.sf.ehcache.util.Timestamper;

import org.apache.commons.lang.Validate;
import org.hibernate.cache.ehcache.internal.nonstop
  .NonstopAccessStrategyFactory;
import org.hibernate.cache.ehcache.internal.regions.EhcacheCollectionRegion;
import org.hibernate.cache.ehcache.internal.regions.EhcacheEntityRegion;
import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
import org.hibernate.cache.ehcache.internal.regions.EhcacheQueryResultsRegion;
import org.hibernate.cache.ehcache.internal.regions.EhcacheTimestampsRegion;
import org.hibernate.cache.ehcache.internal.strategy
  .EhcacheAccessStrategyFactory;
import org.hibernate.cache.ehcache.internal.strategy
  .EhcacheAccessStrategyFactoryImpl;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Region factory implementation.
 *
 * This adapts hibernate cache regions to ehcache caches.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class EhCacheRegionFactory implements RegionFactory {

  /** The serial version.*/
  private static final long serialVersionUID = 1L;

  /** The class logger.*/
  private Logger log = LoggerFactory.getLogger(EhCacheRegionFactory.class);

  /** Ehcache CacheManager that supplied Ehcache instances for this Hibernate
   * RegionFactory.
   *
   * This is never null.
   */
  private volatile CacheManager manager;

  /** The cacheConfigurations, never null.*/
  private final List<CacheConfiguration> cacheConfigurations;

  /** Mbean for hibernate management, never null.*/
  private final ProviderMBeanRegistrationHelper mBean;

  /** Factory for creating various access strategies, never null.*/
  private final EhcacheAccessStrategyFactory accessStrategyFactory;

  /** Settings object for the Hibernate persistence unit.
   *
   * Ignored if null.
   */
  private Settings hibernateSettings;

  /** True if the cache will record hit, miss and evict statistics. */
  private boolean statistics;

  /** Flag that checks if the region has been bootstraped or not.*/
  private boolean bootstraped = false;

  /** Creates a new instance of the KatariSingletonEhCacheRegionFactory.
   *
   * @param cacheManager the cache manager attached to this factory. It cannot
   * be null.
   *
   * @param cacheConfiguration the cache configuration for each region.  It
   * cannot be null.
   *
   * @param recordStatistics true if the cache will record hit, miss and evict
   * statistics.
   */
  public EhCacheRegionFactory(final CacheManager cacheManager,
      final List<CacheConfiguration> cacheConfiguration,
      final boolean recordStatistics) {
    Validate.notNull(cacheManager, "The cache manager cannot be null");
    Validate.notNull(cacheConfiguration,
        "The cache configuration cannot be null");
    manager = cacheManager;
    cacheConfigurations = cacheConfiguration;
    mBean = new ProviderMBeanRegistrationHelper();
    accessStrategyFactory = new NonstopAccessStrategyFactory(
        new EhcacheAccessStrategyFactoryImpl());
    statistics = recordStatistics;
  }

  /** {@inheritDoc} . */
  public synchronized void start(final Settings settings,
      final Properties properties) {
    log.trace("Entering start");
    if (!bootstraped) {
      log.debug("Starting the katari cache manager");
      if (settings == null) {
        hibernateSettings = settings;
      }
      for (CacheConfiguration config : cacheConfigurations) {
        log.debug("adding the cache: " + config.getName());
        config.setStatistics(statistics);
        manager.addCache(new Cache(config));
      }
      mBean.registerMBean(manager, properties);
      bootstraped = true;
    }
    log.trace("Leaving start");
  }

  /** {@inheritDoc} . */
  public synchronized void stop() {
    try {
      if (manager != null) {
        mBean.unregisterMBean();
        manager.shutdown();
        manager = null;
      }
    } catch (net.sf.ehcache.CacheException e) {
      throw new CacheException(e);
    }
  }

  /** {@inheritDoc} . */
  public boolean isMinimalPutsEnabledByDefault() {
    return true;
  }

  /** {@inheritDoc} . */
  public AccessType getDefaultAccessType() {
    return AccessType.READ_WRITE;
  }

  /** {@inheritDoc} . */
  public long nextTimestamp() {
    return Timestamper.next();
  }

  /** Retrieves the cache instance given by the supplied name, if the
   * given cache name it's not within the cache manager, adds a new cache
   * with the given name.
   *
   * @param name the name of the cache to retrieve. It cannot be null.
   *
   * @return the cache instance.  @throws CacheException
   */
  protected Ehcache getCache(final String name) {
    Validate.notNull(name, "The cache name cannot be null");
    try {
      Ehcache cache = manager.getEhcache(name);
      if (cache == null) {
        log.debug("Couldn't find a specific ehcache configuration "
            + "for cache named [" + name + "]; using defaults.");
        manager.addCache(name);
        cache = manager.getEhcache(name);
        log.debug("started EHCache region: " + name);
      }
      return cache;
    } catch (net.sf.ehcache.CacheException e) {
      throw new CacheException(e);
    }
  }

  /** {@inheritDoc} . */
  public EntityRegion buildEntityRegion(final String regionName,
      final Properties properties, final CacheDataDescription metadata) {
    return new EhcacheEntityRegion(accessStrategyFactory, getCache(regionName),
        hibernateSettings, metadata, properties);
  }

  /** {@inheritDoc} . */
  public CollectionRegion buildCollectionRegion(final String regionName,
      final Properties properties, final CacheDataDescription metadata) {
    return new EhcacheCollectionRegion(accessStrategyFactory,
        getCache(regionName), hibernateSettings, metadata, properties);
  }

  /** {@inheritDoc} . */
  public QueryResultsRegion buildQueryResultsRegion(final String regionName,
      final Properties properties) {
    return new EhcacheQueryResultsRegion(accessStrategyFactory,
        getCache(regionName), properties);
  }

  /** {@inheritDoc} . */
  public TimestampsRegion buildTimestampsRegion(final String regionName,
      final Properties properties) {
    return new EhcacheTimestampsRegion(accessStrategyFactory,
        getCache(regionName), properties);
  }

  /** {@inheritDoc}. */
  public NaturalIdRegion buildNaturalIdRegion(final String regionName,
      final Properties properties, final CacheDataDescription metadata)
      throws CacheException {
    return new EhcacheNaturalIdRegion(accessStrategyFactory,
        getCache(regionName), hibernateSettings, metadata, properties);
  }
}

