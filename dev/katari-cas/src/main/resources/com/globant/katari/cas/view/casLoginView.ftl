<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
  "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<#import "spring.ftl" as spring />

<#macro urlencode path="">
  <#if path != "">${response.encodeRedirectURL("?"+path)}</#if>
</#macro>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <title>Login</title>
    <meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8">
  </head>

  <body>

    <h3>Login</h3>
    <h4>Sign in with your account</h4>

    <form id='login' name='login' method="post"
        action="login<@urlencode path=request.getQueryString()/>">

      <span class="error" id="message">

        <#-- There is something bad going on here: this should be spring.errors
        instead of .vars["org ..... BindException.credentials"]. Anyway, it
        works this way ...
        -->
        <#list
            .vars["org.springframework.validation.BindException.credentials"].allErrors
            as error>
          <@spring.message code="${error.code}"/><br/>
        </#list>
      </span>

      <span class="formfield">
        <label for="username">Username</label>
        <input type='text' class="text" id="user" name="username"
            size="32" tabindex="1" accesskey
            ='<@spring.message code="screen.welcome.label.netid.accesskey"/>'/>
      </span>

      <span class="formfield">
        <label for="password">Password</label>

        <#--
        NOTE: Certain browsers will offer the option of caching passwords for a
        user. There is a non-standard attribute, "autocomplete" that when set
        to "off" will tell certain browsers not to prompt to cache credentials.
        For more information, see the following web page:
        http://www.geocities.com/technofundo/tech/web/ie_autocomplete.html
        -->
        <input type="password" class="text" id="password" name="password"
            size="32" tabindex="2" accesskey
            ='<@spring.message "screen.welcome.label.password.accesskey"/>'/>
      </span>

      <input type="hidden" name="lt" value="${flowExecutionKey}" />
      <input type="hidden" name="_eventId" value="submit" />

      <span class="buttons">
        <input name="loginButton" type="submit" class="submit button" 
            accesskey="l" value='Login' tabindex="4" />            
        <input type="reset" class="submit button" accesskey="c" value='Clear'
            tabindex="5" />
      </span>
    </form>
    <!-- vim: set ts=2 sw=2 et ai: -->
  </body>
</html>

