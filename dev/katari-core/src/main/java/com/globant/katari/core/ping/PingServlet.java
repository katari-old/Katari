/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.ping;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/** Servlet that runs a smoke test to check if the application has started
 * successfully.
 *
 * It loads a bean from the spring web application context. By default this
 * bean is called 'katari.pingServices'. This bean is a list of PingService
 * implementations. This servlet pings each service and reports the results in
 * plain text to the web client.
 *
 * The output includes lines of the form:
 *
 * Testing "service name': status'.
 *
 * with a last line of the form:
 *
 * Application started successfully.
 *
 * or
 *
 * Application startup failed.
 *
 * This allows the user to simply grep for the the output to run a quick test
 * to check if the application is running.
 *
 * Given that some tests can be relatively expensive, and the page is not
 * secured, this servlet caches the result of the pings for 30 seconds.
 */
public class PingServlet extends HttpServlet {

  /** The version id for this serializable class.
   */
  private static final long serialVersionUID = 1;

  /** The number of milliseconds that the ping result is cached for.
   */
  private static final long CACHE_TIMEOUT_MILLIS = 30000;

  /** The last time the ping operations where called, or the epoch if never
   * called.
   */
  private Date lastCalled = new Date(0);

  /** The result of the last call to the ping operations, null if never called.
   */
  private String lastResult = null;

  /** This method checks the application status.
   *
   * @param request The HTTP request we are processing.
   *
   * @param response The HTTP response we are processing.
   *
   * @throws IOException in case of error writing to the client.
   */
  protected final void service(final HttpServletRequest request,
      final HttpServletResponse response) throws IOException {

    response.setContentType("text/plain");

    if (lastCalled.getTime() + CACHE_TIMEOUT_MILLIS < new Date().getTime()) {
      lastResult = doPingServices().toString();
      lastCalled = new Date();
    }

    PrintWriter out = response.getWriter();
    try {
      out.println(lastResult);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

  /** Executes all the ping services and generates a StringBuilder with the
   * result.
   *
   * @return a StringBuilder with the result of executing all ping operations
   * in the ping services.
   */
  private StringBuilder doPingServices() {
    boolean isOk = true;

    StringBuilder output = new StringBuilder();
    List<PingService> services = getPingServices();

    if (services == null) {
      output.append("Loading spring context: FAIL\n");
    } else {
      output.append("Loading spring context: SUCCESS\n");

      for (final PingService service : services) {
        PingResult result = service.ping();
        output.append(result.getMessage() + "\n");
        if (!result.isOk()) {
          isOk = false;
        }
      }
    }

    if (isOk) {
      output.append("Application started successfully");
    } else {
      output.append("Application startup failed");
    }
    return output;
  }

  /** Gets the ping service from the spring web application context.
   *
   * @return the list of ping services or null if an error ocurred.
   */
  @SuppressWarnings("unchecked")
  private List<PingService> getPingServices() {
    List<PingService> services = null;
    WebApplicationContext context = WebApplicationContextUtils
        .getWebApplicationContext(getServletContext());
    if (context != null) {
      services = (List<PingService>) context.getBean("katari.pingServices");
    }
    return services;
  }
}

