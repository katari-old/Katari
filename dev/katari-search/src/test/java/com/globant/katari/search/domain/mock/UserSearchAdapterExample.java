/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain.mock;

import java.util.ArrayList;

import com.globant.katari.search.domain.SearchAdapter;
import com.globant.katari.search.domain.SearchResultElement;
import com.globant.katari.search.domain.Action;

import com.globant.katari.hibernate.role.domain.Role;

/** An example handler, for converting User objects into search results.
 *
 * @author nira.amit@globant.com
 */
public class UserSearchAdapterExample implements SearchAdapter {

  public SearchResultElement convert(final Object o, final float score) {

    User user = (User) o;

    ArrayList<Action> actions;
    actions = new ArrayList<Action>();

    actions.add(new Action("Edit", null,
          "userEdit.do?id=" + user.getId()));
    actions.add(new Action("Delete", null,
          "userDelete.do?id=" + user.getId()));

    StringBuilder roles = new StringBuilder();
    for (Role role: user.getRoles()) {
      if (roles.length() != 0) {
        roles.append(", ");
      }
      roles.append(role.getName());
    }

    StringBuilder description = new StringBuilder();
    description.append("User - name: " + user.getName());
    description.append("; email: " + user.getEmail());
    if (user.getRoles().size() != 0) {
      description.append("; roles: " + roles.toString());
    }

    return new SearchResultElement("User", user.getName(),
        description.toString(), "user.do?id=" + user.getId(), actions, score);
  }

  public String getViewUrl() {
    return "/module/user/user.do";
  }

  @SuppressWarnings("unchecked")
  public Class getAdaptedClass() {
    return User.class;
  }
}

