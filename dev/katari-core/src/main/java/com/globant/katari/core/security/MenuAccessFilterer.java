/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.core.web.MenuNode;

/**
 * This class is used to filter a list of menu nodes and only return the ones
 * that the user is allowed to click.
 *
 * {@link SecureUrlAccessHelper}.
 * @see SecureUrlAccessHelper
 * @author pablo.saavedra
 * @author ulises.bochio
 * @author jair.tabares
 */
public class MenuAccessFilterer {
  /**
   * The logger.
   */
  private static Logger log = LoggerFactory.getLogger(MenuAccessFilterer.class);

  /**
   * The {@link SecureUrlAccessHelper}.
   * It is never null.
   */
  private final SecureUrlAccessHelper urlAccessHelper;

  /**
   * The MenuAccessFilterer Constructor.
   *
   * @param theUrlAccessHelper validates if the current user can access to the
   * given menu url. It cannot be null.
   */
  public MenuAccessFilterer(final SecureUrlAccessHelper theUrlAccessHelper) {
    Validate.notNull(theUrlAccessHelper, "theUrlMacroHelper cannot be null.");
    urlAccessHelper = theUrlAccessHelper;
  }

  /** It returns a filtered list of menu nodes and the filter policy is defined
   * by the {@link SecureUrlAccessHelper}.
   *
   * To be added to the output list, the user must have access to the leaf
   * node, or must have access to at least one descendent of a non leaf node.
   *
   * @param nodes A list of nodes to be filtered.It cannot be null.
   *
   * @return A filtered list of menu nodes. If the user cannot access any menu
   * node, it returns an empty list. It never returns null.
   */
  public List<MenuNode> filterMenuNodes(final List<MenuNode> nodes) {
    log.trace("Entering filterMenuNodes()");
    Validate.notNull(nodes, "The List of Menu Nodes cannot be null");
    List<MenuNode> result = new ArrayList<MenuNode>();
    for (MenuNode node : nodes) {
      if (node.isLeaf()) {

        /* urlAccessHelper.canAccessUrl needs a url with a context path. But
         * when the url is absolute, this operation ignores the context path, so
         * we simply pass a dummy context path. This is a hack to avoid passing
         * the request object from the view layer.
         */
        final String url = "/dummy-ctx" + node.getLinkPath();

        if (urlAccessHelper.canAccessUrl(null, url)) {
          result.add(node);
        }

      } else {
        List<MenuNode> childNodes = node.getChildNodes();
        List<MenuNode> filteredNodes = filterMenuNodes(childNodes);
        if (!filteredNodes.isEmpty()) {
          result.add(node);
        }
      }
    }
    log.trace("Leaving filterMenuNodes()");
    return Collections.unmodifiableList(result);
  }
}

