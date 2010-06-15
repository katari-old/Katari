/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.trails;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Registry;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.impl.XmlModuleDescriptorProvider;
import org.apache.hivemind.service.ThreadLocale;
import org.apache.tapestry.ApplicationServlet;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support
  .TransactionSynchronizationManager;
import org.springframework.web.context.WebApplicationContext;

import com.globant.katari.core.web.ServletConfigWrapper;

/** Servlet used by trails modules.
 *
 * It extends ApplicationServlet and redefines the constructRegistry method in
 * order to look for extra locations of the hivemodule.xml file.<br>
 *
 * It also wraps the ServletContext to create a scoped application context,
 * adding beans that are private to this module without affecting the
 * application context.<br>
 *
 * Finally, it implements the open session in view for trails.
 *
 * To configure where to find the hivemodule file, you have to define an init
 * parameter with name "hivemindModulePath" with the location of your hivemind
 * module configuration.<br>
 *
 * You also have to define an init parameter with the name
 * "trailsApplicationContextLocation" that points to the location of the
 * configuration of the bean factory for this module.<br>
 */
public class TrailsModuleApplicationServlet extends ApplicationServlet {

  /**
   * Serial uid.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Name of the init parameter used to indicate where the hivemodule is.
   */
  private static final String HIVEMIND_MODULE_PATH = "hivemindModulePath";

  /**
   * Name for the init parameter that specifies the location of the xml file
   * which configures the context for this module, typically named
   * module-beans.xml.
   */
  private static final String CONTEXT_LOCATION =
      "trailsApplicationContextLocation";

  /** The class logger.
  */
  private static Log log = LogFactory.getLog(
      TrailsModuleApplicationServlet.class);

  /** The spring bean factory used in trails.
   *
   * This is initialized when this servlet is initialized, after that it is not
   * null.
   */
  private BeanFactory beanFactory = null;

  /** This is used to share the Registry through all the application.
   *
   * TODO this makes it impossible to use several instances of trails.
   */
  private static Registry tapestryRegistry = null;

  /** Processes the request.
   *
   * This method implements the open session in view, opening a new hibernate
   * session when the request starts and closing the session on end.
   *
   * {@inheritDoc}
   */
  public void service(final ServletRequest request, final ServletResponse
      response) throws ServletException, IOException {

    SessionFactory sessionFactory;
    sessionFactory = (SessionFactory) beanFactory.getBean("sessionFactory");

    Session session = null;

    // True if there was a previous hibernate session active. In that case, we
    // just leave it alone, efectively 'participating' in the existing session.
    boolean participate = false;

    // True if this is the first time we call this servlet in the current
    // request.
    boolean firstCall = false;

    // The name of the attribute to check if the request has already been
    // handled by this servlet. We open the hibernate session the first time
    // only.
    String alreadyHandled = getServletName() + ".SESSION_OPENED";

    // Check if we have already entered this servlet in the current request.
    if (null == request.getAttribute(alreadyHandled)) {
      request.setAttribute(alreadyHandled, "filtered");
      if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
        // Do not modify the Session: just set the participate flag.
        participate = true;
      } else {
        log.debug("Opening single Hibernate Session in open session in view");
        session = getSession(sessionFactory);
        TransactionSynchronizationManager.bindResource(sessionFactory,
            new SessionHolder(session));
      }
      firstCall = true;
    }

    /* This is due to a bug in tapestry 4.1.5: when rendenig the exception
     * page, tapestry iterates recursively over all request attributes. Katari
     * puts for convenience, an atribute named 'request' that contains the
     * servlet request. This makes tapestry iterate infinitely through the
     * attributes, causing a stack trace exception.
     *
     * The bug is in tapestry HttpServletRequestStrategy.describeObject.
     */
    Object savedRequest = request.getAttribute("request");
    request.removeAttribute("request");
    try {
      super.service(request, response);
    } finally {
      request.setAttribute("request", savedRequest);
      if (firstCall) {
        if (!participate) {
          TransactionSynchronizationManager.unbindResource(sessionFactory);
          log.debug(
              "Closing single Hibernate session in open session in view");
          closeSession(session, sessionFactory);
        }
        request.removeAttribute(alreadyHandled);
      }
    }
  }

  /** Get a Session for the SessionFactory that this servlet uses.
   *
   * Note that this just applies in single session mode!  <p>The default
   * implementation delegates to SessionFactoryUtils' getSession method and
   * sets the Session's flushMode to NEVER.  <p>Can be overridden in subclasses
   * for creating a Session with a custom entity interceptor or JDBC exception
   * translator.
   *
   * @param sessionFactory the SessionFactory that this filter uses.
   *
   * @return the Session to use.
   */
  protected Session getSession(final SessionFactory sessionFactory) {
    Session session = SessionFactoryUtils.getSession(sessionFactory, true);
    session.setFlushMode(FlushMode.MANUAL);
    return session;
  }

  /** Close the given Session.
   *
   * Note that this just applies in single session mode!
   *
   * <p>The default implementation delegates to SessionFactoryUtils'
   * closeSessionIfNecessary method.
   *
   * <p>Can be overridden in subclasses, e.g. for flushing the Session before
   * closing it. See class-level javadoc for a discussion of flush handling.
   * Note that you should also override getSession accordingly, to set the
   * flush mode to something else than NEVER.
   *
   * @param session the Session used for filtering
   *
   * @param sessionFactory the SessionFactory that this filter uses
   */
  protected void closeSession(final Session session, final SessionFactory
      sessionFactory) {
    SessionFactoryUtils.closeSession(session);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Registry constructRegistry(final ServletConfig config) {

    String hivemodulePath = getInitParameter(HIVEMIND_MODULE_PATH);

    synchronized (TrailsModuleApplicationServlet.class) {

      if (hivemodulePath == null) {
        setRegistry(super.constructRegistry(config));
        return super.constructRegistry(config);
      } else {

        ErrorHandler errorHandler = constructErrorHandler(config);

        RegistryBuilder builder = new RegistryBuilder(errorHandler);

        builder.addModuleDescriptorProvider(new XmlModuleDescriptorProvider(
            createClassResolver()));

        ServletContext context = config.getServletContext();

        addModuleIfExists(builder, context, hivemodulePath);

        setRegistry(builder.constructRegistry(Locale.getDefault()));
      }
    }
    return tapestryRegistry;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final ServletConfig config) throws ServletException {
    beanFactory = createBeanFactory(config);

    TrailsModuleServletContext newContext = new TrailsModuleServletContext(
        beanFactory, config.getServletContext());

    ServletConfig newConfig = new ServletConfigWrapper(config, newContext);

    super.init(newConfig);
  }

  /**
   * Creates a BeanFactory child of the application context factory.
   *
   * The BeanFactory is created from the file specified in the servlet init
   * parameter named "trailsApplicationContextLocation".
   *
   * @param config The servlet config.
   *
   * @return the bean factory loaded from the configured location.
   */
  private BeanFactory createBeanFactory(final ServletConfig config) {

    String springConfigurationFile;
    springConfigurationFile = config.getInitParameter(CONTEXT_LOCATION);

    Validate.notNull(springConfigurationFile, "You should define '"
        + CONTEXT_LOCATION + "' init parameter for this servlet to work.");

    ApplicationContext parentContext = (ApplicationContext) config
        .getServletContext().getAttribute(
            WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

    Validate.notNull(parentContext,
      "There should be one Web Application Context for this servlet to work.");

    return new ClassPathXmlApplicationContext(
        new String[] {springConfigurationFile}, parentContext);
  }

  public static Locale getCurrentLocale() {
    return ((ThreadLocale) tapestryRegistry.getService("hivemind.ThreadLocale",
          ThreadLocale.class)).getLocale();
  }

  /** Sets the class level registry, defined only to make it clear that we are
   * setting the registry from a non static method.
   *
   * @param registry The new registry, it cannot be null.
   */
  private static void setRegistry(final Registry registry) {
    Validate.notNull(registry, "The registry cannot be null.");
    tapestryRegistry = registry;
  }
}

