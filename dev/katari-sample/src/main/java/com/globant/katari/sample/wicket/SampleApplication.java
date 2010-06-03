/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.wicket;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;
import org.apache.wicket.settings.IRequestCycleSettings;
import org.apache.wicket.settings.IResourceSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

import com.globant.katari.sample.wicket.view.UserListPage;
import com.globant.katari.sample.wicket.view.UserPage;

/** An example wicket application.
 */
public class SampleApplication extends WebApplication implements
    MessageSourceAware {

  /** The katari debug mode.
   */
  private boolean debugMode = false;

  /** The message source.
   */
  private MessageSource messageSource;

  /** Creates a wicket application.
   *
   * @param isDebug true if the application will start in debug mode
   * (DEVELOPMENT mode in wicket parlance.)
   */
  public SampleApplication(final boolean isDebug) {
    debugMode = isDebug;
  }

  /** Called by wicket to initialize the application.
   */
  public void init() {
    /*
     * The default REDIRECT_TO_BUFFER option does not play well with sitemesh.
     */
    getRequestCycleSettings().setRenderStrategy(
        IRequestCycleSettings.ONE_PASS_RENDER);

    addComponentInstantiationListener(new SpringComponentInjector(this));
    IResourceSettings resourceSettings = getResourceSettings();
    if (this.messageSource != null) {
      // Try to resolve the messages using spring first.
      MessageSourceStringResourceLoader loader;
      loader = new MessageSourceStringResourceLoader(messageSource);
      resourceSettings.addStringResourceLoader(0, loader);
    }
    // Makes wicket load markup files from the file system, even if they are in
    // a jar.
    resourceSettings.addResourceFolder("../katari-sample/src/main/resources");

    /* Removes the wicket:xx from the generated markup. */
    getMarkupSettings().setStripWicketTags(true);

    // Makes the UserListPage available under the users url.
    mountBookmarkablePage("/users", UserListPage.class);
    // Makes the UserPage available under the user url.
    mount(new MixedParamUrlCodingStrategy("user", UserPage.class,
          new String[]{"id"}));
  }

  /** The home page of this wicket module.
   *
   * @return the class for the UserListPage, never null.
   */
  public Class<UserListPage> getHomePage() {
    return UserListPage.class;
  }

  /** Sets the configuration type to match katari debug mode.
   *
   * @return DEVELOPMENT in debug mode, DEPLOYMENT otherwise.
   */
  @Override
  public java.lang.String getConfigurationType() {
    if (debugMode) {
      return DEVELOPMENT;
    } else {
      return DEPLOYMENT;
    }
  }

  /**
   * Sets the message source.
   * @param theMessageSource
   *          The message source to set.
   */
  public void setMessageSource(final MessageSource theMessageSource) {
    this.messageSource = theMessageSource;
  }
}

