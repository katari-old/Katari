/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.view;

import com.globant.katari.tools.FreemarkerTestEngine;
import com.globant.katari.user.application.Password;
import com.globant.katari.user.application.Profile;
import com.globant.katari.user.application.SaveUserCommand;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Tests the userEdit.ftl.
 *
 * @author jose.dominguez
 */
public class UserEditFtlTest {

  /** Tests the userEdit.ftl.
   */
  @Test
  public final void testUserEditFTL() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*<title>Edit User</title>.*");
    valid.add(".*<input .* name=\"profile.name\" value=\"newUser\".*");
    valid.add(".*<input .* name=\"profile.email\" value=\"mail@none\".*");
    valid.add(".*<input .* name=\"profile.email\" value=\"mail@none\".*");
    valid.add(".*<input .* name=\"profile.roleIds\" value=\"[0-9]+\".*");
    valid.add(".*<input [^>]* type=\"submit\" value=\"Save\"/>.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add("Exception");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/user/view", Locale.ENGLISH, buildModel());
    engine.runAndValidate("userEdit.ftl", valid, invalid);
  }

  private Map<java.lang.String, java.lang.Object> buildModel() {
    // Building Model
    /*
    SaveUserCommand saveUserCommand = (SaveUserCommand) SpringTestUtils
        .getServletBean("editUserCommand");
        */
    
    SaveUserCommand command = createMock(SaveUserCommand.class);
    expect(command.getProfile()).andReturn(new Profile()).anyTimes();
    expect(command.getPassword()).andReturn(new Password()).anyTimes();
    expect(command.getUserId()).andReturn(1L).anyTimes();
    Map<String, String> availableRoles = new HashMap<String, String>();
    availableRoles.put("1", "ADMINISTRATOR");
    expect(command.getAvailableRoles()).andReturn(availableRoles).anyTimes();
    replay(command);

    command.getProfile().setName("newUser");
    command.getProfile().setEmail("mail@none");
    List<String> roleIds = new LinkedList<String>();
    roleIds.add("1");
    command.getProfile().setRoleIds(roleIds);

    // saveUserCommand.getProfile().setName("newUser");
    // saveUserCommand.getProfile().setEmail("mail@none");

    Map<java.lang.String, java.lang.Object> root;
    root = new HashMap<java.lang.String, java.lang.Object>();
    // root.put("command", saveUserCommand);
    root.put("command", command);
    return root;
  }
}

