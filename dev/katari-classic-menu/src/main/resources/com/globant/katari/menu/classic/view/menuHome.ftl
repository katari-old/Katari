<#import "menuSupport.ftl" as menu />

<html>

  <head>
    <title>Menu</title>
  </head>

  <body>
    <ul id="menuHome">
      <#list menu.helper().getMenuNodesForPath(menu.current()) as menuitem>
        <li class="${menu.selected(menuitem.menuNode.path)}">
          <a href="${menu.buildPath(menuitem)}">
            ${menuitem.menuNode.displayName}</a>
        </li>
      </#list>
    </ul>
  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

