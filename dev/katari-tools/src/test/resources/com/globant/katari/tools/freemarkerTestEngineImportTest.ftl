<#import "spring.ftl" as spring />

<#import "testlib.ftl" as testlib/>

<html>

  <head>
    <title>Edit User</title>
  </head>

  <body>
    <@testlib.showSomething/>

    <h3>Edit User</h3>

    <form id="userEdit" name="userEdit" method="POST" action="action">

      <span class="error" id="message">
        <@spring.bind "command.*"/>
        <@spring.showErrors  "<br/>" />
      </span>

      <@spring.formHiddenInput "command.userId" />

      <span class="formfield">
        <label for="name">Name:</label>
        <@spring.formInput "command.profile.name" />
      </span>

      <span class="formfield">
        <label for="email">Email:</label>
        <@spring.formInput "command.profile.email" />
      </span>

      <input  type="submit" value="Save"/>
      <input type="submit" value="Cancel"/>
    </form>
  </body>
</html>

<!-- vim: set ts=2 et sw=2 ai ft=xml: -->

