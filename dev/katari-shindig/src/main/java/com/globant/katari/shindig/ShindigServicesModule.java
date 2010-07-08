/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig;

import org.apache.commons.lang.Validate;
import org.apache.shindig.social.core.config.SocialApiGuiceModule;
import org.apache.shindig.social.opensocial.oauth.OAuthDataStore;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.AppDataService;
import org.apache.shindig.social.opensocial.spi.MessageService;
import org.apache.shindig.social.opensocial.spi.PersonService;
import org.apache.shindig.social.sample.oauth.SampleOAuthDataStore;
// import org.apache.shindig.social.sample.oauth.SampleRealm;
import org.apache.shindig.social.sample.spi.JsonDbOpensocialService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.inject.name.Names;

/** Bindings for katari implementation of shindig services.
 *
 * This Guice module implements ApplicationContextAware to be notified by the
 * GuiceInitializerListener of the spring application context.
 */
public class ShindigServicesModule extends SocialApiGuiceModule {

  /** The implementation of the activity service, never null.
   */
  private ActivityService activityService;

  /** Constructor.
   *
   * @param activityService The implementation of the activity service. It
   * cannot be null.
   */
  public ShindigServicesModule(final ActivityService activityServiceImpl) {
    Validate.notNull(activityServiceImpl,
        "The activity service implementation cannot be null.");
    activityService = activityServiceImpl;
  }

  /** Wires the shindig services to the corresponding Katari implementations.
   * 
   * Katari services are obtained from the application context.
   * 
   * This implementation only wires the ActivityService to Katari, the other
   * services get wired to the shindig provided mock.
   */
  @Override
  protected void configure() {
    super.configure();

    bind(ActivityService.class).toInstance(activityService);

    bind(String.class).annotatedWith(Names.named("shindig.canonical.json.db"))
        .toInstance("sampledata/canonicaldb.json");
    bind(AppDataService.class).to(JsonDbOpensocialService.class);
    bind(PersonService.class).to(JsonDbOpensocialService.class);
    bind(MessageService.class).to(JsonDbOpensocialService.class);

    bind(OAuthDataStore.class).to(SampleOAuthDataStore.class);
  }
}

