<#import "katari.ftl" as katari />

<html>

  <head>
    <title>${page.title}</title>
    <meta name="pageName" content="${page.name}">
    <meta name="moduleName" content="editable-pages">
  </head>

  <body>
    <@katari.secureUrlArea url="${baseweb}/module/editable-pages/edit"; url>
      <script type='text/javascript'>
        var confirmRevert = function(url) {
          if (confirm("Are you sure you want to revert your changes?")) {
            location.href=url;
          }
        }

        /* Switches between published and unpublished content. */
        var switchPreview = function(eventName, args, menuItem) {
          var pending = document.getElementById('pending-publication-' + this.pageid);
          var published = document.getElementById('published-' + this.pageid);
          var pencil = document.getElementById('editable-pages-pencil' + this.pageid);
          if (pending.style.display != 'block') {
            pencil.className = 'preview';
            pending.style.display = 'block';
            published.style.display = 'none';
            menuItem.cfg.setProperty("checked", true);
          } else {
            pencil.className = '';
            pending.style.display = '';
            published.style.display = '';
            menuItem.cfg.setProperty("checked", false);
          }
        }

        // Instantiate and configure Loader:
        var editablePagesMenu${elementId};
        var loader = new YAHOO.util.YUILoader({

          require: ["menu"],
          base: '${baseweb}/module/ajax/yui/',
          // Configure loader to pull in optional dependencies.
          loadOptional: true,
          // Called when all script/css resources have been loaded.
          onSuccess: function() {

            var previewMenuItem = new YAHOO.widget.MenuItem("Preview",
              { text: "Preview", className: "previewPage",
              url: '#',
              onclick: {fn:switchPreview}
              });
            previewMenuItem.pageid = ${elementId}

            // Add the menu to the editable content
            editablePagesMenu${elementId} = new YAHOO.widget.Menu(
              'editable-content.menu-${elementId}',
              {visible: false, position: 'dynamic', constraintoviewport:true}
              );
            editablePagesMenu${elementId}.addItems([
              <@katari.secureUrlArea
                  url="${baseweb}/module/editable-pages/edit/create.do"; url>
                // disabled { text: "New", url: "${url}" ,
                //   className: "addPage" },
              </@katari.secureUrlArea>
              <@katari.secureUrlArea
                  url="${baseweb}/module/editable-pages/edit/edit.do"; url>
                { text: "Edit", url: "${url}?id=${page.id}",
                  className: "editPage" },
              </@katari.secureUrlArea>
              <@katari.secureUrlArea
                  url="${baseweb}/module/editable-pages/edit/remove.do"; url>
                // disabled { text: "Remove", url: "${url}?id=${page.id}",
                //  className: "removePage" },
              </@katari.secureUrlArea>
              <#if page.dirty>
                <@katari.secureUrlArea
                    url="${baseweb}/module/editable-pages/edit"; url>
                  previewMenuItem,
                </@katari.secureUrlArea>
                <@katari.secureUrlArea
                    url="${baseweb}/module/editable-pages/edit/publish.do"; url>
                  { text: "Publish", url: "${url}?id=${page.id}",
                    className: "publishPage" },
                </@katari.secureUrlArea>
                <@katari.secureUrlArea
                    url="${baseweb}/module/editable-pages/edit/revert.do"; url>
                  { text: "Revert",
                    url: "javascript:confirmRevert('${url}?id=${page.id}')",
                    className: "revertPage" },
                </@katari.secureUrlArea>
              </#if>
              ]);

            function positionMenu() {
              editablePagesMenu${elementId}.align("bl", "tl");
              editablePagesMenu${elementId}.cfg.setProperty("context",
                  ["editable-pages-pencil${elementId}", "bl", "tl"]);
            }

            editablePagesMenu${elementId}.subscribe("beforeShow", function () {
              if (this.getRoot() == this) {
                positionMenu();
              }
            });

            function onYahooClick(p_oEvent) {
              // Position and display the menu
              positionMenu();
              editablePagesMenu${elementId}.show();
            }

            editablePagesMenu${elementId}.render(
                document.getElementById("editable-pages-pencil${elementId}"));

            /* Assign a "click" event handler to the "Yahoo!" anchor that will
             * display the menu
             */
            YAHOO.util.Event.addListener("editable-pages-pencil${elementId}",
                "click", onYahooClick);
          }
        })
        loader.insert();
      </script>

    </@katari.secureUrlArea>

    <div class="pageContent">

      <@katari.secureUrlArea url="${baseweb}/module/editable-pages/edit"; url>
        <div id='editable-pages-pencil${elementId}'>
          <div class='editable-pages-pencil'><span>E</span></div>
        </div>
      </@katari.secureUrlArea>

      <#if page.dirty>
        <@katari.secureUrlArea
          url="${baseweb}/module/editable-pages/edit"
          else="<div>${page.content}</div>">
          <div class='pending-publish-content'>PENDING PUBLICATION</div>
          <div id='pending-publication-${elementId}'
              class='pending-publication'>
            ${page.unpublishedContent}
          </div>
          <div id='published-${elementId}' class='published'>
            ${page.content}
          </div>
        </@katari.secureUrlArea>
      <#else>
        <div>${page.content}</div>
      </#if>
    </div>

  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=html: -->

