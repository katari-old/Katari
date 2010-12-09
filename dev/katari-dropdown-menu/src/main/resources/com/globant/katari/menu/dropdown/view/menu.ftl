<html>

  <head>
    <title>Menu</title>
  </head>

  <body>
    <!-- menuBar or contextMenu -->
    <#assign menuType = request.getAttribute('instance')/>
    <#assign rootMenu = request.getAttribute(
      'com.globant.katari.menu.dropdown.tree')/>

    <script type='text/javascript'>
      var loader = new YAHOO.util.YUILoader({
        base: '${baseweb}/module/ajax/yui/',
        // Configure loader to pull in optional dependencies.
        loadOptional: true,
        // Called when all script/css resources have been loaded.
        onSuccess: function() {
          var oMenu = new YAHOO.widget.MenuBar("dropdownMenu-${menuType}");
          oMenu.render();
          oMenu.show();
        }
      });
      loader.addModule({
          name: 'katari_yui_patchedoverlay',
          type: 'js',
          varName: "Prototype.getConstrainedY",
          path: 'patches/overlay_patch.js',
      });
      loader.addModule({
          name: 'katari_yui_patchedmenu',
          type: 'js',
          varName: "YAHOO.widget.Menu.prototype.getConstrainedY",
          path: 'patches/menu_patch.js',
          requires: ['menu','katari_yui_patchedoverlay']
      });
      loader.require("katari_yui_patchedmenu");
      loader.insert();
    </script>

    <#macro displayChildNodes parentNode classprefix='yuimenubar'>
      <#if parentNode.children?size != 0>
        <ul>
          <#list parentNode.children as item>
            <#if item.menuNode.isLeaf()>
              <li class='${classprefix}item'>
                <a class="${classprefix}itemlabel"
                    href='${baseweb}${item.menuNode.linkPath}'>
                  ${item.menuNode.displayName}
                </a>
              </li>
            <#else>
              <li class='${classprefix}item'>
                <a class="${classprefix}itemlabel" href="#">
                  ${item.menuNode.displayName}
                </a>
                <div class="${classprefix}">
                  <div class="bd">
                    <@displayChildNodes item 'yuimenu'/>
                  </div>
                </div>
              </li>
            </#if>
          </#list>
        </ul>
      </#if>
    </#macro>

    <div id='dropdownMenu-${menuType}-container'>
      <#if menuType == 'context'>
        <div id="dropdownMenu-${menuType}" class="yuimenubar"
          style='visibility:hidden'>
          <div class="bd">
            <!-- This comment is a hack to avoid the "WARNING: trimming empty
                 <div>" htmltidy error when there are no menus to display. -->
            <ul>
              <li class='yuimenubaritem'>
                <a class="yuimenubaritemlabel" href='#'> Menu </a>
                <div class='yuimenu'>
                  <div class="bd">
                    <@displayChildNodes rootMenu 'yuimenu'/>
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>
      <#elseif menuType == 'bar'>
        <div id="dropdownMenu-${menuType}" class="yuimenubar yuimenubarnav"
          style='display:none'>
          <div class="bd">
            <@displayChildNodes rootMenu />
          </div>
        </div>
      <#else>
        <!-- Hack to throw an error if the menu type is unknown. -->
        ${nonexistingMenuType}
      </#if>
    </div>

  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

