<#import "spring.ftl" as spring />

<html>

  <head>
    <title>View User</title>
  </head>

  <body>
    <h3>View User</h3>
    <form id="userView" name="userView" method="POST"
        action="userEdit.do?userId=${command.userId}">

      <div class="clearfix column left">

        <div class="column left">

          <span class="formfield">
            <label for="name">Name <span>${command.profile.name}</span></label>
          </span>

          <span class="formfield">
            <label for="email">Email <span>${command.profile.email}</span></label>
          </span>

        </div>

        <div class="column left">

          <span class="formfield">
            <label for="roles">Roles</label>
              <#list command.availableRoles?keys as roleId>
                <#assign isSelected = spring.contains(command.profile.roleIds, roleId)>
                <#if isSelected>${command.availableRoles[roleId]}</#if>
              </#list>
          </span>

        </div>

      </div>

      <div class="clear">

        <input class="btn rightMargin" type="submit" value="Edit" onclick="window.location=
          '${request.contextPath}/userEdit.do?userId=${command.userId}';return false;"/>
        <input class="btn rightMargin" type="submit" value="Change Password"
          onclick="window.location=
          '${request.contextPath}/changePassword.do?userId=${command.userId}';return false;"/>
        <input class="btn" type="submit" value="Cancel" onclick="window.location=
          '${request.contextPath}/users.do';return false;"/>

      </div>
    </form>
  </body>
</html>

<!-- vim: set ts=2 et sw=2 ai ft=xml: -->

