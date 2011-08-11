#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package ${package}.web.functionaltest;

import java.util.LinkedList;
import java.util.List;

import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.MultipleFailureException;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.HttpMethod;

/** Hits each registered page and verifies that they don't generate an
 * exception.
 */
@RunWith(PagesSmokeTest.class)
public class PagesSmokeTest extends ParentRunner<String> {

  private static final String LOGIN_URL = "/module/local-login/login.do";

  private WebClient webClient;

  /** Constructor.
   *
   * @param testClass the test class.
   *
   * @throws InitializationError initialization error.
   */
  public PagesSmokeTest(final java.lang.Class<?> testClass)
      throws InitializationError {
    super(testClass);
  }

  /** Returns the list of tests.
   *
   * This runner considers each page to be a test. This method returns all the
   * registered pages.
   */
  protected List<String> getChildren() {
    List<String> pages = new LinkedList<String>();
    pages.add("");
    pages.add("/");
    return pages;
  }

  /** Returns the page as the description for it.
   *
   * @param child the page to describe.
   *
   * @return the description for the given page.
   */
  protected Description describeChild(final String child) {
    return Description.createTestDescription(PagesSmokeTest.class, child);
  }

  /** Runs the test corresponding to each page.
   *
   * The test consists on hitting the application with the page link and
   * verifying the resulting page. This method also calls the corresponding
   * notifier methods to inform the listeners about the status of the test run.
   * This lets, for example, maven and eclipse, generate a report and progress
   * bar respectively.
   *
   * @param child the menu node to navigate.
   *
   * @param notifier the junit run notifier.
   */
  protected void runChild(final String child, final RunNotifier notifier) {
    notifier.fireTestStarted(describeChild(child));
    try {
      if (webClient == null){
        webClient = SimplePageVerifier.login(LOGIN_URL);
        webClient.setJavaScriptEnabled(false);
      }
      SimplePageVerifier.verifyPage(webClient, child, "", HttpMethod.GET, ".*",
          new String[] { "(?s).*Administration.*" }, new String[] {
            ".*Exception.*", ".*Not Found.*" });
    } catch (AssumptionViolatedException e) {
      notifier.fireTestAssumptionFailed(new Failure(describeChild(child), e));
    } catch (Throwable e) {
      addFailure(notifier, describeChild(child), e);
    } finally {
      notifier.fireTestFinished(describeChild(child));
    }
  }

  /** Fires a test failure for a given exception.
   *
   * @param notifier the junit run notifier.
   *
   * @param description the test description.
   *
   * @param targetException the target exception.
   */
  private void addFailure(final RunNotifier notifier,
      final Description description, final Throwable targetException) {
    if (targetException instanceof MultipleFailureException) {
      MultipleFailureException mfe= (MultipleFailureException) targetException;
      for (Throwable each : mfe.getFailures())
        addFailure(notifier, description, each);
      return;
    }
    notifier.fireTestFailure(new Failure(description, targetException));
  }
}

