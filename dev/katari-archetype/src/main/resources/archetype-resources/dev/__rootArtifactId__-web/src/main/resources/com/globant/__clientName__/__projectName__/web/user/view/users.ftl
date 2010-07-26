<#import "spring.ftl" as spring />
<html>

  <head>
    <title>Users</title>
  </head>

  <body>

    <#-- Message -->
    <!-- div align="center">
      ${message}
    </div -->

    <h3>List User</h3>
    <form class="bottomMargin" action="users.do" method="GET">

      <#-- Searching -->
      <div class="clearfix">
        <@spring.bind "userFilter.containsFilter.value" />
        <input class="left rightMargin" type="text"
            name="${spring.status.expression}"
            value="${spring.status.value?default("")}" />
        <@spring.bind "userFilter.containsFilter.columnName" />
        <input type="hidden" name="${spring.status.expression}" value="name"/>
        <span class="btnContainer left">
          <input class="btn" type="submit" value="search"/>
        </span>
      </div>

    </form>

    <#-- Table of users -->
    <table width='100%' border='0' class='results bottomMargin' id='userList'
      cellpadding='0' cellspacing='0'>
	  <col width='20%'/>
	  <col width='30%'/>
	  <col width='30%'/>
	  <col width='20%'/>
      <thead>
        <tr>
          <th>Id</th>
          <th>Name
            <#-- Sorting -->
            <a href='${request.contextPath}${order}&amp;sorting.columnName=name'>
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
                  ${user_index + 1}
              </a>
            </td>
          <td>${user.name}</td>
            <td>${user.email}</td>
            <#-- Delete a user -->
            <td>
              <#if currentUserId != user.id>
                <form method="POST" action="userDelete.do" class='innerform'>
                  <input type="hidden"  name="userId" value="${user.id}"
                      class="text" />
                  <span class="btnContainer"><input class="btn" type="submit"
                      value="delete" onclick="return confirm('Are you sure you'
                      + ' want to delete the user ${user.name}?');" /></span>
                </form>
              </#if>
            </td>
          </tr>
          </#list>
        </tbody>
      </#if>
    </table>

    <#-- Paging -->
    <div class="paginator">
      <#if 0 < userFilter.paging.pageNumber>
        <a href='${request.contextPath}${previousPage}'><< Prev</a>
      <#else><< Prev </#if>|
      <#if userFilter.paging.pageNumber < totalPageNumber - 1>
        <a href='${request.contextPath}${nextPage}'>Next >></a>
      <#else> Next >></#if>
    </div>

  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

