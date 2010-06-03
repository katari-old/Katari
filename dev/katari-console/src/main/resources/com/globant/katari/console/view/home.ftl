<html>
  <head>
    <title>Katari web console</title>
        
    <script src="asset/js/client/codemirror.js" type="text/javascript"></script>
    <script src="asset/js/client/mirrorframe.js" type="text/javascript"></script>
    <script src="asset/js/client/jquery-1.3.2.min.js" type="text/javascript"></script>
    <script src="asset/js/client/jquery-ui-1.7.2.custom.min.js"
      type="text/javascript"></script>
        
    <link rel="stylesheet" type="text/css"
      href="asset/style/theme/redmond/jquery-ui-1.7.1.custom.css"/>
    <link rel="stylesheet" type="text/css" href="asset/style/main.css"/>
  </head>
  <body>
    <script src="asset/js/client/main.js" type="text/javascript"></script>      
 
    <h3>
      Katari web console
    </h3>
 
    <form id="publishform" action="/publish.groovy" method="POST">
      <div id="textarea-container" class="border">
        <textarea id="script" name="script" cols="140" rows="40"></textarea>
      </div>

      <div id="button-bar">
        <div id="actionsBreadcrumb">
          <span class="actionsBreadcrumbHead">Actions &nbsp;&#x27A4;</span>
          <span class="actionsBreadcrumbChild" id="executeButton"><a href="javascript:void(0)">Execute script</a></span>
          <span class="actionsBreadcrumbLastChild" id="newScriptButton"><a href="home.do">New script</a></span>
        </div>
      </div>
    </form>

    <div id="tabs">
      <ul>
        <li><a href="#tabs-output">Output</a></li>
        <li><a href="#tabs-error">Error</a></li>
      </ul>

      <div id="tabs-output">
        <pre id="output" class="border hidden"></pre>
      </div>

      <div id="tabs-error">
        <pre id="error" class="border hidden"></pre>
      </div>
    </div>

    <script type="text/javascript">
      var editor = CodeMirror.fromTextArea('script', {
        height: "300px",
        parserfile: ["tokenizejavascript.js", "parsejavascript.js"],
        stylesheet: "asset/style/jscolors.css",
        path: "asset/js/client/",
        continuousScanning: 500,
        lineNumbers: true,
        textWrapping: false,
        tabMode: "spaces",
        submitFunction: function() {
          $("#executeButton").click();
        }
      });
    </script>
                
  </body>
</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

