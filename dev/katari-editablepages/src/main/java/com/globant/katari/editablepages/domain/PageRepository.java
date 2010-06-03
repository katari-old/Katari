/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.domain;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/** This class is responsible for managing the persistence of pages.
 */
public class PageRepository extends HibernateDaoSupport {

  /** Saves a new page or updates an existing page to the database.
   *
   * @param siteName The name of the site of the page. It cannot be null.
   *
   * @param page The page to save. It cannot be null.
   */
  public void save(final String siteName, final Page page) {
    Validate.notNull(page, "The page cannot be null");
    Validate.notNull(siteName, "The site cannot be null");
    page.setSiteName(siteName);
    getHibernateTemplate().saveOrUpdate(page);
  }

  /** Removes the specified page from the database.
   *
   * @param page The page to remove. It cannot be null.
   */
  public void remove(final Page page) {
    Validate.notNull(page, "The page cannot be null");
    getHibernateTemplate().delete(page);
  }

  /** Finds a page by name and a site.
   *
   * @param siteName The site name to search for. It cannot be null.
   *
   * @param name The name of the page to search for. It cannot be null.
   *
   * @return Returns the page with the specified name and site or null if no
   *         such page exists.
   */
  @SuppressWarnings("unchecked")
  public Page findPageByName(final String siteName, final String name) {
    Validate.notNull(siteName, "The site name cannot be null.");
    Validate.notNull(name, "The page name cannot be null.");
    List<Page> pages = getHibernateTemplate().find(
        "from Page where name = ? and site_name = ?",
        new Object[] {name, siteName });
    if (pages.isEmpty()) {
      return null;
    } else {
      return pages.get(0);
    }
  }

  /** Finds the page with the specified id.
   *
   * @param id The id of the page to search for.
   *
   * @return Returns the page with the specified id, or null if no such page
   * exists.
   */
  public Page findPage(final long id) {
    Page page = (Page) getHibernateTemplate().get(Page.class, id);
    return page;
  }
}

