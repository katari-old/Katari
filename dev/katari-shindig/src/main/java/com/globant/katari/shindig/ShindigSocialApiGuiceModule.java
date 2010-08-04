package com.globant.katari.shindig;

import static com.google.inject.name.Names.named;

import java.util.List;
import java.util.Set;

import org.apache.shindig.auth.AnonymousAuthenticationHandler;
import org.apache.shindig.auth.AuthenticationHandler;
import org.apache.shindig.common.servlet.ParameterFetcher;
import org.apache.shindig.protocol.DataServiceServletFetcher;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.protocol.conversion.BeanXStreamConverter;
import org.apache.shindig.protocol.conversion.xstream.XStreamConfiguration;
import org.apache.shindig.social.core.oauth.AuthenticationHandlerProvider;
import org.apache.shindig.social.core.util.BeanXStreamAtomConverter;
import org.apache.shindig.social.core.util.xstream.XStream081Configuration;
import org.apache.shindig.social.opensocial.service.ActivityHandler;
import org.apache.shindig.social.opensocial.service.AppDataHandler;
import org.apache.shindig.social.opensocial.service.MessageHandler;
import org.apache.shindig.social.opensocial.service.PersonHandler;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Shindig base Social API Module.
 *
 * This module, defines the handlers and the outputs format converters (xml,
 * json, etc).
 *
 * NOTE: When upgrading to next Shindig versions, CHECK the file
 * inside: org.apache.shindig.social.core.config.SocialApiGuiceModule and DOUBLE
 * CHECK the differences.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class ShindigSocialApiGuiceModule extends AbstractModule {

  /** {@inheritDoc} */
  @Override
  protected void configure() {

    bind(ParameterFetcher.class).annotatedWith(named("DataServiceServlet")).to(
        DataServiceServletFetcher.class);

    bind(Boolean.class).annotatedWith(
      named(AnonymousAuthenticationHandler.ALLOW_UNAUTHENTICATED))
      .toInstance(Boolean.FALSE);

    bind(XStreamConfiguration.class).to(XStream081Configuration.class);

    bind(BeanConverter.class)
      .annotatedWith(named("shindig.bean.converter.xml")).to(
      BeanXStreamConverter.class);

    bind(BeanConverter.class).annotatedWith(
      named("shindig.bean.converter.json")).to(BeanJsonConverter.class);

    bind(BeanConverter.class).annotatedWith(
      named("shindig.bean.converter.atom"))
      .to(BeanXStreamAtomConverter.class);

    bind(new TypeLiteral<List<AuthenticationHandler>>() { } )
      .toProvider(AuthenticationHandlerProvider.class);

    bind(new TypeLiteral<Set<Object>>() { } ).annotatedWith(
      named("org.apache.shindig.social.handlers")).toInstance(getHandlers());

    bind(Long.class).annotatedWith(
      named("org.apache.shindig.serviceExpirationDurationMinutes"))
     .toInstance(60L);
  }

  /**
   * Hook to provide a Set of request handlers. Subclasses may override to add
   * or replace additional handlers.
   */
  protected Set<Object> getHandlers() {
    return ImmutableSet.<Object> of(ActivityHandler.class,
        AppDataHandler.class, PersonHandler.class, MessageHandler.class);
  }
}

