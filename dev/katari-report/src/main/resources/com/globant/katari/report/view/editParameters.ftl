<#import "spring.ftl" as spring />

<html>
<head>
  <title>Edit Parameters Descriptors</title>
  <!-- Load the YUI Loader script: -->
  <script>
    var loader = new YAHOO.util.YUILoader({
        require: ["calendar"],
        base: '${baseweb}/module/ajax/yui/',
        loadOptional: false,
        onSuccess: function() {
        }
    });
    loader.insert();

    function getActualDate(){
      actualDate = new Date();
      day = '' + actualDate.getDate() + '';
      month = '' + (actualDate.getMonth() + 1) + '';
      fullYear = actualDate.getFullYear();

      if (day.length == 1) {
          day = '0' + day;
      }
      if (month.length == 1) {
          month = '0' + month;
      }
      return day + "/" + month + "/" + fullYear;
    }

    function getCalendarStartDate(inputField, calendarField, trigger){
      var mySelectHandler = function(type, args, obj){
        var selected = new Array();
        selected = args[0].toString().split(",");

        day = selected[2];
        month = selected[1];
        year = selected[0];

        if (day.length == 1) {
            day = '0' + day;
        }
        if (month.length == 1) {
            month = '0' + month;
        }

        document.getElementById(inputField).value = day + "/" + month
          + "/" + year;
        obj.render();
        obj.hide();
      };

      var myCalendar = new YAHOO.widget.Calendar(calendarField);
      myCalendar.cfg.setProperty("close", true);
      myCalendar.cfg.setProperty("close", true);
      myCalendar.selectEvent.subscribe(mySelectHandler, myCalendar, true);
      YAHOO.util.Event.addListener(trigger, "click", myCalendar.show, myCalendar, true);
      myCalendar.render();
    }
    
    function reloadDropdown() {
      document.getElementById("reloading").value='true';
      document.getElementById("reportForm").submit();
    }    
  </script>
</head>

<body>
  <h3>Report Parameters: ${command.reportName}</h3>

  <form method="POST" action="" id="reportForm" name="reportForm">
    <span class="error" id="message">
      <@spring.bind "command.*"/>
      <@spring.showErrors "<br/>" />
    </span>
    <#-- Table of clients -->
    <table width='100%' border='0' class='results' id='parametersList' cellpadding='0' cellspacing='0'>
	  <col width='20%'/>
	  <col width='20%'/>
	  <col width='60%'/>
      <thead>
        <tr>
          <th>Name</th>
          <th>Value</th>
          <th>&nbsp;</th>
        </tr>
      </thead>
      <tbody>
      	<#assign rowCounter = 0 />
      	<#list command.parameters as parameter>
        <tr>
          <td>${parameter.name}</td>
          <#switch parameter.type>
            <#case "java.util.Date">
              <td>
                <@spring.bind "command.values[${parameter.name}]"/>
                <input id="calText_${rowCounter}" type="text" name="${spring.status.expression}" readonly="1" 
                   value="${command.values[parameter.name]!''}"/>                   
              </td>
              <td>
                <image src="${baseweb}/module/decorator/image/calendar.gif" id="f_trigger_c${rowCounter}"
                   title="Date selector"
                   onclick="javascript:getCalendarStartDate('calText_${rowCounter}', 'calContainer_${rowCounter}','f_trigger_c${rowCounter}');"/>
                <span class="yui-skin-sam">
                   <div id="calContainer_${rowCounter}" style="z-index:99; position:absolute"></div>
				        </span>
              </td>
              <#break>
            <#case "java.lang.Boolean">
              <td>
                <@spring.bind "command.values[${parameter.name}]"/>
                <input type="checkbox" name="${spring.status.expression}" value="true"/>
              </td>
              <td>&nbsp;</td>
              <#break>
            <#case "java.lang.Integer">
              <td>
                <#if parameter.dropdown>
                  <@spring.formSingleSelect "command.values[${parameter.name}]", command.getDropdownOptions(parameter), "onChange=\"reloadDropdown();\"" />
                <#else>
                  <@spring.formInput "command.values[${parameter.name}]"/>
                </#if>
              </td>
              <td>&nbsp;</td>
              <#break>
            <#default>
              <td>
                <#if parameter.dropdown>
                  <@spring.formSingleSelect "command.values[${parameter.name}]", command.getDropdownOptions(parameter), "onChange=\"reloadDropdown();\"" />
                <#else>
                  <@spring.formInput "command.values[${parameter.name}]"/>                  
                </#if>
              </td>
              <td>&nbsp;</td>
          </#switch>
        </tr>
        <#assign rowCounter = rowCounter + 1/>
        </#list>
        </tr>
          <td>
            <@spring.bind "command.reportType"/>
            <label for="${spring.status.expression}">Select the report output type:</label>
          </td>
          <td>
          	<select name="${spring.status.expression}">
              <#list reportTypes as reportType>
                <option value="${reportType}" <#if reportType == "PDF">selected</#if>>
                  ${reportType}
                </option>
              </#list>
            </select>
          </td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <input type="hidden" id="reloading" name="reloading" value="false"/>          
            <input type="hidden" name="parameterReportId" value="${command.reportId}"/>
            <input class="btn" type="submit" value="show report"/>
          </td>
          <td></td>
        </tr>
      </tbody>
    </table>
    
  </form>

</body>

</html>
