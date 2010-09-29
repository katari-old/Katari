<#import "spring.ftl" as spring />

<html>

  <head>
    <title>Edit User</title>
  </head>

  <body>
    <h3>Edit User</h3>
    <#if (command.password)?? >
      <#assign action='createUser.do'>
    <#else>
      <#assign action='userEdit.do'>
    </#if>

    <form id="userEdit" name="userEdit" method="POST" action="${action}">

      <span class="error" id="message">
        <@spring.bind "command.*"/>
        <@spring.showErrors  "<br/>" />
      </span>

      <div class="clearfix column left">

        <div class="column left">

          <@spring.formHiddenInput "command.userId" />

          <span class="formfield">
            <label for="name">Name</label>
            <@spring.formInput "command.profile.name" />
          </span>

          <span class="formfield">
            <label for="email">Email</label>
            <@spring.formInput "command.profile.email" />
          </span>

          <#if (command.password)?? >
            <span class="formfield">
              <label for="password">New Password</label>
              <@spring.formPasswordInput "command.password.newPassword" />
            </span>

            <span class="formfield">
              <label for="passwordConfirm">Confirm New Password</label>
              <@spring.formPasswordInput "command.password.confirmedPassword" />
            </span>
          </#if>

        </div>

        <div class="column left">

          <div class="formfield">
            <label for="roles">Roles</label>
            <div>
              <@spring.formCheckboxes "command.profile.roleIds",
                command.availableRoles, "</div><div>",
                "class='check left rightMargin'"/>
            </div>
          </div>

        </div>

      </div>

      <div class="buttons clear">
        <input class="btn rightMargin" type="submit" value="Save"/>
        <input class="btn" type="submit" value="Cancel" onclick="window.location=
          '${request.contextPath}/users.do';return false;"/>
      </div>

    </form>
  </body>
</html>

<!-- vim: set ts=2 et sw=2 ai ft=xml: -->

