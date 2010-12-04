<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>
  <!-- vim: set ts=2 sw=2 et ai: -->
  <cas:authenticationSuccess>

    <cas:user>${((assertion.chainedAuthentications?last).principal.id)?xml}</cas:user>

    <#if pgtIou??>
      <cas:proxyGrantingTicket>${pgtIou}</cas:proxyGrantingTicket>
    </#if>

    <#if assertion.chainedAuthentications?size gt 1>
      <cas:proxies>
        <#list assertion.chainedAuthentications as proxy>
          <#if proxy_has_next>
            <cas:proxy>${proxy.principal.id?xml}</cas:proxy>
          </#if>
        </#list>
      </cas:proxies>
    </#if>

  </cas:authenticationSuccess>
</cas:serviceResponse>

