/**
 * Katari top-level namespace.
 * @namespace
 */
var katari = katari || {};

katari.jsmodule = katari.jsmodule || {};

// Setup katari.jsmodule.onDomReady() to allow cross-browser suscription to DOM
// ready event.
(function () {
  var domReadyCallbacks = [];
  var domIsReady = false;

  var ready = function () {
    var i, j;

    // Defer if body doesn't exist yet, for IE's sake.
    if (!document.body) {
      return setTimeout(ready, 1);
    }

    domIsReady = true;

    for (i = 0, j = domReadyCallbacks.length; i < j; i++) {
      domReadyCallbacks.splice(i, 1)[0]();
    }
  };

  var domContentLoaded;
  // Define domContentLoaded (cleanup function for the dom ready method)
  if (document.addEventListener) {
    domContentLoaded = function () {
      document.removeEventListener("DOMContentLoaded", domContentLoaded, false);
      ready();
    };
  } else if (document.attachEvent) {
    domContentLoaded = function () {
      // Make sure body exists, at least, in case IE gets a little overzealous
      if (document.readyState === "complete") {
        document.detachEvent("onreadystatechange", domContentLoaded);
        ready();
      }
    };
  }

  // The DOM ready check for Internet Explorer
  var doScrollCheck = function () {
    if (!domIsReady) {
      try {
        // If IE is used, use the trick by Diego Perini
        // http://javascript.nwbox.com/IEContentLoaded/
        document.documentElement.doScroll("left");
      } catch (e) {
        setTimeout(doScrollCheck, 1);
        return;
      }

      // Execute any waiting functions
      ready();
    }
  };

  var bindReady = function () {
    // If there are subscribers, binding has already happened; exit.
    if (domReadyCallbacks.length > 0) {
      return;
    }

    if (document.readyState === "complete") {
      // Handle it asynchronously to allow scripts the chance to delay ready
      setTimeout(ready, 1);
    } else {
      // Mozilla, Opera and webkit nightlies currently support this event
      if (document.addEventListener) {
        // Use the handy event callback
        document.addEventListener("DOMContentLoaded", domContentLoaded, false);

        // A fallback to window.onload, that will always work
        window.addEventListener("load", function () {ready();}, false);
      // If IE event model is used
      } else if (document.attachEvent) {
        // Ensure firing before onload, maybe late but safe also for iframes.
        document.attachEvent("onreadystatechange", domContentLoaded);

        // A fallback to window.onload, that will always work
        window.attachEvent("onload", ready);

        // If IE and not a frame
        // continually check to see if the document is ready
        var toplevel = false;

        try {
          toplevel = window.frameElement === null;
        } catch (e) {}

        if (document.documentElement.doScroll && toplevel) {
          doScrollCheck();
        }
      }
    }
  };

  katari.jsmodule.onDomReady = function (callback) {
    bindReady();
    domReadyCallbacks.push(callback);
  };
})();

/** Contains the current browser information. */
var Browser = (function () {
  var ua = navigator.userAgent;
  var isOpera;
  isOpera = Object.prototype.toString.call(window.opera) == '[object Opera]';

  var currentBrowser = {
    IE:             !!window.attachEvent && !isOpera,
    Opera:          isOpera,
    WebKit:         ua.indexOf('AppleWebKit/') > -1,
    Gecko:          ua.indexOf('Gecko') > -1 && ua.indexOf('KHTML') === -1,
    MobileSafari:   /Apple.*Mobile/.test(ua)
  };

  return currentBrowser;
})();

/**
 * Loads JS files, including their dependencies.
 *
 * Dependencies will be browsed by looking for a corresponding ".dep" file
 * where they should be specified (e.g., for a file called "file.js",
 * "file.dep.js" will be looked for in the same directory). If no dependencies
 * file is found, it's assumed that there are no dependencies. On
 * <code>load</code> includes a bundle of all requested files in no-debug
 * mode, or each separate file uncompressed in debug mode.
 *
 * @param {String} baseweb The base path of the application. Cannot be null or
 *     undefined.
 */
katari.jsmodule.Loader = function (baseweb) {
  /**
   * Whether all preconditions are met for constructing this object. Execution
   * will throw an error if preconditions are not met.
   *
   * @type Boolean
   * @memberOf katari.jsmodule.Loader
   * @private
   */
  var checkPreconditions = (function () {
    if (baseweb === null || baseweb === undefined) {
      throw new Error("Can't construct Loader: baseweb cannot be null or " +
          "undefined.");
    }

    return true;
  }());

  /**
   * Flag to indicate whether <code>load()</code> has already been called.
   *
   * @type Boolean
   * @memberOf katari.jsmodule.Loader
   * @private
   */
  var hasLoaded = false;

  /**
   * Flag to indicate whether the <code>ready</code> event has already been
   * triggered.
   *
   * @type Boolean
   * @memberOf katari.jsmodule.Loader
   * @private
   */
  var hasFinished = false;

  /**
   * List of JS files that are to be included at the call of
   * <code>load()</code>.
   *
   * @type String[]
   * @memberOf katari.jsmodule.Loader
   * @private
   */
  var toInclude = [];

  /**
   * List of subscribers to the Loader's "ready" event.
   *
   * @type Function[]
   * @memberOf katari.jsmodule.Loader
   * @private
   */
  var subscribers = [];

  /**
   * Includes in the current document all the JS files received in the
   * parameter object.
   *
   * @param {String[]} includes List of JS files to include. Cannot be null or
   *     undefined, nor empty.
   *
   * @memberOf katari.jsmodule.Loader
   * @private
   */
  var include = function (includes) {
    var jsIncludes;
    var includeSequentially;
    // Check preconditions: includes cannot be null or undefined, nor empty.
    if (!includes || !includes.js || includes.js.length === 0) {
      throw new Error('katari.jsmodule.Loader.include: attempted to include' +
          ' empty or undefined lists of files.');
    }

    jsIncludes = includes.js;

    includeSequentially = function () {
      if (jsIncludes.length > 0) {
        var filePath = jsIncludes.shift();

        includeScript(baseweb + '/module/jsmodule' + filePath,
            includeSequentially);
      } else {
        onReady();
      }
    };

    includeSequentially();
  };

  /**
   * Cross-browser lazy script including function.
   *
   * @param {String} url Url to include. It cannnot be null or empty.
   * @param {Function} [callback] Function called after the script is
   *     completely loaded. It can be null.
   * @memberOf katari.jsmodule.Loader
   * @private
   */
  var includeScript = function (url, callback) {
    var script = document.createElement("SCRIPT");
    var interval;

    script.src = url;
    script.type = "text/javascript";

    // Was a callback requested
    if (typeof callback === "function") {
      // Some browsers support onreadystatechange (Opera, IE).
      if (script.onreadystatechange !== undefined) {
        script.onreadystatechange = function () {
          if (script.readyState === 'loaded' ||
              script.readyState === 'complete') {
            callback(url);
          }
        };

      // Others support onload (Gecko-compliant)
      } else if (Browser.Gecko || script.onload !== undefined) {
        script.onload = function () {
          callback(url);
        };

      // The rest will have to make do with a timer.
      } else {
        interval = setInterval(function () {
          if (/loaded|complete/.test(document.readyState)) {
            clearInterval(interval);
            callback(url);
          }
        }, 10);
      }
    }

    document.getElementsByTagName("head")[0].appendChild(script);
  };

  /**
   * Triggers all subscribers to the Loader's "ready" event.
   *
   * @memberOf katari.jsmodule.Loader
   * @private
   */
  var onReady = function () {
    var i;
    hasFinished = true;

    for (i = 0; i < subscribers.length; i++) {
      subscribers[i]();
    }
  };

  /**
   * Make an ajax call to post data with a success callback.
   *
   * @param {String} endpoint The url to post to.
   * @param {String} params The data to post in the following format
   *     "key=value&key2=value2(...)".
   * @param {Function} successCallback Function to execute on successful return
   *     of the call. Gets passed the parsed response.
   */
  var post = function (endpoint, params, successCallback) {
    var xmlhttp;
    if (window.XMLHttpRequest) {
      // code for IE7+, Firefox, Chrome, Opera, Safari
      xmlhttp = new XMLHttpRequest();
    } else {
      // code for IE6, IE5
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }

    xmlhttp.open("POST", endpoint, true);
    xmlhttp.setRequestHeader("Content-type",
        "application/x-www-form-urlencoded");
    xmlhttp.onreadystatechange = function () {
      if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
        if (xmlhttp.responseXML) {
          xmlresponse = xmlhttp.responseXML;
        } else if (xmlhttp.responseText) {
          xmlresponse = xmlhttp.responseText;
        }
        try {
          successCallback(JSON.parse(xmlhttp.responseXML));
        } catch (ex) {
          successCallback(JSON.parse(xmlhttp.responseText));
        }
      }
    };
    xmlhttp.send(params);
  };

  return {
    /**
     * Adds a file to the list of files to be imported.
     *
     * @param {String} pathToFile The path to the file to import. Cannot be
     * null nor undefined.
     *
     * @memberOf katari.jsmodule.Loader
     * @public
     */
    importFile : function (pathToFile) {
      // Checking preconditions.
      if (typeof pathToFile != 'string') {
        throw new Error('Wrong type of parameter at katari.jsmodule.Loader.' +
            'importFile, expected "string".');
      }

      // Not avoiding duplicates.
      toInclude.push(pathToFile);
    },

    /**
     * Includes all the previously imported files and their dependencies.
     *
     * @memberOf katari.jsmodule.Loader
     * @public
     */
    load : function () {
      var endpoint = baseweb + '/module/jsmodule/com/globant/katari' +
          '/jsmodule/action/resolveDependencies.do';

      if (hasLoaded) {
        throw new Error('katari.jsmodule.Loader.load: method already invoked, ' +
            'cannot make more than one load.');
      } else {
        hasLoaded = true;

        if (toInclude.length) {
          var params = "";
          var i, j;
          for (i = 0, j = toInclude.length; i < j; i++) {
            params += '&files=' + toInclude[i];
          }

          params = params.replace('&', '');

          post(endpoint, params, include);
        }
      }
    },

    /**
     * Takes subscriptions to the "ready" event of the Loader.
     *
     * @param {Function} callback A function to be triggered on the event.
     *     Cannot be null or undefined.
     * @memberOf katari.jsmodule.Loader
     * @public
     */
    ready : function (callback) {
      if (typeof callback === 'function') {
        if (hasFinished) {
          callback();
        } else {
          subscribers.push(callback);
        }
      } else {
        throw new Error("Cannot subscribe to katari.jsmodule.Loader's ready:" +
            " provided callback is not a function.");
      }
    }
  };
};

