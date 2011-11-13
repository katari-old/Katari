<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
  "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv='pragma' content='no-cache'>
    <meta http-equiv='cache-control' content='no-cache'>
    <meta http-equiv='expires' content='0'>
    <title>Error</title>

    <style type="text/css">
      body {
        font-family: arial, verdana, helvetica, lucida, sans-serif;
        font-size: 9pt;
        color: black;
        background-color: #fff;
        margin: 10px;
      }

      #stacktrace {
        color: red;
      }

      #norris {
        float: right;
        background-color: #000;
        border: 1px solid #000;
      }

      #norris h4 {
        color: white;        margin: 10px;
        font-size: 15pt;
        text-align: center;
      }
    </style>
  </head>
  <body>
    <h1>Error</h1>
    <#if debugMode>
       <div id="norris">
        <img src="http://www.dailyjist.com/wp-content/uploads/2010/12/chuck-norris-2.jpg" alt="chuck norris catch it!"/>
        <h4>I catch it for you!</h4>
      </div>
      <h2>${exception.message}</h2>
      <div id="stacktrace">
        <#list exception.stackTrace as stack>
          ${stack} <br />
        </#list>
      </div>
    </#if>
  </body>
</html>
