<#import "spring.ftl" as spring />
<html>
  <head>
    <title>Add Report Definition</title>
  </head>
  <body>
    <h3>Report Definition</h3>
    <form id="editReport" name="editReport" method="POST" action="" enctype="multipart/form-data">
      <span class="error" id="message">
        <@spring.bind "command.*"/>
        <@spring.showErrors "<br/>" />
      </span>
      
      <div class="clearfix column left">
      
        <div class="column left">
          <span class="formfield">
            <label for="name">Report Name</label>
            <@spring.formInput "command.name" />
          </span>

          <span class="formfield">
            <label for="name">Report Description</label>
            <@spring.formInput "command.description" />
          </span>

          <span class="formfield">
            <label for="reportContent">Select File Location</label>
            <input type="file" name="reportContent" />
          </span>
        
          <input class="btn" type="submit" value="Save Report"/>
          <input class="btn" type="submit" value="Cancel" onclick=
            "window.location='reports.do';return false;"/>
        
        </div>
      
        <div class="column left">
          <div class="formfield">
            <label for="roles">Roles:</label>
            <div>
              <@spring.formCheckboxes "command.roleIds", command.availableRoles, "</div><div>",
                "class='check left rightMargin'"/>
            </div>
          </div>
        </div>     
      
      </div>
      
    </form>
  </body>
</html>
