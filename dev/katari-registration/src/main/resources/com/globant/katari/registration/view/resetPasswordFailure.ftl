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
      Start the recover workflow again.
    </div>

  </body>
 <!-- vim: set ts=2 et sw=2 ai filetype=xml: -->
</html>
