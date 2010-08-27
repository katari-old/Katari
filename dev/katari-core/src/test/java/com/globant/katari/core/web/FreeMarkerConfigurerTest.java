/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.lang.reflect.Field;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import freemarker.cache.MultiTemplateLoader;

import org.springframework.ui.freemarker.SpringTemplateLoader;

public class FreeMarkerConfigurerTest {

  @Test
  public void testGetTemplateLoaderForPath_noDebug() throws Exception {

    FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
    configurer.setTemplateLoaderPath(".");
    configurer.afterPropertiesSet();

    configurer.getConfiguration();
    assertEquals(SpringTemplateLoader.class,
        configurer.getTemplateLoaderForPath(".").getClass());
  }

  @Test
  public void testGetTemplateLoaderForPath_debug() throws Exception {

    FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
    configurer.setTemplateLoaderPath(".");
    configurer.setDebug(true);
    configurer.setDebugPrefix(".");
    configurer.afterPropertiesSet();

    configurer.getConfiguration();
    assertEquals(MultiTemplateLoader.class,
        configurer.getTemplateLoaderForPath(".").getClass());
  }

  @Test
  public void testSetDebugPrefix_removeTrailingSlash() throws Exception {

    FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
    configurer.setDebugPrefix("something/");
    Field prefix = FreeMarkerConfigurer.class. getDeclaredField("debugPrefixes");
    prefix.setAccessible(true);
    assertEquals("something", ((List) prefix.get(configurer)).get(0));
  }
}

