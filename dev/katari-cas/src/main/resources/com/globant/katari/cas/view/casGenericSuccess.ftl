<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
  "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<#import "spring.ftl" as spring />

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <title>Login</title>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
    <meta name="keywords" content="Central Authentication Service,JA-SIG,CAS" />
    <link rel="stylesheet" href="css/home.css" type="text/css" media="all" />
    <link rel="stylesheet" href="css/jasig.css" type="text/css" media="all" />
  </head>
  <body>

    <div id="welcome">
      <h2><@spring.message code="screen.success.header"/></h2>
      <p><@spring.message code="screen.success.success"/></p>
      <p><@spring.message code="screen.success.security"/></p>
    </div>

  <!-- vim: set ts=2 sw=2 et ai: -->
  </body>
</html>

