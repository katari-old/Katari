/* vim: set ts=2 et sw=2 cindent fo=qroca: */

/** Base canvas social container.
 *
 * This library requires jquery 1.4.2 and shindig's rpc.js.
 *
 * It uses jQuery instead of $, so it is safe to use with jQuery.noConflict().
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */

/** All katari related js stuff will go in the katari 'namespace'.
 */
var katari = katari || {};

// TODO: make this configured in the server.
katari.debugMode = true;

/** All social related js stuff will go in the social 'namespace' under katari.
 */
katari.social = katari.social || {};

/** TODO Decide where should the console be defined. How does this work?
 */
katari.console = {
  log : function(objLog) {
    if(katari.debugMode) {
      try {
        console.log(objLog);
      } catch(e){
      }
    }
  }
};

/** The canvas configuration.
 *
 * This is an object with:
 *
 * host: the base location of the server.
 *
 * container: the url of the gadget iframe.
 *
 * relayFile: the url of the rpc_relay.html.
 *
 * TODO: Document the rest of the parameters.
 */

/* Initializes the canvasConfig element in katari.social. It is a closure to
 * avoid polluting the global namespace.
 */
(function() {

  var host = location.protocol + "//" + location.host;

  katari.social = {
    canvasConfig: {
      /** The base location of the server, up to the port number.
       */
      host: host,
      /** The url of the iframe that will contain the gadget.
       */
      container: host + "${baseweb}/module/shindig/gadgets/ifr",
      /** The rpc_relay.html location, used for rpc in older Gecko engines (ff
       * 2 and possibly others). See rpc.js for more info.
       */
      relayFile: host + "${baseweb}/module/gadgetcontainer/assets/rpc_relay.html",
      /** The prefix of id of the frame that contains the gadget.
       *
       * Gadgets iframes has an id of the form [applicationPrefix] +
       * [gadgetId]. 
       */
      applicationPrefix: "Application-",
      socialContainer: "default",
      defaultView: "home",
      synId: "0"
    }
  };

})();

/** Constructor for GadgetInstances.
 *
 * TODO: decide if it is better to receive a gagdetSpec. Beware that the
 * gadgetSpec will be held by reference.
 *
 * Takes a gadget specification object.
 *
 * @param sId The gadget id. It is used to identify the gadget in rpc/rest
 * requests.
 *
 * @param sUrl The url of the gadget specification xml.
 *
 * @param sSecurityToken A token that identifies the user and the gadget to the
 * rpc/rest services.
 *
 * @param sViewer The id of the logged in user.
 *
 * @param sOwner The id of the owner of the gadget instance.
 *
 * @param sColumn The column where the gadget will be displayed.
 *
 * @param sOrder The position of the gadget in the column.
 */
katari.social.GadgetInstance = function(sId, sUrl, sSecurityToken, sViewer,
    sOwner, sColumn, sOrder) {

  /** The id of the gadget instance, usually as retrieved from the backend.
   */
  var id = sId;

  /** The id of the html iframe that contains this gadget.
   *
   * This id is also used as the rpc torken for gadget to container
   * communication. It is the same as the iframe id to implement a workaround
   * in ie6/ie7 where the rezise event does not receive the iframe id.
   */
  var applicationId = katari.social.canvasConfig.applicationPrefix + sId;

  /** The url used to render the gadget, that is, the value of the src
   * attribute of the iframe that contains the gadget.
   */
  this.getGadgetUrl = function() {
    var url = [];
    url.push(katari.social.canvasConfig.container);
    if (katari.debugMode) {
      url.push("?debug=true");
      url.push("&nocache=1&");
    } else {
      url.push("?");
    }
    url.push("url=", sUrl);
    url.push("#rpctoken=", applicationId);
    url.push("&st=", sSecurityToken);
    url.push("&mid=", id);
    url.push("&synd=", katari.social.canvasConfig.synId);
    url.push("&container=", katari.social.canvasConfig.socialContainer);
    url.push("&viewer=", sViewer);
    url.push("&owner=", sOwner);
    url.push("&aid=", id);
    url.push("&parent=", katari.social.canvasConfig.host);
    url.push("&view=", katari.social.canvasConfig.defaultView);
    return url.join('');
  };

  /** Returns the id of the gadget instance, usually as retrieved from the
   * backend.
   */
  this.getId = function() {
    return id;
  }

  /** Returns the id of the html iframe that contains this gadget.
   */
  this.getApplicationId = function() {
    return applicationId;
  }

  /** The column where this gadget will be displayed.
   */
  this.column = sColumn;

  /** The position of the gadget in the column.
   */
  this.order = sOrder;
};

/** Create a new gadget group.
 *
 * @param {String} sContainer id of the container, usually a div, that will
 * contain all the gadgets in the group.
 */
katari.social.GadgetGroup = function(sContainer) {

  /** The id of the html element (usually a div) that will contain all the
   * gadgets in this group.
   */
  var container = sContainer;

  /** A list of GadgetInstance.
   */
  this.gadgets = [];

  /** A list of divs, one for each column.
   */
  this.columns = [];

  /** Add a gadget instance.
   *
   * @param {Object} objGadgetInstance
   *
   * return this.
   */
  this.addGadget = function(objGadgetInstance) {
    this.gadgets.push(objGadgetInstance);
    return this;
  };

  /** Adds a gadgets to the group from the groupSpec.
   *
   * @param groupSpec
   */
  this.addGadgetsFromJson = function(groupSpec) {
    var that = this;
    // Create the empty columns.
    for (var i = 0; i < groupSpec.numberOfColumns; i++) {
      this.columns[i] = jQuery('<div class="canvasColumn">');
    }
    // And create all the gadgets.
    jQuery.each(groupSpec.gadgets, function(i, gadgetSpec) {
      that.addGadget(new katari.social.GadgetInstance(gadgetSpec.id,
          gadgetSpec.url, gadgetSpec.securityToken, groupSpec.viewerId,
          groupSpec.ownerId, gadgetSpec.column, gadgetSpec.order));
    });
    return this;
  };

  /** Renders the group in the container, the html element with the id provided
   * to the gadget group.
   *
   * The structure of the rendered html is:
   *
   * (container) -> {n times} div(class='canvasColumn) -> {n times}
   * div(id='header_N') + div -> iframe(id='Application_N)
   *
   * The container has a final clear div after all the columns.
   *
   * TODO Review the structure.
   */
  this.render = function() {
    // sort the gadgets.
    this.gadgets.sort(
      function(a, b) {
        if(a.column === b.column) {
          return a.order - b.order;
        } else {
          return a.column - b.column;
        }
      }
    );

    katari.console.log(this.gadgets);

    // add the gadgets
    var that = this;
    jQuery.each(this.gadgets, function(i, gadget) {
      var iframe = jQuery("<iframe>");
      iframe.attr("src", gadget.getGadgetUrl());
      iframe.attr("id", gadget.getApplicationId());
      iframe.attr("name", gadget.getApplicationId());
      iframe.attr("frameborder", 0);

      var titleDiv = jQuery("<div></div>");
      titleDiv.attr("id", "header_" + gadget.getApplicationId());
      var localDiv = jQuery("<div></div>");

      localDiv.append(iframe);

      that.columns[gadget.column].append(titleDiv);
      that.columns[gadget.column].append(localDiv);
    });

    // Adds all the columns to the provided container.
    var containerDiv = jQuery('#' + container);
    jQuery.each(this.columns, function(index, column) {
      containerDiv.append(column);
    });
    // Add a clear div to the container.
    containerDiv.append("<div style='clear:both;'><!-- empty div --></div>");

    // Now, we iterate over all gagdets (again) to initialize the rpc
    // mechanism.
    if (window.gadgets) {
      jQuery.each(this.gadgets, function(i, gadget) {
        gadgets.rpc.setupReceiver(gadget.getApplicationId());
      });
    }
  };
};

/** OpenSocial container implementation.
 */
katari.social.Container = function() {

  /** The maximum height for each gadged.
   *
   * TODO: this should be configured somewhere else.
   */
  this.maxHeight = 4096;

  gadgets.rpc.register('resize_iframe', this.setHeight);
  gadgets.rpc.register('set_pref', this.setUserPref);
  gadgets.rpc.register('set_title', this.setTitle);
  gadgets.rpc.register('requestNavigateTo', this.requestNavigateTo);

};

/** Called by the gadget to request a height change.
 *
 * Sets the new height if it lower than maxHeight, otherwise it sets it to
 * maxHeight.
 *
 * @param height The new requested height.
 */
katari.social.Container.prototype.setHeight = function(height) {
  if (height > gadgets.container.maxHeight) {
    height = gadgets.container.maxHeight;
  }

  // HACK: in ie 6 and 7, this.f is empty, so I use this.t (rpcToken) as the
  // iframe Id.
  var elementId = this.f || this.t;

  var element = document.getElementById(elementId);
  if (element) {
    element.height = height;
  }

  // ie6 hack (from here:
  // http://stackoverflow.com/questions/33837/ie-css-bug-how-do-i-maintain-a-positionabsolute-when-dynamic-javascript-conten)
  //
  // The footer is not positioned correctly under ie6 without this. Seems to
  // have no visible effect on other browsers.
  var content = document.getElementById('footer');
  content.style.zoom = '1';
  content.style.zoom = '';
};

katari.social.Container.prototype.setTitle = function(title) {
  var elementId = this.f || this.t;
  var element = jQuery('#header_' + elementId);
  if (element !== undefined) {
    element.text(title.replace(/&/g, '&amp;').replace(/</g, '&lt;'));
  }
};

katari.social.Container.prototype._parseIframeUrl = function(url) {
  var ret = {};
  var hashParams = url.replace(/#.*$/, '').split('&');
  var param = '';
  var key = '';
  var val = '';
  for (i = 0; i < hashParams.length; i++) {
    param = hashParams[i];
    key = param.substr(0, param.indexOf('='));
    val = param.substr(param.indexOf('=') + 1);
    ret[key] = val;
  }
  return ret;
};

katari.social.Container.prototype.setUserPref = function(editToken, name, value) {
  /*
  if (jQuery('#' + this.f) != undefined) {
    var params = gadgets.container._parseIframeUrl(jQuery('#' + this.f).src);
    new Ajax.Request('/prefs/set', {
      method : 'get',
      parameters : {
        name : name,
        value : value,
        st : params.st
      }
    });
  }
  */
};

katari.social.Container.prototype._getUrlForView = function(view, person, app, mod) {
  if (view === 'home') {
    return '/home';
  } else if (view === 'profile') {
    return '/profile/' + person;
  } else if (view === 'canvas') {
    return '/profile/application/' + person + '/' + app + '/' + mod;
  } else {
    return null;
  }
};

katari.social.Container.prototype.requestNavigateTo = function(view, opt_params) {
  if (jQuery('#' + this.f) !== undefined) {
    var params = gadgets.container._parseIframeUrl(jQuery('#' + this.f).src);
    var url = gadgets.container._getUrlForView(view, params.owner, params.aid,
        params.mid);
    if (opt_params) {
      var paramStr = Object.toJSON(opt_params);
      if (paramStr.length > 0) {
        url += '?appParams=' + encodeURIComponent(paramStr);
      }
    }
    if (url && document.location.href.indexOf(url) == -1) {
      document.location.href = url;
    }
  }
};

jQuery(document).ready(function() {
  // Override shindig container configuration.
  gadgets.container = new katari.social.Container();
});

/*
jQuery.extend({
  getUrlVars: function(){
    var vars = [];
    var hash = [];
    var hashes = window.location.href.slice(
        window.location.href.indexOf('?') + 1).split('&');

    for (var i = 0; i < hashes.length; i++) {
      hash = hashes[i].split('=');
      vars.push(hash[0]);
      vars[hash[0]] = hash[1];
    }
    return vars;
  },
  getUrlVar: function(name){
    return jQuery.getUrlVars()[name];
  },
  getWindowLocation : function() {
   return window.location.href;
  }
});
*/

