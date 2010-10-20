<#import "spring.ftl" as spring />

<html>

  <head>
    <title>Task Dashboard</title>

    <script type="text/javascript">
      var loader = new YAHOO.util.YUILoader({

        require: ["datatable"],

        // where on my local domain the library lives
        base: '${baseweb}/module/ajax/yui/',

        loadOptional: true,

        // The function to call when all script/css resources have been loaded
        onSuccess: function() {

          var dataSource = new YAHOO.util.DataSource("asyncTaskDashboard.do");
          dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
          dataSource.responseSchema = {
            fields: ["friendlyName", "progressPercent", "information",
            "nextExecutionTime", "lastExecutionTime"]
          };
          dataSource.doBeforeParseData = function (request, response) {
            return response;
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
            {
              key: "progressPercent",
              label: "%",
              formatter: percentageFormatter
            }, {
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

          var config = {
            dynamicData: true
          };

          var table = new YAHOO.widget.DataTable("task-list", columns,
            dataSource, config);

          // Sends a request to the DataSource for more data when the user
          // clicks the refresh button, or after a timeout.
          var refresh = document.getElementById("task-refresh");
          var refreshNow = function(e) {
            var callback = {
              success : table.onDataReturnSetRows,
              failure : table.onDataReturnSetRows,
              scope : table,
              argument: table.getState()
            };
            dataSource.sendRequest("", callback);
          }
          YAHOO.lang.later(10000, null, refreshNow, null, true);
          YAHOO.util.Event.addListener(refresh, "click", refreshNow);
        }
      });
      loader.insert();
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

