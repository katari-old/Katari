/**
 * IG Expansion top-level namespace.
 * @namespace
 */
var katari = katari || {};

katari.jsmodule = katari.jsmodule || {};

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
 * TODO (carolina.becerra) This still depends on jQuery, needed to trigger
 * load() on its "ready" event, using jQuery,ajax and browser detection on
 * includeScript(). Let's remove it.
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
      throw new Error('katari.jsmodule.Loader.include: attempted to include'
          + ' empty or undefined lists of files.');
    }

    jsIncludes = includes.js;

    includeSequentially = function () {
      if (jsIncludes.length > 0) {
        var filePath = jsIncludes.shift();

        // TODO (carolina.becerra) Remove this hardcoded filter.
        if (filePath !==
            '/com/globant/igexpansion/smp/style/ui/lib/jquery-1.6.2-min.js') {
          includeScript(baseweb + '/module/jsmodule' + filePath,
              includeSequentially);
        } else {
          includeSequentially();
        }
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
   * @memberOIGE.Loader
   * @private
   */
  var includeScript = function (url, callback) {
    var script = document.createElement("SCRIPT");
    var interval;

    script.src = url;
    script.type = "text/javascript";

    // Was a callback requested
    if (typeof callback === "function") {
      // Opera supports both onload and onreadystatechange.
      if (!jQuery.browser.Opera) {
        // The IE way is using the onreadystatechange event.
        script.onreadystatechange = function () {
          if (script.readyState === 'loaded' ||
              script.readyState === 'complete') {
            callback(url);
          }
        };
      }

      // Gecko-compliant browsers supports onload.
      script.onload = function () {
        callback(url);
      };

      // Safari 2 doesn't support either onload or readystate, creating a
      // timer is the only way to do this in Safari 2.
      if (jQuery.browser.WebKit && navigator.userAgent.match(/Version\/2/)) {
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

  return {
    /**
     * Adds a file to the list of files to be imported.
     *
     * @param {String} pathToFile The path to the file to import. Cannot be null
     *     or undefined.
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
      var endpoint = baseweb + '/module/jsmodule/com/globant/katari'
          + '/jsmodule/action/resolveDependencies.do';

      if (hasLoaded) {
        throw new Error('katari.jsmodule.Loader.load: method already' +
            ' invoked, cannot make more than one load.');
      } else {
        hasLoaded = true;

        if (toInclude.length) {
          jQuery.ajax(endpoint, {
            data : {
              files : toInclude
            },
            success : include,
            dataType : 'json',
            traditional : true,
            type : 'POST'
          });
        }
      }
    },

    /**
     * Takes subscriptions to the "ready" event of the Loader.
     *
     *
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

