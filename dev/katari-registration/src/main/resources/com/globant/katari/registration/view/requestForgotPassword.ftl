<#import "spring.ftl" as spring />
<html>
  <head>
    <title>User Forgot Password</title>
  </head>
  <body>
    <div>
      <span class="error" id="message">
        <@spring.bind "command.*"/>
        <@spring.showErrors  "<br/>" />
      </span>
    </div>
    <div>
      <form id="forgotPasswordForm" name="forgotPasswordForm" action="" method="post">
        <span class="formfield">
          <label for="email">Email</label>
          <@spring.formInput "command.email" />
        </span>
        <div class="buttons clear">
          <input class="btn rightMargin" type="submit" value="Retrieve Password"/>
        </div>
      </form>
    </div>
  </body>
 <!-- vim: set ts=2 et sw=2 ai filetype=xml: -->
</html>
