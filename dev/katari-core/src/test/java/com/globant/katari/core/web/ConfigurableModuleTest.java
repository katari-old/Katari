/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import org.junit.Test;
import org.junit.Before;

import com.globant.katari.core.security.StaticUrlToRoleMapper;
import com.globant.katari.core.security.UrlToRoleMapper;

import com.globant.katari.core.spring.KatariMessageSource;

public class ConfigurableModuleTest {

  KatariMessageSource messageSource;

  @Before public void setUp() {
    messageSource = new KatariMessageSource(Locale.US);
    messageSource.setBasename("classpath:katariMessageSource");
  }

  @Test public void init() {

    List<FilterMapping> filters = new LinkedList<FilterMapping>();

    Map<String, ServletAndParameters> entryPoints;
    entryPoints = new HashMap<String, ServletAndParameters>();

    Map<String, String> weblets = new HashMap<String, String>();

    MenuBar menuBar = new MenuBar();

    UrlToRoleMapper urlToRoleMapper
      = new StaticUrlToRoleMapper(new HashMap<String, String[]>());

    Class<?>[] persistentClasses = new Class<?>[1];
    persistentClasses[0] = getClass();

    Class<?>[] crudClasses = new Class<?>[1];
    crudClasses[0] = getClass();

    ConfigurableModule module = new ConfigurableModule();
    module.setFilters(filters);
    module.setEntryPoints(entryPoints);
    module.setWeblets(weblets);
    module.setMenuBar(menuBar);
    module.setUrlToRoleMapper(urlToRoleMapper);

    ModuleContext context = createMock(ModuleContext.class);
    context.registerFilters(filters);
    context.registerEntryPoints(entryPoints);
    context.registerWeblets(weblets);
    expect(context.getMessageSource()).andReturn(messageSource);
    context.registerMenu(menuBar);
    context.registerUrlToRoleMapper(urlToRoleMapper);
    replay(context);

    module.init(context);
    module.destroy();
    verify(context);
  }

  @Test public void init_menuWithMessageSource() {

    List<FilterMapping> filters = new LinkedList<FilterMapping>();

    Map<String, ServletAndParameters> entryPoints;
    entryPoints = new HashMap<String, ServletAndParameters>();

    Map<String, String> weblets = new HashMap<String, String>();

    MenuBar menuBar = createMock(MenuBar.class);
    menuBar.setMessageSource(messageSource);
    replay(menuBar);

    UrlToRoleMapper urlToRoleMapper
      = new StaticUrlToRoleMapper(new HashMap<String, String[]>());

    Class<?>[] persistentClasses = new Class<?>[1];
    persistentClasses[0] = getClass();

    Class<?>[] crudClasses = new Class<?>[1];
    crudClasses[0] = getClass();

    ConfigurableModule module = new ConfigurableModule();
    module.setMessageSource(messageSource);
    module.setFilters(filters);
    module.setEntryPoints(entryPoints);
    module.setWeblets(weblets);
    module.setMenuBar(menuBar);
    module.setUrlToRoleMapper(urlToRoleMapper);

    ModuleContext context = createMock(ModuleContext.class);
    context.registerFilters(filters);
    context.registerEntryPoints(entryPoints);
    context.registerWeblets(weblets);
    expect(context.getMessageSource()).andReturn(messageSource);
    context.registerMenu(menuBar);
    context.registerUrlToRoleMapper(urlToRoleMapper);
    replay(context);

    module.init(context);

    verify(menuBar);
  }
}

