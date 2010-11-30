<#import "spring.ftl" as spring />
<html>
  <head></head>
  <body>
    <#if user?exists>
      <div>
        Cannot perform the activation operation, please click here to start again the workflow.
      </div>
    <#else>
      <div>
        A password was sent to your email.
      </div>
    </#if>
  </body>
</html>