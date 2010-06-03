<#import "spring.ftl" as spring />
<html>

  <head>
    <title>My Time</title>

    <script type='text/javascript'>
      // Instantiate and configure Loader:
      var loader = new YAHOO.util.YUILoader({

        // Identify the components you want to load. Loader will automatically
        // identify any additional dependencies required for the specified
        // components.
        require: ["calendar"],

        // where on my local domain the library lives
        base: '${baseweb}/module/ajax/yui/',

        // Configure loader to pull in optional dependencies. For
        // example, animation is an optional dependency for slider.
        loadOptional: true,

        // The function to call when all script/css resources have been loaded
        onSuccess: function() {
          //this is your callback function; you can use
          //this space to call all of your instantiation
          //logic for the components you just loaded.

          var mySelectHandler = function(type, args, obj) {
            var dates = args[0];
            var date = dates[0];
            var year = date[0], month = date[1], day = date[2];
            window.location = "${request.contextPath}/myTime.do?date=" + month
              + "/" + day + "/" + year;
          };

          calendar = new YAHOO.widget.Calendar("calendar-container", {navigator:true});
          calendar.selectEvent.subscribe(mySelectHandler, calendar, true);
          var date = "${date}";
          calendar.cfg.setProperty("pagedate", date.substring(0,2) + "/" +
            date.substring(6,10));
          calendar.cfg.setProperty("selected", "${date}");
          calendar.render();
        }
      });

      // Load the files using the insert() method. The insert method takes an
      // optional configuration object, and in this case we have configured
      // everything in the constructor, so we don't need to pass anything to
      // insert().
      loader.insert();

    </script>

  </head>

  <body>

    <h3>Activity Detail</h3>

    <form class="clearfix bottomMargin" id="myTime" name="myTime"
      action="myTime.do" method="POST">

      <#-- Time entry form. -->

      <div class="error" id="message">
        <@spring.bind "command.*"/>
        <@spring.showErrors "<br>" />
      </div>
      
      <div class="clearfix column left">
      
        <span class="formfield">
          <label for="name">User: <span>${user.name}</span></label>
        </span>	
        
        <div class="column left">      
          
          <span class="formfield">
          <label for="roles">Project</label>
          <@spring.formSingleSelect "command.projectId", projects,
            "class='largeInput'" />
          </span>
          
          <span class="formfield">
          <label for="roles">Activity</label>
          <@spring.formSingleSelect "command.activityId", activities,
            "class='largeInput'" />
          </span>
          
        </div>
          
        <div class="column right">
      
          <span class="formfield">
          <label for="start">Start <span>(hh:mm)</span></label>
          <@spring.formInput "command.start", "size='2'"/>
          </span>
      
          <span class="formfield">
          <label for="duration">Duration <span>(minutes)</span></label>
          <@spring.formInput "command.duration", "size='2'"/>
          </span>
          
        </div>
        
        <span class="formfield">
          <label for="note">Note</label>
          <@spring.formTextarea "command.comment", "rows='2' cols='55'"/>
        </span>
      
        <input class="btn rightMargin" type="submit" value="submit" />
      
        <input class="btn" type="submit" value="cancel"
          onclick="window.location='${request.contextPath}/myTime.do';return false;" />
        
      </div>
		  
      <div class="clearfix column right">
        <div id="calendar-container" class="yui-skin-sam"></div>
      </div>     

      <span class="formfield">
        <@spring.bind "command.date" />
        <input type="hidden" class="text" id="id"
            name="${spring.status.expression}" value="${date}" />
      </span>     

    </form>

    <#-- Table of time entries -->
  
  	<h3>Activity List</h3>
    <table width='100%' border='0' class='results' id='timeEntryList'
      cellpadding='0' cellspacing='0'>
      <col width='5%'/>
      <col width='20%'/>
      <col width='20%'/>
      <col width='10%'/>
      <col width='10%'/>
      <col width='25%'/>
      <col width='10%'/>
      <thead>
        <tr>
          <th>Id</th>
          <th>Project</th>
          <th>Activity</th>
          <th>Start</th>
          <th>Duration</th>
          <th>Note</th>
          <th>&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <#if timeEntryList?size == 0>
          <tr><td colspan='7'>No time entries registered.</td></tr>
        </#if>
        <#list timeEntryList as timeEntry>
          <tr>
            <td>
              <a href='${request.contextPath}/editTimeEntry.do?timeEntryId=${timeEntry.id}'>
                  ${timeEntry.id}
              </a>
            </td>
            <td>${timeEntry.project.name}</td>
            <td>${timeEntry.activity.name}</td>
            <td>${timeEntry.period.startHour}:${timeEntry.period.startMinutes}</td>
            <td>${timeEntry.period.duration}</td>
            <td>${timeEntry.comment}</td>

            <#-- Delete a time entry -->
            <td>
              <form class="deleteTimeEntry" method="POST"
                action="deleteTimeEntry.do">
                <@spring.bind "command.timeEntryId" />
                <input type="hidden" name="timeEntryId" value="${timeEntry.id}"/>
                <input class="btn" type="submit" value="Delete"
                  onclick="return confirm('Are you sure you want to delete the time entry?');" />
              </form>
            </td>

          </tr>
        </#list>
      </tbody>
    </table>

  </body>
 <!-- vim: set ts=2 et sw=2 ai filetype=xml: -->
</html>

