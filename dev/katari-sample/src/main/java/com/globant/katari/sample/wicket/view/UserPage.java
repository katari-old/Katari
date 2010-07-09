/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.sample.wicket.view;

import java.util.List;

import org.apache.commons.lang.Validate;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.validation
  .EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import com.globant.katari.hibernate.coreuser.domain.Role;
import com.globant.katari.sample.user.application.SaveUserCommand;
import com.globant.katari.sample.user.domain.User;

/** A wicket page to add or modify a user.
 *
 * This page edits the profile for an existing user. For a new user, it also
 * edits the password fields.
 */
public class UserPage extends WebPage {

  /** The minimum password length.
   */
  private static final int MIN_PASSWORD_LENGTH = 6;

  /** The command to save the user.
   *
   * Injected by spring, never null.
   */
  @SpringBean
  private SaveUserCommand command;

  /** The id of the user being edited.
   *
   * This is 0 for a new user.
   */
  private long userId = 0L;

  /** Creates a user page for a new user.
   */
  public UserPage() {
    this(null);
  }

  /** Creates a user page.
   *
   * @param params The parameters passed to the page. This page accepts the
   * parameter 'id', that is the id of the user to edit. Null if the page is
   * created for a new user.
   */
  public UserPage(final PageParameters params) {
    final IModel<String> titleModel = new AbstractReadOnlyModel<String>() {
      @Override
      public String getObject() {
        if (userId == 0) {
          return "New user";
        } else {
          return "Edit user";
        }
      }
    };
    // Two labels to display the page title, one for <head> and one for <h1>
    add(new Label("head-title-label", titleModel).setRenderBodyOnly(true));
    add(new Label("page-title-label", titleModel));
    if (params != null) {
      this.userId = params.getAsLong("id", 0L);
    }
    command.setUserId(this.userId);
    command.init();
    Form<SaveUserCommand> form = new Form<SaveUserCommand>("userForm",
        new CompoundPropertyModel<SaveUserCommand>(command)) {
          @Override
          protected void onSubmit() {
            getModelObject().execute();
            setResponsePage(UserListPage.class);
          }
    };

    form.add(new RequiredTextField<SaveUserCommand>("profile.name"));
    form.add(new RequiredTextField<SaveUserCommand>("profile.email"));
    PasswordTextField password = new PasswordTextField(
        "password.newPassword");
    password.setResetPassword(false);
    password.add(StringValidator.minimumLength(MIN_PASSWORD_LENGTH));
    PasswordTextField confirmPassword = new PasswordTextField(
        "password.confirmedPassword");
    confirmPassword.setResetPassword(false);
    WebMarkupContainer passwords = new WebMarkupContainer("passwords");
    passwords.add(password, confirmPassword);
    form.add(passwords);
    if (this.userId != 0) {
      passwords.setVisible(false);
    }
    form.add(new Button("save"));
    form.add(new Button("cancel") {
      @Override
      public void onSubmit() {
        setResponsePage(UserListPage.class);
      }
    }.setDefaultFormProcessing(false));
    List<Role> roleList = command.getRoles();
    CheckBoxMultipleChoice<Role> roles = new CheckBoxMultipleChoice<Role>(
        "profile.roles", roleList, new ChoiceRenderer<Role>("name", "id"));
    form.add(roles);
    form.add(new EqualPasswordInputValidator(password, confirmPassword));
    form.add(new FeedbackPanel("errors"));
    add(form);
  }

  /** Link to a user page.
   *
   * @param user The user that will be edited by this page. It can be null, in
   * which case the link will point to a page to create a new user.
   */
  public static class Link extends BookmarkablePageLink<UserPage> {

    /** Creates a link to an empty user page.
     *
     * @param id the wicket id, it cannot be null.
     */
    public Link(final String id) {
      super(id, UserPage.class);
    }

    /** Creates a link to a user page initialized with the provided user.
     *
     * @param id the wicket id, it cannot be null.
     *
     * @param user the user to edit with the user page. It cannot be null.
     */
    public Link(final String id, final User user) {
      super(id, UserPage.class);
      Validate.notNull(user, "The user cannot be null.");
      setParameter("id", user.getId());
    }
  }
}

