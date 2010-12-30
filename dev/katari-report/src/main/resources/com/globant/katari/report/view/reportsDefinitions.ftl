<#import "spring.ftl" as spring />
<#import "katari.ftl" as katari />

<html>
  <head>
    <title>Report Definitions</title>
    <script type='text/javascript'>
      function confirmDelete(id) {
        var agree=confirm('Are you sure you want to delete the this report?');
        if (agree) {
          document.getElementById('reportId').value=id;
          document.getElementById('deleteReport').submit();
        }
      }
    </script>
  </head>

  <body>
    <h3>Report Definitions</h3>
	
    <a class='btn' href='editReport.do'>New</a>

	<table width='100%' border='0' class='results' id='reportList' cellpadding='0' cellspacing='0'>
	  <col width='20%'/>
	  <col width='20%'/>
	  <col width='20%'/>
	  <col width='20%'/>
	  <col width='20%'/>
      <thead>
        <tr>
          <th>Name</th>
          <th>Description</th>
          <th colspan="3">&nbsp;</th>
        </tr>
      </thead>
      <tbody>
        <#if reportsDefinitions?size == 0>
          <tr><td colspan='5'>No reports found</td></tr>
        </#if>
        <#list reportsDefinitions as report>
        <tr>
          <td><a href="editParameters.do?reportId=${report.id}">${report.name}</a></td>
          <td>${report.description}</td>
          <td>
          	<@katari.secureUrlArea url="editReport.do"; url>
              <form method="GET" action="${url}" class="innerform">
                <input type="hidden" name="reportId" value="${report.id}" />
                <input type="submit" value="Edit" />
              </form>
            </@katari.secureUrlArea>
          </td>
          <td>
          	<@katari.secureUrlArea url="downloadReport.do"; url>
              <form method="GET" action="${url}" class="innerform">
                <input type="hidden" name="reportId" value="${report.id}" />
                <input type="submit" value="Download">
              </form>
            </@katari.secureUrlArea>
          </td>
          <td>
            <@katari.secureUrlArea url="deleteReport.do"; url>
              <form method="POST" action="${url}" class="innerform">
                <input type="hidden" name="reportId" value="${report.id}" />
                <input type="submit" value="Delete" onclick="return confirm('Are you sure you want to delete the report ${report.name}?');">
              </form>
            </@katari.secureUrlArea>
          </td>
        </tr>
        </#list>
      </tbody>
    </table>

  </body>
  <!-- vim: set ts=2 et sw=2 ai filetype=xml: -->
</html>

