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

    <#if result?exists>
      <div>
        An email was sent to your inbox: ${result.email}
      </div>
    <#else>
      <div>
        Oups... error, please start the recover workflow again please.
      </div>
    </#if>

  </body>
 <!-- vim: set ts=2 et sw=2 ai filetype=xml: -->
</html>
