<#--
A library of katari convenient macros.
-->

<#--
Renders a weblet.
-->
<#macro weblet moduleName webletName instance = '' >
  ${request.getAttribute("::weblet-renderer").renderWebletResponse(
    moduleName, webletName, instance, request, response)}
</#macro>

<#--
Renders a weblet inside a div decorator.

The div has a class named after the weblet:
  'weblet weblet_[modulename]_[webletname]_[instance]'.
-->
<#macro decoratedWeblet moduleName webletName instance = '' >
  <#if !instance?is_hash && instance != '' >
    <#assign className='weblet weblet_' + moduleName + '_' + webletName + '_' +
      instance>
  <#else>
    <#assign className='weblet weblet_' + moduleName + '_' + webletName>
  </#if>
  <div class='${className}'>
    ${request.getAttribute("::weblet-renderer").renderWebletResponse(
      moduleName, webletName, instance, request, response)}
  </div>
</#macro>

<#-- Katari secure url macro. This macro shows the body only if the user has
access to the specified url. There is a debug mode, enabled when the
application is running against a development database and the users sends a
securityDebug=true parameter. -->
<#macro secureUrlArea url else=''>
  <#assign urlHelper = request.getAttribute("secureUrlHelper")>
  <#assign session = request.getSession(false)??>
  <#if !session??>
    <#assign testMode = session.getAttribute("securityDebug")??>
  <#else>
    <#assign testMode = false>
  </#if>
  <#assign canAccess = urlHelper.canAccessUrl(request.getRequestURI(), url)>
  <#if !testMode>
    <#if canAccess>
      <#nested url>
    <#else>
      ${else}
    </#if>
  <#else>
    <#if canAccess>
      <#nested url>
    <#else>
      ${else}
      <div style="color:red"><b>debug</b><br>
        <#nested url>
      </div>
    </#if>
  </#if>
</#macro>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

