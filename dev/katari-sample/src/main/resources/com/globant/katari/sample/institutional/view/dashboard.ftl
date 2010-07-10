<#import "spring.ftl" as spring />
<html>

  <head>
    <title>Dashboard</title>
    <script type='text/javascript'
      src='${baseweb}/module/gadgetcontainer/assets/js/jquery-1.4.2.js'>
    </script>

    <script type='text/javascript'
      src='${baseweb}/module/shindig/gadgets/js/rpc.js'>
    </script>

    <script type='text/javascript'
      src='${baseweb}/module/gadgetcontainer/katariSocialCanvas.js'>
    </script>

     <style type="text/css">
      
      .canvasContainer {
        display: inline;
      }
      
      .gadgetContainer iframe {
        border: 0px;
      }
      
      .canvasColumn {
        float: left;
        width: 30%;
        margin: 10px;
        padding-top: 10px;
      }
      
      #canvasDiv {
        width: 100%;
        height: 400px;
        border: 1px solid #000;
        text-align: center;
      }
      
    </style>
  </head>

  <body>

    <div id='gadgets-div'>
      <!-- will hold open social gadgets. -->
    </div>

    <script type='text/javascript'>
      $(document).ready(function() {
        gadgetGroup = new KATARI.SOCIAL.Canvas('gadgets-div', 2);

        $.getJSON(
          KATARI.SOCIAL.canvasConfig.host +
            '/module/gadgetcontainer/socialPage.do?groupName=main', 
          function(data) {
            gadgetGroup.addGadgetsFromJson(data);
            gadgetGroup.render();
          });
      });
    </script>

  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

