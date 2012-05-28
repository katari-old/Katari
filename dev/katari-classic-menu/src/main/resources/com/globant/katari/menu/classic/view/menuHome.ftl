<#import "menuSupport.ftl" as menu />

<html>

  <head>
    <title>Menu</title>
  </head>

  <body>
    <ul id="menuHome">
      <#list menu.helper.getMenuNodesForCurrentPath() as menuitem>
        <li class="${menu.selected(menuitem)}">
          <a href="${menu.buildPath(menuitem)}">
            ${menuitem.menuNode.displayName}</a>
        </li>
      </#list>
    </ul>
  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

