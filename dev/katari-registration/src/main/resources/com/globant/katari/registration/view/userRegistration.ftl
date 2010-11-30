<#import "spring.ftl" as spring />
<html>
  <head>
    <title>User Registration</title>
  </head>
  <body>
    <div>
      <span class="error" id="message">
        <@spring.bind "command.*"/>
        <@spring.showErrors  "<br/>" />
      </span>
    </div>
    <div>
      <form id="registrationForm" name="registrationForm" action="" method="post">
        <span class="formfield">
          <label for="name">Name</label>
          <@spring.formInput "command.name" />
        </span>
        <span class="formfield">
          <label for="email">Email</label>
          <@spring.formInput "command.email" />
        </span>
        <div class="buttons clear">
          <input class="btn rightMargin" type="submit" value="Create account"/>
        </div>
      </form>
    </div>
  </body>
 <!-- vim: set ts=2 et sw=2 ai filetype=xml: -->
</html>
