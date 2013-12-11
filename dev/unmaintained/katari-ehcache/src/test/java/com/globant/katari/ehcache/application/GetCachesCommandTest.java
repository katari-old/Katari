package com.globant.katari.ehcache.application;

import static org.junit.Assert.*;

import java.util.List;

import net.sf.ehcache.Cache;

import org.junit.Before;
import org.junit.Test;

import static com.globant.katari.ehcache.SpringTestUtils.*;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class GetCachesCommandTest {

  private GetCachesCommand command;

  @Before
  public void setUp() throws Exception {
    command = (GetCachesCommand) getBean("getCachesCommand");
  }

  @Test
  public void testExecute() {
    List<Cache> caches = command.execute();
    assertTrue("Cache size cannot be zero", caches.size() > 0);
  }

}
