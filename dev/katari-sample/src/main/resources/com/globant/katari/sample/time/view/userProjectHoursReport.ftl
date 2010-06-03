<#import "spring.ftl" as spring />
<html>

  <head>
    <title>User hours by project report</title>
    <script type='text/javascript'>
      // Instantiate and configure Loader:
      var loader = new YAHOO.util.YUILoader({

        // Identify the components you want to load. Loader will automatically
        // identify any additional dependencies required for the specified
        // components.
        require: ["calendar"],

      // where on my local domain the library lives
      base: '${baseweb}/module/ajax/yui/',

        // Configure loader to pull in optional dependencies.  For example, animation
        // is an optional dependency for slider.
        loadOptional: true,

        // The function to call when all script/css resources have been loaded
        onSuccess: function() {
          // this is your callback function; you can use
          // this space to call all of your instantiation
          // logic for the components you just loaded.
          fromCalendar = new YAHOO.widget.Calendar(null, "container-from");
          toCalendar = new YAHOO.widget.Calendar(null, "container-to");

          fromCalendar.selectEvent.subscribe(copySelectedDateForElement("fromDate"));
          fromCalendar.selectEvent.subscribe(
            function(type, args, obj) {
              toCalendar.cfg.setProperty("mindate", eventArgsAsYahooDate(args), false);
              toCalendar.render();
            }
          );

          toCalendar.selectEvent.subscribe(copySelectedDateForElement("toDate"));
          toCalendar.selectEvent.subscribe(
            function(type, args, obj) {
              fromCalendar.cfg.setProperty("maxdate", eventArgsAsYahooDate(args), false);
              fromCalendar.render();
            }
          );

          var today = asYahooDate(fromCalendar.today);
          document.getElementById("fromDate").value = today;
          document.getElementById("toDate").value = today;

          fromCalendar.cfg.setProperty("selected", today, false);
          fromCalendar.cfg.setProperty("maxdate", today, false);
          fromCalendar.render();

          toCalendar.cfg.setProperty("selected", today, false);
          toCalendar.cfg.setProperty("mindate", today, false);
          toCalendar.render();
        }
      });

      // Load the files using the insert() method. The insert method takes an
      // optional configuration object, and in this case we have configured
      // everything in the constructor, so we don't need to pass anything to
      // insert().
      loader.insert();

    </script>

    <script type="text/javascript">
      function copySelectedDateForElement(elementId) {
        return function(type, args, obj) {
          var hiddenInput = document.getElementById(elementId);
          hiddenInput.value = eventArgsAsYahooDate(args);
        }
      };

      function asYahooDate(date) {
        return (date.getMonth()+1) + "/" + date.getDate() + "/" + (1900+date.getYear());
      };

      function eventArgsAsYahooDate(args) {
        var date = args[0][0];
        var year = date[0], month = date[1], day = date[2];
        return month + "/" + day + "/" + year;
      };
    </script>
  </head>

  <body>

    <h3>User hours by project report</h3>
    <form class="clearfix" id="userProjectHoursReport" name="userProjectHoursReport"
        action="userProjectHoursReport.do" method="POST">
      
      <span class="error" id="message">
        <@spring.bind "command.*"/>
        <@spring.showErrors  "<br/>" />
      </span>
      
      <div class="clearfix column left">
      
        <div class="column left">
        
          <label for="fromDate">Start date</label>
          <div id="container-from" class="yui-skin-sam">&nbsp;</div>
        
        </div>
        
        <div class="column right">
        
          <label for="toDate">End date</label>
          <div id="container-to" class="yui-skin-sam">&nbsp;</div>
        
        </div>
        
        <@spring.formHiddenInput "command.format"/>
        <@spring.formHiddenInput "command.fromDate"/>
        <@spring.formHiddenInput "command.toDate"/>
        <input class="btn topMargin" type="submit" value="submit"/>
        
      </div>
      
    </form>
  </body>
  <!-- vim: set ts=2 et sw=2 ai filetype=xml: -->
</html>

