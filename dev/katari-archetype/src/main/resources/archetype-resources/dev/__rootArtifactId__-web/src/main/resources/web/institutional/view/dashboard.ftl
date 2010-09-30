<#import "spring.ftl" as spring />
<html>

  <head>
    <title>Dashboard</title>
    <script type='text/javascript'
      src='${baseweb}/module/gadgetcontainer/assets/js/jquery-1.4.2.js'>
    </script>

    <script type='text/javascript'
      src='${baseweb}/module/gadgetcontainer/assets/js/jquery-ui-1.8.4.custom.min.js'>
    </script>

    <script type='text/javascript'
      src='${baseweb}/module/shindig/gadgets/js/rpc.js'>
    </script>

    <script type='text/javascript'
      src='${baseweb}/module/gadgetcontainer/katariSocialCanvas.js'>
    </script>

  </head>

  <body>

    <div id='top-gadgets'>
      <!-- will hold open social gadgets. -->
    </div>

    <div id='custom-gadgets'>
      <div class='gadgetContainerTools'>
        <a href='${baseweb}/module/gadgetcontainer/directory.do?returnUrl=/module/institutional/dashboard.do&amp;gadgetGroupName=main'>Add ...</a>
      </div>
      <!-- will hold open social gadgets. -->
    </div>

    <script type='text/javascript'>
      $(document).ready(function() {
        var topGadgets = new katari.social.GadgetGroup('top-gadgets');

        $.getJSON(
          katari.social.canvasConfig.host +
            '${baseweb}/module/gadgetcontainer/socialPage.do?groupName=top', 
          function(data) {
            topGadgets.addGadgetsFromJson(data);
            topGadgets.render();
          });

        var mainGadgets = new katari.social.GadgetGroup('custom-gadgets');
        $.getJSON(
          katari.social.canvasConfig.host +
            '${baseweb}/module/gadgetcontainer/socialPage.do?groupName=main', 
          function(data) {
            mainGadgets.addGadgetsFromJson(data);
            mainGadgets.render();
          });
      });
    </script>

  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

