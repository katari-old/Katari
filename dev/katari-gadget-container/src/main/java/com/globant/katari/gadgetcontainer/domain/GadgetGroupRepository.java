/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/** Repository for the gadget groups.
 *
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 */
public class GadgetGroupRepository extends HibernateDaoSupport {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(GadgetGroupRepository.class);

  /** Find the requested gadget group by name and user.
   *
   * @param userId the user. Can not be null.
   * @param name the page name. Can not be empty.
   * @return the gadget group found or null.
   */
  @SuppressWarnings("unchecked")
  public GadgetGroup findGadgetGroup(final long userId, final String name) {
    Validate.notEmpty(name, "pageName can not be empty");
    Validate.notNull(userId, "canvasUser can not be null");
    log.debug("searching page for: " + userId + " with name:" + name);

    List<GadgetGroup> groups = getHibernateTemplate().find(
        "from GadgetGroup where name = ? and owner.id = ?",
        new Object[]{name, userId});

    if(groups.isEmpty()) {
      return null;
    }

    GadgetGroup group = groups.get(0);
    log.debug("page found!");

    return group;
  }

  /** Saves the given group in the db.
   *
   * @param gadgetGroup {@link GadgetGroup} the group to store. Can not be
   * null.
   */
  public void save(final GadgetGroup gadgetGroup) {
    log.debug("storing new GadgetGroup");
    Validate.notNull(gadgetGroup, "page can not be null");
    getHibernateTemplate().saveOrUpdate(gadgetGroup);
  }
}
