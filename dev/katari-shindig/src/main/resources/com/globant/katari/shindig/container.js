/* vim: set ts=2 et sw=2 cindent fo=qroca: */

// Container must be an array; this allows multiple containers to share
// configuration.
{
  "gadgets.container": ["default"],

  // Set of regular expressions to validate the parent parameter. This is
  // necessary to support situations where you want a single container to
  // support multiple possible host names (such as for localized domains, such
  // as <language>.example.org. If left as null, the parent parameter will be
  // ignored; otherwise, any requests that do not include a parent value
  // matching this set will return a 404 error.
  "gadgets.parent": null,

  // Should all gadgets be forced on to a locked domain?
  "gadgets.lockedDomainRequired": false,

  // DNS domain on which gadgets should render.
  "gadgets.lockedDomainSuffix": "-a.example.com:8080",

  // Various urls generated throughout the code base.
  // iframeBaseUri will automatically have the host inserted
  // if locked domain is enabled and the implementation supports it.
  // query parameters will be added.
  "gadgets.iframeBaseUri": "/ifr",
  // "gadgets.iframeBaseUri": "%context%/module/shindig/gadgets/ifr",

  // jsUriTemplate will have %host% and %js% substituted.
  // No locked domain special cases, but jsUriTemplate must
  // never conflict with a lockedDomainSuffix.
  "gadgets.jsUriTemplate":
    "http://%host%%context%/module/shindig/gadgets/js/%js%",

  //New configuration for iframeUri generation:
  "gadgets.uri.iframe.lockedDomainSuffix" :  "-a.example.com:8080",
  "gadgets.uri.iframe.unlockedDomain" : "www.example.com:8080",
  "gadgets.uri.iframe.basePath" : "/gadgets/ifr",

  "gadgets.uri.js.host" : "http://www.example.com/",
  "gadgets.uri.js.path" : "/",

  // Callback URL.  Scheme relative URL for easy switch between https/http.
  "gadgets.oauthGadgetCallbackTemplate":
    "//%host%%context%/module/shindig/gadgets/oauthcallback",

  // Use an insecure security token by default
  //"gadgets.securityTokenType": "insecure",

  // Config param to load Opensocial data for social
  // preloads in data pipelining.  %host% will be
  // substituted with the current host.
  "gadgets.osDataUri": "http://%host%%context%/module/shindig/social/rpc",

  // Uncomment these to switch to a secure version
  //
  "gadgets.securityTokenType": "secure",

  // URI for the default shindig test instance.
  "defaultShindigTestHost": "localhost:8098",

  // Authority (host:port without scheme) for the proxy and concat servlets.
  "defaultShindigProxyConcatAuthority": "localhost:8098",

  // Default Uri config: these must be overridden - specified here for testing purposes
  "gadgets.uri.iframe.unlockedDomain": "${Cur['defaultShindigTestHost']}",
  "gadgets.uri.iframe.lockedDomainSuffix": "${Cur['defaultShindigTestHost']}",

  // Default Js Uri config: also must be overridden.
  "gadgets.uri.js.host": "${Cur['defaultShindigTestHost']}",
  "gadgets.uri.js.path": "%context%/module/shindig/gadgets/js",

  // Default concat Uri config; used for testing.
  //"gadgets.uri.concat.host" : "${Cur['defaultShindigProxyConcatAuthority']}",
  "gadgets.uri.concat.host" : "%hostAndPort%",
  "gadgets.uri.concat.path" : "%context%/module/shindig/gadgets/concat",
  "gadgets.uri.concat.js.splitToken" : "false",

  // Default proxy Uri config; used for testing.
  //"gadgets.uri.proxy.host" : "${Cur['defaultShindigProxyConcatAuthority']}",
  "gadgets.uri.proxy.host" : "%hostAndPort%",
  "gadgets.uri.proxy.path" : "%context%/module/shindig/gadgets/proxy",

  // This is not used, you should check the config.property file to assign the
  // key. "gadgets.securityTokenKeyFile": "/file.txt",

  // This config data will be passed down to javascript. Please
  // configure your object using the feature name rather than
  // the javascript name.

  // Only configuration for required features will be used.
  // See individual feature.xml files for configuration details.
  "gadgets.features": {
    "core.io": {
      // Note: /proxy is an open proxy. Be careful how you expose this!
      "proxyUrl": "http://%host%%context%/module/shindig/gadgets/proxy?container=default&refresh=%refresh%&url=%url%%rewriteMime%",
      "jsonProxyUrl":
        "http://%host%%context%/module/shindig/gadgets/makeRequest"
    },
    "views": {
      "profile": {
        "isOnlyVisible": false,
        "urlTemplate": "http://localhost:8098%context%/module/shindig/gadgets/profile?{var}"
      },
      "home": {
        "isOnlyVisible": false,
        "urlTemplate": "http://localhost:8098%context%/module/shindig/gadgets/home?{var}",
        "aliases": ["DASHBOARD", "default"]
      },
      "canvas": {
        "isOnlyVisible": true,
        "urlTemplate": "http://localhost:8098%context%/module/shindig/gadgets/canvas?{var}",
        "aliases": ["FULL_PAGE"]
      }
    },
    "rpc": {
      // Path to the relay file. Automatically appended to the parent
      /// parameter if it passes input validation and is not null.
      // This should never be on the same host in a production environment!
      // Only use this for TESTING!
      "parentRelayUrl":
        "%context%/module/gadgetcontainer/assets/rpc_relay.html",

      // If true, this will use the legacy ifpc wire format when making rpc
      // requests.
      "useLegacyProtocol": false
    },
    // Skin defaults
    "skins": {
      "properties": {
        "BG_COLOR": "",
        "BG_IMAGE": "",
        "BG_POSITION": "",
        "BG_REPEAT": "",
        "FONT_COLOR": "",
        "ANCHOR_COLOR": ""
      }
    },
    "opensocial": {
      // Path to fetch opensocial data from
      // Must be on the same domain as the gadget rendering server
      "path": "http://%host%%context%/module/shindig/social/rpc",
      // Path to issue invalidate calls
      "invalidatePath": "http://%host%%context%/module/shindig/gadgets/api/rpc",
      "domain": "shindig",
      "enableCaja": false,
      "supportedFields": {
        "person": [
          "id", {"name": ["familyName", "givenName", "unstructured"]},
        "thumbnailUrl", "profileUrl"
          ],
        "activity": ["id", "title"]
      }
    },
    "osapi.services": {
      // Specifying a binding to "container.listMethods" instructs osapi to
      // dynamicaly introspect the services provided by the container and delay
      // the gadget onLoad handler until that introspection is complete.
      // Alternatively a container can directly configure services here rather
      // than having them introspected. Simply list out the available servies
      // and omit "container.listMethods" to avoid the initialization delay
      // caused by gadgets.rpc E.g. "gadgets.rpc": ["activities.requestCreate",
      // "messages.requestSend", "requestShareApp", "requestPermission"]
      "gadgets.rpc": ["container.listMethods"]
    },
    "osapi": {
      // The endpoints to query for available JSONRPC/REST services
      "endPoints": [ "http://%host%%context%/module/shindig/social/rpc",
      "http://%host%%context%/module/shindig/gadgets/api/rpc" ]
    },
    "osml": {
      // OSML library resource.  Can be set to null or the empty string to
      // disable OSML for a container.
      "library": "config/OSML_library.xml"
    }
  }
}

