/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import java.io.File;
import freemarker.template.TemplateException;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import junit.framework.TestCase;
import junit.framework.AssertionFailedError;

import com.globant.katari.tools.FreemarkerTestEngine;

public class FreemarkerTestEngineTest extends TestCase {

  private static class MockProfile {
    @SuppressWarnings("unused")
    public String getName() {
     return "new user";
    }
    @SuppressWarnings("unused")
    public String getEmail() {
     return "new@user";
    }
  }

  private static class MockCommand {
    @SuppressWarnings("unused")
    public long getUserId() {
     return 10;
    }

    @SuppressWarnings("unused")
    public MockProfile getProfile() {
     return new MockProfile();
    }
  }

  private MockCommand command = new MockCommand();

  public void testRunAndValidate_validRegexpsSuccess() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*<title>Edit User</title>.*");
    valid.add(".*<input .* name=\"profile.name\" value=\"new user\".*");
    valid.add(".*<input .* name=\"profile.email\" value=\"new@user\".*");
    valid.add(".*<input  type=\"submit\" value=\"Save\"/>.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/tools", Locale.ENGLISH, buildModel());
    engine.runAndValidate("freemarkerTestEngineTest.ftl", valid,
        Collections.<String>emptyList());
  }

  public void testRunAndValidate_invalidRegexpsSuccess() throws Exception {

    List<String> invalid = new ArrayList<String>();
    invalid.add(".*Exception.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/tools", Locale.ENGLISH, buildModel());
    engine.runAndValidate("freemarkerTestEngineTest.ftl",
        Collections.<String>emptyList(), invalid);
  }

  // Tests that runAndValidate throws an exception if a valid regexp is not
  // found.
  public void testRunAndValidate_validRegexpsFail() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*<title>NON EXISTENT TITLE</title>.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/tools", Locale.ENGLISH, buildModel());
    boolean exceptionThrown = false;
    try {
      engine.runAndValidate("freemarkerTestEngineTest.ftl", valid,
          Collections.<String>emptyList());
      // Cannot use fail() here because it throws AssertionFailedError.
    } catch (AssertionFailedError e) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }

  // Tests that runAndValidate throws an exception if an invalid regexp is
  // found.
  public void testRunAndValidate_invalidRegexpsFail() throws Exception {

    List<String> invalid = new ArrayList<String>();
    invalid.add(".*Edit User.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/tools", Locale.ENGLISH, buildModel());
    boolean exceptionThrown = false;
    try {
      engine.runAndValidate("freemarkerTestEngineTest.ftl",
          Collections.<String>emptyList(), invalid);
      // Cannot use fail() here because it throws AssertionFailedError.
    } catch (AssertionFailedError e) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
  }

  public void testRunAndValidate_fileCreation() throws Exception {
    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/tools", Locale.ENGLISH, buildModel());
    engine.runAndValidate("freemarkerTestEngineTest.ftl",
        Collections.<String>emptyList(), Collections.<String>emptyList());
    String fileName = "target/freemarker-test/"
      + FreemarkerTestEngineTest.class.getName()
      + ".testRunAndValidate_fileCreation.html";
    File output = new File(fileName);
    assertTrue("The file " + fileName + " was not found.", output.exists());
  }

  @SuppressWarnings("unchecked")
  public void testRunAndValidate_springErrors() throws Exception {
    // Creates the basic model.
    Map<String, Object> model = buildModel();

    // Adds some validation errors to the model.
    BeanPropertyBindingResult result;
    result = new BeanPropertyBindingResult(command, "command");
    result.addError(new ObjectError("command.profile.name", new String[]{"1"},
          null, "This is the error message for the user name"));
    result.addError(new ObjectError("command.profile.email", new String[]{"1"},
          null, "This is the error message for the user email"));
    model.putAll(result.getModel());

    FreemarkerTestEngine engine;
    engine = new FreemarkerTestEngine("/com/globant/katari/tools", model);

    List<String> valid = new ArrayList<String>();
    valid.add(".*This is the error message for the user name.*");
    valid.add(".*This is the error message for the user email.*");

    engine.runAndValidate("freemarkerTestEngineTest.ftl", valid,
        Collections.<String>emptyList());
  }

  public void testRunAndValidate_importOk() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*Something to show.*");
    valid.add(".*<title>Edit User</title>.*");
    valid.add(".*<input .* name=\"profile.name\" value=\"new user\".*");
    valid.add(".*<input .* name=\"profile.email\" value=\"new@user\".*");
    valid.add(".*<input  type=\"submit\" value=\"Save\"/>.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        new String [] { "/com/globant/katari/tools",
        "/com/globant/katari/tools/templatelib" },
        Locale.ENGLISH, buildModel());
    engine.runAndValidate("freemarkerTestEngineImportTest.ftl", valid,
        Collections.<String>emptyList());
  }

  public void testRunAndValidate_importNotFound() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*<title>Edit User</title>.*");
    valid.add(".*<input .* name=\"profile.name\" value=\"new user\".*");
    valid.add(".*<input .* name=\"profile.email\" value=\"new@user\".*");
    valid.add(".*<input  type=\"submit\" value=\"Save\"/>.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/tools", Locale.ENGLISH, buildModel());
    try {
      engine.runAndValidate("freemarkerTestEngineImportTest.ftl", valid,
          Collections.<String>emptyList());
      fail("Should have failed with FileNotFoundException");
    } catch (TemplateException e) {
      // Ox, exception expected
    }
  }

  private Map<String, Object> buildModel() {
    // Building Model
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("command", command);
    return model;
  }
}

