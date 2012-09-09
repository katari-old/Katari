<#import "spring.ftl" as spring />

<html>
  <head>
    <title>EhCache - Viewer</title>
  </head>
  <body>
    <h3>List of cache entries</h3>
    <table class="results">
      <colgroup span="2"></colgroup>
      <thead>
        <tr>
          <th></th>
          <th colspan='2'>Average time (ms)</th>
          <th rowspan='2'>Searches<br>per second</th>

          <th colspan='4'>Cache</th>
          <th colspan='3'>Memory</th>
          <th colspan='3'>Off Heap</th>
          <th colspan='3'>Disk</th>
          <th></th>
        </tr>
        <tr>

          <th>Name</th>
          <th>Search</th>
          <th>Get</th>

          <th>#</th>
          <th>Hit</th>
          <th>Miss</th>
          <th>Evict</th>

          <th>#</th>
          <th>Hit</th>
          <th>Miss</th>

          <th>#</th>
          <th>Hit</th>
          <th>Miss</th>

          <th>#</th>
          <th>Hit</th>
          <th>Miss</th>

          <th></th>
        </tr>
      </thead>
      <#list result as cache>
        <#assign statistics = cache.statistics />
        <tbody>
          <tr>
            <td>${statistics.associatedCacheName}</td>
            <td>${statistics.averageSearchTime}</td>
            <td>${statistics.averageGetTime}</td>
            <td>${statistics.searchesPerSecond}</td>

            <td>${statistics.objectCount}</td>
            <td>${statistics.cacheHits}</td>
            <td>${statistics.cacheMisses}</td>
            <td>${statistics.evictionCount}</td>

            <td>${statistics.memoryStoreObjectCount}</td>
            <td>${statistics.inMemoryHits}</td>
            <td>${statistics.inMemoryMisses}</td>

            <td>${statistics.offHeapStoreObjectCount}</td>
            <td>${statistics.offHeapHits}</td>
            <td>${statistics.offHeapMisses}</td>

            <td>${statistics.diskStoreObjectCount}</td>
            <td>${statistics.onDiskHits}</td>
            <td>${statistics.onDiskMisses}</td>

            <td><a href="cleanCache.do?cacheName=${cache.name}">clear</a></td>
          </tr>
        </tbody>
      </#list>
    </table>
  </body>
  <!-- vim: set ts=2 sw=2 et ai: -->
</html>

