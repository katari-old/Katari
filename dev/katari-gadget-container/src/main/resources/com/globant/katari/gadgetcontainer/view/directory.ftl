<#import "spring.ftl" as spring />
<html>

  <head>
    <title>Select Application</title>

    <script type='text/javascript'
      src='${baseweb}/module/jsmodule/com/globant/jslib/jquery/jquery.js'>
    </script>

    <script type='text/javascript'>
      var gadgetContainerElements = ${result?size};
      var addApplicationToGroup = function(groupName, applicationId) {
        var that = this;
        
        var parameters = 'groupName=' + groupName + '&applicationId=' +
          applicationId;

        jQuery.getJSON(
          '${baseweb}/module/gadgetcontainer/addApplicationToGroup.do?' +
            parameters,
          function(data) {
            var row;
          
            row = jQuery('tr.item_' + applicationId);
            row.hide();
            // Show the 'no more elements' banner if the list is empty.
            gadgetContainerElements = gadgetContainerElements - 1;
            if (gadgetContainerElements === 0) {
              jQuery('tr.nothingNew').show();
            } else {
              if (row.hasClass('first')) {
                row.nextAll(':visible').first().addClass('first');
              }
              if (row.hasClass('last')) {
                row.prevAll(':visible').first().addClass('last');
              }
            }
            row.removeClass('first');
            row.removeClass('last');
          });
      };
    </script>

  </head>

  <body>

    <h3>Personalize your dashboard</h3>

    <span class="genLink right"><a href='${baseweb}${command.returnUrl}'>Back to Dashboard</a></span>

    <table width='100%' class='directory' border='0' cellpadding='0'
        cellspacing='0'>
      <col width='15%'>
      <col width='75%'>
      <col width='10%'>
      <#list result as application>

        <#if application_index = 0>
          <#assign class_first = 'first'>
        <#else>
          <#assign class_first = ''>
        </#if>
        <#if ! application_has_next>
          <#assign class_last = 'last'>
        <#else>
          <#assign class_last = ''>
        </#if>

        <#escape x as x?html>
          <tr class='directory_entry ${class_first} ${class_last} item_${application.id}'>
            <td class='gadget'>
              <#if application.thumbnail??>
                <img src='${application.thumbnail!}'
                  alt='${application.title}'>
              </#if>
              <div>
                <input class="btn" type="button" value="Add it now"
                  onclick='addApplicationToGroup("${command.gadgetGroupName}", ${application.id})'>
              </div>
            </td>              
            <td class='info'>
              <h5>${application.title}</h5>
              <#if application.description??>
                <p>${application.description}</p>
              </#if>
            </td>
            <td class='author'>
              <span>
                <#if application.author??>
                  By ${application.author}
                <#else>
                  <!-- empty span. -->
                </#if>
              </span>
            </td>
          </tr>
        </#escape>
      </#list>
      <#if result?has_content>
        <#assign nothingNewStyle = "style='display:none'">
      <#else>
        <#assign nothingNewStyle = "">
      </#if>
      <tr class='nothingNew' ${nothingNewStyle}>
        <td colspan='3'>
          There is nothing new to add to you panel.
        </td>
      </tr>
    </table>
    <!-- div class="paginator">
        To add for paging.
        &lt;&lt; Prev | Next &gt;&gt;
    </div -->
  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

