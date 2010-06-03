<#import "katari.ftl" as katari />

<html>

  <body>
    <!-- Hack to avoid the empty div validation error. -->
    <@katari.secureUrlArea url="${baseweb}/module/editable-pages/edit"; url>

      <!-- Switches between the published content and
        the unpublished content. -->
      <script type='text/javascript'>

        var changecss = function(theClass, element, value) {

          var cssRules;

          var added = false;
          for (var i = 0; i < document.styleSheets.length; i++) {

            var stylesheet = document.styleSheets[i];

            cssRules = '';

            if (stylesheet['rules']) {
              cssRules = 'rules';
            } else if (stylesheet['cssRules']) {
              cssRules = 'cssRules';
            } else {
              // alert ("UNK: " + i + "|" + stylesheet.href);
            }

            /* NOTE: When somehow we where hitting
              http://yui.yahooapis.com/2.8.0r4/build/calendar/assets/
                skins/sam/calendar.css,
              the returned object did not have cssRules nor rules in chrome.
              So we skip that stylesheet in that case.
             */
            if (cssRules != '') {
              for (var j = 0; j < stylesheet[cssRules].length; j ++) {
                if (stylesheet[cssRules][j].selectorText == theClass) {
                  if (stylesheet[cssRules][j].style[element]) {
                    stylesheet[cssRules][j].style[element] = value;
                    added=true;
                    break;
                  }
                }
              }
              if (!added){
                if (stylesheet.insertRule) {
                  stylesheet.insertRule(theClass + ' { ' + element
                      + ': '+value+'; }', stylesheet[cssRules].length);
                } else if (stylesheet.addRule) {
                  stylesheet.addRule(theClass, element + ': ' + value + ';');
                }
              }
            }
          }
        }

        // Hides all the published contents and shows all the unpublished
        // contents.
        function preview() {
        	changecss('.pending-publication', 'display', 'block');
        	changecss('.published', 'display', 'none');

        	changecss('div.preview-button a.pending-publication', 'display', 'block');
        	changecss('div.preview-button a.published', 'display', 'none');
        }

        // Hides all the unpublished contents and shows all the published
        // contents.
        function back() {
        	changecss('.pending-publication', 'display', 'none');
        	changecss('.published', 'display', 'block');

        	changecss('div.preview-button a.pending-publication', 'display', 'none');
        	changecss('div.preview-button a.published', 'display', 'block');
        }

      </script>

      <div class='preview-button'>
        <a class='published' onclick="javascript:preview()"><span>P</span></a>
        <a class='pending-publication' onclick="javascript:back()">
          <span>O</span>
        </a>
      </div>

    </@katari.secureUrlArea>

  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=html: -->

