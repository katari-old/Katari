/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.tools;

import java.util.List;
import java.util.LinkedList;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.ListItem;

import org.apache.wicket.model.CompoundPropertyModel;

public class WicketTestPage extends WebPage {

  public static class User {
    public User(final String theName, final String theEmail) {
      name = theName;
      email = theEmail;
    }
    public String name;
    public String email;
  };

  @SuppressWarnings("serial")
  public WicketTestPage() {
    List<User> users = new LinkedList<User>();
    users.add(new User("name1", "email1"));
    users.add(new User("name2", "email2"));
    users.add(new User("name3", "email3"));
    users.add(new User("name4", "email4"));

    add(new ListView<User>("users", users) {
      public void populateItem(final ListItem<User> item) {
        final User user = item.getModelObject();
        item.setModel(new CompoundPropertyModel<User>(user));
        item.add(new Label("name"));
        item.add(new Label("email"));
        item.add(new Link<User>("delete") {
          @Override
          public void onClick() {
          }
        });
      }
    });
  }
}

