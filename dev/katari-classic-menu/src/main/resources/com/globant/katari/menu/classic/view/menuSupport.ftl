<#-- Obtains the MenuDisplayHelper object. -->
<#function helper>
  <#return request.getAttribute("::menu-display-helper")/>
</#function>

<#-- Obtains the current selected menu path (a string). -->
<#function current>
  <#return request.getSession().getAttribute("::selected-module-entry")/>
</#function>

<#-- Obtains the current menu level (an integer). -->
<#function level>
  <#return request.getAttribute('instance')?number/>
</#function>

<#--
Determines the style of a menu item. This function selects between two
styles, 'selected' and 'noselected', based on the content of a request
attribute '::selected-module-entry'. If a leaf is selected the parent
containers will also be selected

menuitempath : the path of menuitem to be evaluated.
-->
<#function selected menuitempath>
  <#if current()??>
    <#if current()?starts_with(menuitempath) >
      <#return "selected"/>
    </#if>
  </#if>
  <#return "noselected"/>
</#function>

<#--
Builds the link url of the menu, adding the the request parameter
'selected-module-entry' to the linkpath. The selected module entry will be
used to mark the menu as selected.
-->
<#function buildPath menuItem>

  <#assign path = menuItem.path/>
  <#assign linkPath = baseweb + menuItem.linkPath/>

  <#if linkPath?contains("?")>
    <#assign linkPath = linkPath + "&selected-module-entry=" + path/>
  <#else>
    <#assign linkPath = linkPath + "?selected-module-entry=" + path/>
  </#if>

  <#return linkPath/>
</#function>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

