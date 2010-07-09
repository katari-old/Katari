#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package com.globant.${clientName}.${projectName}.web.user.view;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.hibernate.coreuser.domain.RoleRepository;

import com.globant.katari.tools.FreemarkerTestEngine;
import com.globant.${clientName}.${projectName}.web.testsupport.SpringTestUtils;
import com.globant.${clientName}.${projectName}.web.user.application.SaveUserCommand;
import com.globant.${clientName}.${projectName}.web.user.domain.User;
import com.globant.${clientName}.${projectName}.web.user.domain.UserRepository;

import freemarker.template.Configuration;
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
    valid.add(".*<input .* name=${symbol_escape}"profile.name${symbol_escape}" value=${symbol_escape}"newUser${symbol_escape}".*");
    valid.add(".*<input .* name=${symbol_escape}"profile.email${symbol_escape}" value=${symbol_escape}"mail@none${symbol_escape}".*");
    valid.add(".*<input .* name=${symbol_escape}"profile.email${symbol_escape}" value=${symbol_escape}"mail@none${symbol_escape}".*");
    valid.add(".*<input .* name=${symbol_escape}"profile.roleIds${symbol_escape}" value=${symbol_escape}"1${symbol_escape}".*");
    valid.add(".*<input [^>]* type=${symbol_escape}"submit${symbol_escape}" value=${symbol_escape}"Save${symbol_escape}"/>.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/${clientName}/${projectName}/web/user/view", Locale.ENGLISH, buildModel());
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

