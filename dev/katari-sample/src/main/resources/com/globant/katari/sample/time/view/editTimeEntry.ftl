<#import "spring.ftl" as spring />
<html>

  <head>
    <title>Edit Time Entry</title>
  </head>

  <body>

    <h3>Edit Time Entry</h3>

    <form class="clearfix" id="editTimeEntry" action="editTimeEntry.do"
      method="POST">

    <span class="error" id="message">
      <@spring.bind "command.*"/>
      <@spring.showErrors  "<br/>" />
    </span>

    <div class="clearfix column left">

      <div class="column left">

        <span class="formfield">
          <label for="projects">Project</label>
          <@spring.formSingleSelect "command.projectId", projects,
            "class='largeInput'" />
        </span>

        <span class="formfield">
          <label for="activities">Activity</label>
          <@spring.formSingleSelect "command.activityId", activities,
            "class='largeInput'" />
        </span>

      </div>

    <div class="column right">

        <span class="formfield">
          <label for="start">Start <span>(hh:mm)</span></label>
          <@spring.formInput "command.start", "size='2'" />
        </span>

        <span class="formfield">
          <label for="duration">Duration <span>(minutes)</span></label>
          <@spring.formInput "command.duration", "size='2'" />
        </span>

      </div>

      <span class="formfield">
        <label for="comment">Note:</label>
        <@spring.formTextarea "command.comment", "rows='2' cols='55'"/>
      </span>

      <@spring.formHiddenInput "command.timeEntryId" />

      <input class="btn rightMargin" type="submit" value="save"/>
      <input class="btn" type="submit" value="cancel" onclick="window.location=
          '${request.contextPath}/myTime.do?date=${command.date?date}';return false;"/>

    </div>

  </form>

  </body>
</html>
