/* vim: set ts=2 et sw=2   cindent fo=qroca: */

package com.globant.katari.ehcache.application;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.Validate;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import com.globant.katari.core.application.Command;

/** Retrieves the list of active EH-caches.
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class GetCachesCommand implements Command<List<Cache>> {

  /** The cache manager to obtain the caches from, never null.
   */
  private CacheManager cacheManager;

  /** Constructor, builds a GetCachesCommand.
   *
   * @param theCacheManager the cache manager to obtain the caches from, never
   * null.
   */
  public GetCachesCommand(final CacheManager theCacheManager) {
    Validate.notNull(theCacheManager, "The cache manager cannot be null.");
    cacheManager = theCacheManager;
  }

  /** {@inheritDoc} . */
  public List<Cache> execute() {
    String[] names = cacheManager.getCacheNames();
    List<Cache> caches = new LinkedList<Cache>();
    for (String name : names) {
      caches.add(cacheManager.getCache(name));
    }
    return caches;
  }
}

