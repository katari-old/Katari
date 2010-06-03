<#import "menuSupport.ftl" as menu />

<html>

  <head>
    <title>Menu</title>
  </head>

  <body>
    <!-- This comment is a hack to avoid the "WARNING: trimming empty <div>"
      htmltidy error. -->
    <#list menu.helper().getMenuNodesForLevel(menu.current(), menu.level())
          as menuitem>
      <#if menuitem_index == 0>
        <ul id="menu_level_${menu.level()}" class="clearfix">
      </#if>
        <li class="${menu.selected(menuitem.menuNode.path)}">
          <a href="${menu.buildPath(menuitem)}">
            ${menuitem.menuNode.displayName}</a>
        </li>
      <#if ! menuitem_has_next>
        </ul>
      </#if>
    </#list>
  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

