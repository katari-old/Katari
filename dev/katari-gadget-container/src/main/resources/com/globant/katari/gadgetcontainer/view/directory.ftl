<#import "spring.ftl" as spring />
<html>

  <head>
    <title>Select Application</title>

    <script type='text/javascript'
      src='${baseweb}/module/gadgetcontainer/assets/js/jquery-1.4.2.js'>
    </script>

    <script type='text/javascript'>
      var addApplicationToGroup = function(groupName, applicationId) {
        var that = this;
        
        var parameters = 'groupName=' + groupName + '&applicationId=' +
          applicationId;

        jQuery.getJSON(
          '${baseweb}/module/gadgetcontainer/addApplicationToGroup.do?' +
          parameters,
          function(data) {
            jQuery("li.item_" + applicationId).hide();
          });
      };
    </script>

  </head>

  <body>

    <h3>Applications</h3>

    <a href='${baseweb}${command.returnUrl}'>Close</a>

    <#if result?has_content>
      <ul class='directory'>
        <#list result as application>

          <#if application_index = 0>
            <#assign class = 'first'>
          <#elseif ! application_has_next>
            <#assign class = 'last'>
          <#else>
            <#assign class = ''>
          </#if>

          <#escape x as x?html>
            <li class='directory_entry ${class} item_${application.id}'>
              <div class='gadget'>
                <img src='${application.thumbnail!}' alt='${application.title}'>
                <div>
                  <input type="button" value="Add it now"
                    onclick='addApplicationToGroup("${command.gadgetGroupName}", ${application.id})'>
                </div>
              </div>
              <div class='author'>
                <span>
                  <#if application.author??>
                    By ${application.author}
                  <#else>
                    <!-- empty span. -->
                  </#if>
                </span>
              </div>
              <div class='info'>
                <h4>${application.title}</h4>
                <#if application.description??>
                  <p>${application.description}</p>
                </#if>
              </div>
              <div class='clear'><!-- Empty div --></div>
            </li>
          </#escape>
        </#list>
      </ul>
    </#if>
  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

