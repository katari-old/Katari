/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.StringEscapeUtils;

/** Filter to proxy connections to specific nodes in a cluster.
 *
 * This filter is used let users select which node to hit in a cluster. It
 * shows a list of available nodes, and proxies the request to the user
 * selected one.
 *
 * It is initialized by a map of node ids (a simple string that is shown to the
 * user), to an schema://host:port prefix corresponding to that node. The hosts
 * defined in this map must be accessible by all the other nodes in the
 * cluster.
 *
 * To use this filter to proxy katari monitoring to a specific node, use:
 *
 * - Add a parameter katari-node= (with empty value) to the menu item
 *   (link='katari-monitoring?katari-node=')
 *
 * - Add the filter to match the path of the monitoring endpoints:
 *   (.*\/module/monitoring/katari-monitoring.*)
 *
 * - Add the nodes to the filter: Node1 - http://localhost:8098 / Node2 -
 *   http://localhost:8099.
 *
 * With this, when hitting the monitoring menu item, instead of showing the
 * monitoring page, it shows a list of cluster node. Each node is a link to the
 * monitoring module in the corresponding cluster node.
 */
public class ClusterNodeProxyFilter implements Filter {

  /** A map of node names to url prefix (scheme, host and port).
   *
   * The keys in this map are url compatible (only digits, letters,
   * - and _). It never contains the 'local' key. It cannot be null.
   *
   * The url prefix cannot end in '/'.
   */
  private Map<String, String> nodeToUrl = new HashMap<String, String>();

  /** Constructor, creates a ClusterNodeProxyFilter instance.
   *
   * The keys in the map must only contain digits, letters, _ and -. The key
   * named 'local' is reserved and cannot be used.
   *
   * If the map is empty, this filter does nothing and just forwards the
   * request to the local node.
   *
   * @param theNodeToUrl a map of node names to node urls. It cannot be null.
   */
  public ClusterNodeProxyFilter(final Map<String, String> theNodeToUrl) {
    Validate.notNull(theNodeToUrl, "The map of nodes cannot be null.");
    for (String key : theNodeToUrl.keySet()) {
      Validate.isTrue(!key.equals("local"), "The key 'local' is forbidden.");
      Validate.isTrue(key.matches("^[a-zA-Z0-9-_-]*$"),
          "The key can only contain letters, digits, - and _.");
    }
    nodeToUrl = theNodeToUrl;
  }

  /** {@inheritDoc}
   */
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  /** {@inheritDoc}
   */
  public void doFilter(final ServletRequest servletRequest,
      final ServletResponse servletResponse,
      final FilterChain chain) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    String node = request.getParameter("katari-node");
    if (node == null) {
      // Find the node in a cookie.
      node = "";
      if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
          if (cookie.getName().equals("katari-node")) {
            node = cookie.getValue();
          }
        }
      }
    }
    if (node.equals("") && !nodeToUrl.isEmpty()) {
      // There are nodes configured but none was selected.
      listNodes(request, response);
    } else if (node.equals("local") || nodeToUrl.isEmpty()) {
      // There are no nodes configured, or we want the local node. Show the
      // current node,
      chain.doFilter(request, response);
    } else {
      proxyToNode(request, response, node);
    }
  }

  /** Performs a request to another node and sends the result to the browser.
   *
   * @param request the original http request. It cannot be null.
   *
   * @param response the original http response. It cannot be null.
   *
   * @param node the node to forward the request to. It cannot be null.
   */
  @SuppressWarnings("unchecked")
  private void proxyToNode(final HttpServletRequest request,
      final HttpServletResponse response, final String node)
      throws IOException, MalformedURLException {

    // Proxy another node.
    Cookie cookie = new Cookie("katari-node", node);
    response.addCookie(cookie);
    // Resolve url from node name.
    URI destination = calculateDestination(request, node, true);
    request.setAttribute("katari-skip-decoration", "true");

    HttpURLConnection connection = null;

    boolean sendPayload = "POST".equals(request.getMethod())
          || "PUT".equals(request.getMethod())
          || "OPTIONS".equals(request.getMethod());

    try {
      connection = (HttpURLConnection) destination.toURL().openConnection();
      connection.setInstanceFollowRedirects(false);
      if (sendPayload) {
        connection.setDoOutput(true);
      }
      connection.setRequestMethod(request.getMethod());

      // Send headers received from the browser to the target host.
      Enumeration<String> headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        Enumeration<String> headerValues = request.getHeaders(headerName);
        while (headerValues.hasMoreElements()) {
          String headerValue = headerValues.nextElement();
          connection.addRequestProperty(headerName, headerValue);
        }
      }

      InputStream in = null;
      OutputStream out = null;
      byte[] buffer = new byte[8192];
      int count;

      if (sendPayload) {
        try {
          // Send the content received from the browser to the target host.
          in = request.getInputStream();
          out = connection.getOutputStream();
          count = in.read(buffer);
          while (count != -1) {
            out.write(buffer, 0, count);
            count = in.read(buffer);
          }
        } finally {
          if (out != null) {
            in = null;
            out.close();
            out = null;
          }
        }
      }

      int responseCode = connection.getResponseCode();

      response.setStatus(responseCode);
      // Send the headers received from the target host to the browser.
      Map<String, List<String>> headers = connection.getHeaderFields();
      for (String headerName : headers.keySet()) {
        for (String headerValue : headers.get(headerName)) {
          if (headerName != null) {
            response.addHeader(headerName, headerValue);
          }
        }
      }

      try {
        // Send the content received from the target host to the browser.
        if (responseCode < 400) {
          // No error.
          in = connection.getInputStream();
        } else {
          in = connection.getErrorStream();
        }
        out = response.getOutputStream();
        buffer = new byte[8192];
        count = in.read(buffer);
        while (count != -1) {
          out.write(buffer, 0, count);
          count = in.read(buffer);
        }
      } finally {
        if (in != null) {
          out = null;
          in.close();
          in = null;
        }
      }
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  /** Calculates the destination url to hit the node.
   *
   * This operation considers two scenarios: the first one is to generate a url
   * to hit the cluster, specifying which node to proxy to. This uses the
   * request object and adds a query string parameter katari-node with the
   * provided node name.
   *
   * The other scenario is used to hit an internal node from another node. The
   * generated url is built from the host associated with the node, the request
   * and with a query string parameter katari-node with 'local' as its value.
   *
   * @param request the http request. It cannot be null.
   *
   * @param node the name of the node to proxy to. It cannot be null.
   *
   * @param toLocal true if the destination must point to an internal node,
   * true if the destination must point to the cluster.
   */
  @SuppressWarnings("unchecked")
  private URI calculateDestination(final HttpServletRequest request,
      final String node, final boolean toLocal) {
    URI destination;
    try {
      // If the node is local, we use the current request host.
      String host;
      String targetNode;
      if (toLocal) {
        host = nodeToUrl.get(node);
        targetNode = "local";
      } else {
        host = request.getScheme() + "://" + request.getServerName()
            + ":" + request.getServerPort();
        targetNode = node;
      }

      destination = new URI(host + request.getRequestURI());
      destination = new URI(destination + "?katari-node=" + targetNode);

      Enumeration<String> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements()) {
        String parameterName = parameterNames.nextElement();
        if (!parameterName.equals("katari-node")) {
          String[] parameterValues;
          parameterValues = request.getParameterValues(parameterName);
          for (String parameterValue : parameterValues) {
            destination = new URI(destination + "&"
                + URLEncoder.encode(parameterName, "UTF-8") + "="
                + URLEncoder.encode(parameterValue, "UTF-8"));
          }
        }
      }
    } catch (URISyntaxException e) {
      throw new RuntimeException("Error building uri", e);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Error building uri", e);
    }
    return destination;
  }

  /** Generates an html page that lists the available nodes.
   *
   * Each node in the list is a link to the original url, but with a parameter
   * katari-node set to the name of the node.
   *
   * @param request the http request. It cannot be null.
   *
   * @param response the http response where this operation writes the html
   * page. It cannot be null.
   */
  private void listNodes(final HttpServletRequest request,
      final HttpServletResponse response) throws IOException {
    // Show the list of nodes to select.
    Cookie cookie = new Cookie("katari-node", "");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    response.setContentType("text/html");
    PrintWriter writer = response.getWriter();
    writer.append("<h3>List of Cluster Nodes</h3>");
    for (String nodeName : nodeToUrl.keySet()) {
      writer.append("<br><a href = '");
      URI nodeUrl = calculateDestination(request, nodeName, false);
      writer.append(StringEscapeUtils.escapeHtml(nodeUrl.toString()));
      writer.append("'>" + nodeName + "</a>");
    }
  }

  /** {@inheritDoc}
   */
  public void destroy() {
  }
}

