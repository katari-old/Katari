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

          var dataSource = new YAHOO.util.DataSource("getTasks.do");
          dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
          dataSource.responseSchema = {
            fields: ["groupName", "jobName", "friendlyName", "isRunning",
            "progressPercent", "information", "nextExecutionTime",
            "lastExecutionTime"]
          };
          dataSource.doBeforeParseData = function (request, response) {
            return response;
          };

          /** Formats the information column.
          */
          var informationFormatter = function(cell, row, column, data) {
            var values = row.getData(column.key);
            var content = [];
            for(x in values) {
              if (content.length != 0) {
                content.push(', ');
              }
              content.push(x);
              content.push(": ");
              content.push(values[x]);
            }
            cell.innerHTML = content.join("");
          };

          /** Formats the progressPercent column.
          */
          var percentageFormatter = function(cell, row, column, data) {
            var value = row.getData(column.key);
            var isRunning = row.getData('isRunning');
            var content = [];
            content.push("<div style='width:200px; border: 1px solid #999'>");
            if (value) {
            content.push("<div style='background-color:#AFF584; width:" +
                value + "%; text-align:right'>");
              content.push(value + "%");
              content.push("</div>");
            } else if (isRunning) {
              content.push("Running");
            } else {
              content.push("Task not running");
            }
            content.push("</div>");
            cell.innerHTML = content.join("");
          };

          /** Formats the date columns (very hacky).
          *
          * Returns "" for a null date.
          */
          var dateFormatter = function(cell, row, column, data) {
            var value = row.getData(column.key);
            if (value == null) {
              return "";
            }

            var struct = /(\d{4})-?(\d{2})-?(\d{2})(?:[T ](\d{2}):?(\d{2}):?(\d{2})(?:\.(\d{3,}))?(?:(Z)|([+\-])(\d{2})(?::?(\d{2}))?))/.exec(value);

            timestamp = Date.UTC(+struct[1], +struct[2] - 1, +struct[3],
              +struct[4], +struct[5], +struct[6]);

            cell.innerHTML = YAHOO.util.Date.format(new Date(timestamp),
              {format: "%Y-%m-%d %T"});
          }

          /** Shows the 'run now' button.
          */
          var showRunNowButton = function(cell, row, column, data) {
            var isRunning = row.getData('isRunning');
            var disabled = '';
            if (isRunning) {
              disabled = "disabled='disabled'";
            }
            var content = "<input type='button' " + disabled
              + " class='btn' value='Run Now'>";
            cell.innerHTML = content;
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
            }, {
              key: "runNow",
              label: '',
              formatter: showRunNowButton
            }
          ];

          var config = {
            dynamicData: true
          };

          var table = new YAHOO.widget.DataTable("task-list", columns,
            dataSource, config);

          // Listens for the click event in the 'runNow' cell.
          table.subscribe('cellClickEvent', function(ev) {
            var target = YAHOO.util.Event.getTarget(ev);
            var column = table.getColumn(target);
            var isRunning = table.getRecord(target).getData('isRunning');
            if (column.key == 'runNow' && !isRunning) {
              if (confirm('Are you sure?')) {
                var groupName = table.getRecord(target).getData('groupName');
                var jobName = table.getRecord(target).getData('jobName');
                var params = [];
                params.push('groupName=' + encodeURIComponent(groupName));
                params.push('jobName=' + encodeURIComponent(jobName));
                var url = 'runTask.do?' + params.join('&');
                table.getRecord(target).setData('isRunning', true);
                var callback = {
                  success: function(o) {
                    refreshNow();
                  },
                  failure: function(o) {
                  },
                };
                YAHOO.util.Connect.asyncRequest('GET', url, callback);
              }
            } else {
              table.onEventShowCellEditor(ev);
            }
          });

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
    <div id="task-list" class='yui-dt' style="margin-top:10px;">
      <!-- Table goes here -->
    </div>
    <input id='task-refresh' type="submit" value="Refresh Now" class="btn"
      style="margin-top:10px;"/>
  </body>

 <!-- vim: set ts=2 et sw=2 ai filetype=xml: -->
</html>

