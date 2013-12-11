<#import "spring.ftl" as spring />
<#import "katari.ftl" as katari />

<html>

  <head>
    <title>Search</title>
    <script type="text/javascript" charset="utf-8">
      function nextPage() {
        nextPrevPage(1);
      }

      function prevPage() {
        nextPrevPage(-1);
      }

      function nextPrevPage(where) {
        var form = document.getElementById("nextPageForm");
        var page = form.getInputs("hidden", "pageNumber")[0];
        page.value = parseInt(page.value,10) + where;
        form.submit();
      }
    </script>
  </head>

  <body>

    <h3>Search</h3> 

    <form action="search.do" method="POST" id="searchForm" class='bottomMargin'>
      <input type="hidden" name="pageNumber" value="${command.pageNumber}"/>
      <@spring.formInput "command.query" />
      <input type="submit" value="Search"/>
    </form>

    <table width='100%' border='0' class='results bottomMargin' id='userList'
        cellpadding='0' cellspacing='0'>
      <col width='70%'/> 
      <col width='15%'/> 
      <col width='15%'/> 

      <thead>
        <tr>
          <th>Title</th>
          <th></th>
        </tr>
      </thead>
      <#if (searchResults?size != 0)>
        <tbody>
        <tr><th></th></tr>
          <#list searchResults as result>
            <tr>
              <td>
                <a href="${baseweb}${result.viewUrl}">${result.title}</a>
              </td>
              <td>
                <#list result.actions as action>
                  <@katari.secureUrlArea url=baseweb + action.url; url>
                    <#if !action.actionIcon??>
                      <a href="${url}">${action.name}</a>
                    <#else>
                      <a href="${url}">${action.icon}</a>
                    </#if>
                  </@katari.secureUrlArea>
                </#list>
              </td>
            </tr>
            <tr>
              <td colspan='2'>${result.description}</td>
            </tr>
          </#list>
        </tbody>
      </#if>
    </table>

    <form id="nextPageForm" action="search.do" method="POST" style="display:none;">
      <input type="hidden" name="query" value="${command.query}"/>
      <input type="hidden" name="pageNumber" value="${command.pageNumber}"/>
    </form>

    <#if (command.totalPages > 0)>
      <#if command.pageNumber != 0>
        <div class="pager page-oneplus">
          <a href="javascript:prevPage();">&lt;&lt;</a>
      <#else>
        <div class="pager page-zero">
      </#if>
      <span>${command.pageNumber + 1} of ${command.totalPages}</span>
      <#if command.pageNumber + 1 < command.totalPages>
        <a href="javascript:nextPage();">&gt;&gt;</a>
      </#if>
        </div>
      <#elseif command.query??>
      <div>NO MATCHES FOUND FOR QUERY: ${command.query}</div>
    </#if>

  </body>

</html>

<!-- vim: set ts=2 et sw=2 ai filetype=xml: -->

