<#import "spring.ftl" as spring />
<#import "katari.ftl" as katari />

<html>

  <head>
    <title>Users</title>
  </head>

  <body>

    <#escape x as x?html>
      <h3>List User</h3>
      <form class="bottomMargin" action="users.do" method="GET">

        <!-- TODO: put this in katari.ftl -->
        <#macro showErrors separator >
          <#list spring.status.errorMessages as error >
            ${error?html}
            <#if error_has_next>${separator}</#if>
          </#list>
        </#macro>

        <span class="error" id="message">
          <@spring.bind "command.*"/>
          <@showErrors "<br/>" />
        </span>

        <#-- Searching -->
        <div class="clearfix">
          <@spring.bind "command.containsFilter.value" />
          <input class="left rightMargin" type="text"
              name="${spring.status.expression}"
              value="${spring.status.value?default("")}" />
          <@spring.bind "command.containsFilter.columnName" />
          <input type="hidden" name="${spring.status.expression}"
            value="name"/>
          <span class="btnContainer left">
            <input class="btn" type="submit" value="search"/>
          </span>
        </div>

      </form>

      <a class='btn' href='${request.contextPath}/createUser.do'>New</a>

      <#-- Table of users -->
      <table width='100%' border='0' class='results bottomMargin' id='userList'
        cellpadding='0' cellspacing='0'>
      <col width='40%'/>
      <col width='40%'/>
      <col width='20%'/>
        <thead>
          <tr>
            <th>Name
              <#-- Sorting -->
              <a href='${request.contextPath}/users.do?${command.urlOrder}&amp;sorting.columnName=name'>
                Az
              </a>
            </th>
            <th>E-Mail</th>
            <th>&nbsp;</th>
          </tr>
        </thead>
        <#if users?has_content>
          <tbody>
            <#list users as user>
            <tr>
              <td>
                <a href='${request.contextPath}/userView.do?userId=${user.id?c}'>
                  ${user.name}
                </a>
              </td>
              <td>${user.email}</td>
              <#-- Delete a user -->
              <td>
                <@katari.secureUrlArea url="userDelete.do"; url>
                  <#if currentUserId != user.id>
                  <form method="POST" action="userDelete.do?${command.url}"
                      class='innerform'>
                      <input type="hidden"  name="userId" value="${user.id?c}"
                          class="text" />
                      <span class="btnContainer"><input class="btn" type="submit"
                          value="delete" onclick="return confirm('Are you sure you'
                          + ' want to delete the user ${user.name}?');" /></span>
                    </form>
                  </#if>
                </@katari.secureUrlArea>
              </td>
            </tr>
            </#list>
          </tbody>
        </#if>
      </table>

      <#-- Paging -->
      <div class="paginator">
        <#if 0 < command.paging.pageNumber>
          <a href='${request.contextPath}/users.do?${command.urlPrevPage}'><< Prev</a>
        <#else><< Prev </#if>|
        <#if command.paging.pageNumber < command.paging.totalPageNumber - 1>
          <a href='${request.contextPath}/users.do?${command.urlNextPage}'>Next >></a>
        <#else> Next >></#if>
      </div>

    </#escape>
  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->