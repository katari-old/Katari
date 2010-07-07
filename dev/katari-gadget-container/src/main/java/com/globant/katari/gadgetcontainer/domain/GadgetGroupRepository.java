/**
 * 
 */
package com.globant.katari.gadgetcontainer.domain;


import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Repository for operations related with the canvas page.
 * 
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class GadgetGroupRepository extends HibernateDaoSupport {
  
  /** The class logger.
   */
  private static Log log = LogFactory.getLog(GadgetGroupRepository.class);
  
  /** Find the requested page by his name and related user.
   * 
   * @param canvasUser {@link CanvasUser} the user. Can not be null.
   * @param pageName {@link String} the page name. Can not be empty.
   * @return {@link GadgetGroup} the first page found or null.
   */
  @SuppressWarnings("unchecked")
  public GadgetGroup findPage(final String canvasUser, final String pageName) {
    Validate.notEmpty(pageName, "pageName can not be empty");
    Validate.notNull(canvasUser, "canvasUser can not be null");
    log.debug("searching page for: " + canvasUser + " with name:" + pageName);
    
    List<GadgetGroup> pages = getHibernateTemplate().find(
        "from GadgetGroup where name=? and canvasUser=?",
        new String[]{ pageName, canvasUser} );
    
    if(pages.isEmpty()) {
      return null;
    }
    
    GadgetGroup page = pages.get(0);
    if(page != null) {
      log.debug("page found!");
    }
    
    return page;
  }
  
  /**Store the given canvas page in the db.
   * 
   * @param page {@link GadgetGroup} the page to store. Can not be null.
   */
  public void savePage(final GadgetGroup page) {
    log.debug("storing new GadgetGroup");
    Validate.notNull(page, "page can not be null");
    getHibernateTemplate().saveOrUpdate(page);
  }
}
