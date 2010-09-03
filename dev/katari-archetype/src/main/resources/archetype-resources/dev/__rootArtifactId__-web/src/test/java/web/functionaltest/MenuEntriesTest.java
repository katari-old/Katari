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

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.globant.katari.core.web.MenuBar;
import com.globant.katari.core.web.MenuNode;
import com.globant.katari.core.web.ModuleContextRegistrar;
import ${package}.web.testsupport.SpringTestUtils;

/** This test navigates all the katari menu entries and verifies that each page
 * contains the logged in user and no Exception thrown.
 *
 * To improve the error reporting, this test contains its own TestRunner. This
 * test runner exposes each menu as a single test, so if the application has 10
 * menu items, the junit reports that it ran 10 tests.
 *
 * This test can be used an example for dynamic test generation with junit4.
 * @author gerardo.bercovich
 */
@RunWith(MenuEntriesTest.class)
public class MenuEntriesTest extends ParentRunner<MenuNode> {

  private static final String LOGIN_URL = "/module/local-login/login.do";

  private WebClient webClient;

  /** The MenuEntriesTest Constructor.
   * @param klass the test class.
   * @throws InitializationError initialization error.
   */
  public MenuEntriesTest(final java.lang.Class<?> klass)
      throws InitializationError {
    super(klass);
  }

  /**
   * Returns the list of tests. This runner considers a menu node to be a test.
   * This method returns all the nodes that has links.
   */
  protected List<MenuNode> getChildren() {
    ModuleContextRegistrar registrar = (ModuleContextRegistrar) SpringTestUtils
        .getBeanFactory().getBean("katari.contextRegistrar");
    MenuBar menuBar = registrar.getMenuBar();
    return getMenuLeaves(menuBar);
  }

  /** Retrieves the current application menu links.
   * @return the application menu links.
   */
  private List<MenuNode> getMenuLeaves(final MenuNode menuNode) {
    List<MenuNode> pathList = new LinkedList<MenuNode>();
    List<MenuNode> childNodes = menuNode.getChildNodes();
    for (MenuNode currentNode : childNodes) {
      String linkPath = currentNode.getLinkPath();
      if (linkPath != null && linkPath.length() != 0) {
        pathList.add(currentNode);
      }
      if (!currentNode.isLeaf() && currentNode.getChildNodes().size() > 0) {
        pathList.addAll(getMenuLeaves(currentNode));
      }
    }
    return pathList;
  }

  /** Returns the description for a menu node, consisting of the menu node path.
   * @param child the menu node to describe.
   * @return the description for the given menu node.
   */
  protected Description describeChild(final MenuNode child) {
    return Description.createTestDescription(MenuEntriesTest.class,
        child.getPath());
  }

  /** Runs the test corresponding to the menu node.
   *
   * The test consists on hitting the application with the menu node link and
   * verifying the resulting page.
   * This method also calls the corresponding notifier methods to inform the
   * listeners about the status of the test run. This lets, for example, maven
   * and eclipse, generate a report and progress bar respectively.
   * @param child the menu node to navigate.
   * @param notifier the junit run notifier.
   */
  protected void runChild(final MenuNode child, final RunNotifier notifier){
    notifier.fireTestStarted(describeChild(child));
    try {
      if(webClient == null){
        webClient = SimplePageVerifier.login(LOGIN_URL);
        webClient.setJavaScriptEnabled(false);
      }
      SimplePageVerifier.verifyPage(webClient, child.getLinkPath(), "",
          HttpMethod.GET, ".*", new String[] { "(?s).*admin.*" }, new String[]
          { ".*Exception.*", ".*Not Found.*" });
    } catch (AssumptionViolatedException e) {
      notifier.fireTestAssumptionFailed(new Failure(describeChild(child), e));
    } catch (Throwable e) {
      addFailure(notifier, describeChild(child), e);
    } finally {
      notifier.fireTestFinished(describeChild(child));
    }
  }

  /** Fires a test failure for a given exception.
   * @param notifier the junit run notifier.
   * @param description the test description.
   * @param targetException the target exception.
   */
  private void addFailure(final RunNotifier notifier,
      final Description description,
      final Throwable targetException) {
    if (targetException instanceof MultipleFailureException) {
      MultipleFailureException mfe= (MultipleFailureException) targetException;
      for (Throwable each : mfe.getFailures())
        addFailure(notifier, description, each);
      return;
    }
    notifier.fireTestFailure(new Failure(description, targetException));
  }
}
