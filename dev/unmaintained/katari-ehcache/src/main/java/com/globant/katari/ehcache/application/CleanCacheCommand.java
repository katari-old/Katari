package com.globant.katari.ehcache.application;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import com.globant.katari.core.application.Command;

/** Removes all the attached instances from the cache, given by its name.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class CleanCacheCommand implements Command<Void> {

  /** The class logger.*/
  private Logger log = LoggerFactory.getLogger(CleanCacheCommand.class);

  /** The name of the cache to clean, never null. */
  private String cacheName;

  /** Remove the instances from the cache given by its name.
   *
   * @return always returns null.
   */
  public Void execute() {
    Validate.notEmpty(cacheName, "The cache name cannot be null or empty");
    log.trace("Entering execute");
    log.debug("cleaning the cache with name: [ " + cacheName + " ]");
    CacheManager manager = CacheManager.getInstance();
    Cache cache = manager.getCache(cacheName);
    cache.removeAll();
    log.trace("Leaving execute");
    return null;
  }

  /** Sets the name of the cache to clean.
   * @param theCacheName the name of the cache to clean.
   */
  public void setCacheName(final String theCacheName) {
    cacheName = theCacheName;
  }
}

