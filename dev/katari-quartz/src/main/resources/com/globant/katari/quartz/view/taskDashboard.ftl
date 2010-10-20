<#import "spring.ftl" as spring />

<html>

  <head>
    <title>Task Dashboard</title>

    <link rel="stylesheet" type="text/css"
      href="http://yui.yahooapis.com/2.8.1/build/fonts/fonts-min.css" />
    <link rel="stylesheet" type="text/css"
      href="http://yui.yahooapis.com/2.8.1/build/datatable/assets/skins/sam/datatable.css"
      />

    <script type="text/javascript"
      src="http://yui.yahooapis.com/2.8.1/build/yahoo-dom-event/yahoo-dom-event.js">
    </script>
    <script type="text/javascript"
      src="http://yui.yahooapis.com/2.8.1/build/json/json-min.js">
    </script>
    <script type="text/javascript"
      src="http://yui.yahooapis.com/2.8.1/build/element/element-min.js">
    </script>
    <script type="text/javascript"
      src="http://yui.yahooapis.com/2.8.1/build/datasource/datasource-min.js">
    </script>
    <script type="text/javascript"
      src="http://yui.yahooapis.com/2.8.1/build/connection/connection-min.js">
    </script>
    <script type="text/javascript"
      src="http://yui.yahooapis.com/2.8.1/build/datatable/datatable-min.js">
    </script>

    <script type="text/javascript">
      YAHOO.util.Event.onDOMReady(function() {

        var DS = new YAHOO.util.DataSource("asyncTaskDashboard.do");
        DS.responseType = YAHOO.util.DataSource.JSON_ARRAY;
        DS.responseSchema = {
          fields: ["friendlyName", "progressPercent", "information", "endTime",
          "finalFireTime", "nextExecutionTime", "lastExecutionTime"]
        };
        DS.doBeforeParseData = function (request, response) {
          return eval(response.responseText);
        };

        /** Formats the information column.
        */
        var informationFormatter = function(cell, row, column, data) {
          var values = row.getData(column.key);
          var tmp = [];
          for(x in values) {
            if (tmp.length != 0) {
              tmp.push(', ');
            }
            tmp.push(x);
            tmp.push(": ");
            tmp.push(values[x]);
          }
          cell.innerHTML = tmp.join("");
        };

        /** Formats the progressPercent column.
        */
        var percentageFormatter = function(cell, row, column, data) {
          var value = row.getData(column.key);
          var tmp = [];
          tmp.push("<div style='width:200px; border: 1px solid #999'>");
          if (value) {
            tmp.push("<div style='background-color:#AFF584; width:" + value +
                "%; text-align:right'>");
            tmp.push(value + "%");
            tmp.push("</div>");
          } else {
            tmp.push("Unknown");
          }
          tmp.push("</div>");
          cell.innerHTML = tmp.join("");
        };

        /** Formats the date columns (very hacky).
        */
        var dateFormatter = function(cell, row, column, data) {
          var value = row.getData(column.key);

          var struct = /(\d{4})-?(\d{2})-?(\d{2})(?:[T ](\d{2}):?(\d{2}):?(\d{2})(?:\.(\d{3,}))?(?:(Z)|([+\-])(\d{2})(?::?(\d{2}))?))/.exec(value);

          timestamp = Date.UTC(+struct[1], +struct[2] - 1, +struct[3],
            +struct[4], +struct[5], +struct[6]);

          cell.innerHTML = YAHOO.util.Date.format(new Date(timestamp),
            {format: "%Y-%m-%d %T"});
        }

        var columns = [
          {key: "friendlyName", label: "Name"},
          {key: "progressPercent", label: "%", formatter: percentageFormatter},
          {
            key: "lastExecutionTime",
            label: "Last run on",
            formatter: dateFormatter
          }, {
            key: "nextExecutionTime",
            label: "Will run on",
            formatter: dateFormatter
          }, {
            key: "information",
            label: "Information",
            formatter: informationFormatter
          }
        ];

        var oConfigs = {
          dynamicData: true
        };

        var table = new YAHOO.widget.DataTable("task-list", columns, DS,
          oConfigs);

        var refresh = document.getElementById("task-refresh");
        var refreshNow = function(e) {
          // Sends a request to the DataSource for more data
          var oCallback = {
            success : table.onDataReturnSetRows,
            failure : table.onDataReturnSetRows,
            scope : table,
            argument: table.getState()
          };
          DS.sendRequest("", oCallback);
        }

        YAHOO.lang.later(10000, null, refreshNow, null, true);
        YAHOO.util.Event.addListener(refresh, "click", refreshNow);
      });
    </script>
  </head>

  <body>
    <div id="task-list" style="margin-top:10px;">
      <!-- table here -->
    </div>
    <input id='task-refresh' type="submit" value="Refresh Now" class="btn"
      style="margin-top:10px;"/>
  </body>

 <!-- vim: set ts=2 et sw=2 ai filetype=xml: -->
</html>

