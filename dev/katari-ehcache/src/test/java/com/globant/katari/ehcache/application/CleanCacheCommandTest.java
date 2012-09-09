package com.globant.katari.ehcache.application;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Before;
import org.junit.Test;

import com.globant.katari.ehcache.SpringTestUtils;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class CleanCacheCommandTest {

  private CleanCacheCommand command;

  @Before
  public void setUp() throws Exception {
    command = (CleanCacheCommand) SpringTestUtils.getBean("cleanCacheCommand");
  }

  @Test
  public void testExecute() {
    CacheManager manager = CacheManager.getInstance();
    String[] names = manager.getCacheNames();
    Cache cache = manager.getCache(names[0]);

    Element element = new Element("someKey", "theValue");
    cache.put(element);

    Element fromCacheElement = cache.get("someKey");
    assertThat(fromCacheElement.getValue(), is(element.getValue()));

    command.setCacheName(cache.getName());
    command.execute();

    Element afterCleanElement = cache.get("someKey");
    assertThat(afterCleanElement, nullValue());
  }
}

