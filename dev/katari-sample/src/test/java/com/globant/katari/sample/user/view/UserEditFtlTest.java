package com.globant.katari.sample.user.view;

import com.globant.katari.tools.FreemarkerTestEngine;
import com.globant.katari.sample.testsupport.SpringTestUtils;
import com.globant.katari.sample.user.application.SaveUserCommand;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tests the userEdit.ftl.
 *
 * @author jose.dominguez
 *
 */
public class UserEditFtlTest extends TestCase {

  /**
   * Tests the userEdit.ftl.
   */
  public final void testUserEditFTL() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*<title>Edit User</title>.*");
    valid.add(".*<input .* name=\"profile.name\" value=\"newUser\".*");
    valid.add(".*<input .* name=\"profile.email\" value=\"mail@none\".*");
    valid.add(".*<input .* name=\"profile.email\" value=\"mail@none\".*");
    valid.add(".*<input .* name=\"profile.roleIds\" value=\"1\".*");
    valid.add(".*<input [^>]* type=\"submit\" value=\"Save\"/>.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/sample/user/view", Locale.ENGLISH, buildModel());
    engine.runAndValidate("userEdit.ftl", valid, invalid);
  }

  private Map<java.lang.String, java.lang.Object> buildModel() {
    // Building Model
    SaveUserCommand saveUserCommand = (SaveUserCommand) SpringTestUtils
        .getBeanFactory().getBean("editUserCommand");

    saveUserCommand.init();
    saveUserCommand.setUserId(1);
    saveUserCommand.getProfile().setName("newUser");
    saveUserCommand.getProfile().setEmail("mail@none");

    Map<java.lang.String, java.lang.Object> root;
    root = new HashMap<java.lang.String, java.lang.Object>();
    root.put("command", saveUserCommand);
    return root;
  }
}

