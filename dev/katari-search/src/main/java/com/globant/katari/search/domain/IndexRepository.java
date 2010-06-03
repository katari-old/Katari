/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain;

import org.apache.commons.lang.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import org.compass.core.Compass;
// import org.compass.core.CompassSession;
// import org.compass.core.CompassTransaction;
import org.compass.core.CompassHit;
import org.compass.core.spi.InternalCompass;
import org.compass.core.support.search.CompassSearchResults;
import org.compass.core.support.search.CompassSearchCommand;
import org.compass.core.support.search.CompassSearchHelper;
import org.compass.gps.CompassGps;

import com.globant.katari.core.security.SecureUrlAccessHelper;

/** Provides the mechanism to find objects through a reverse text index.
 *
 * This is the main entry point to find objects indexed through compass.
 *
 * @author nira.amit@globant.com
 *
 * TODO Document the syntax of the query.
 */
public class IndexRepository {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(IndexRepository.class);

  /** The GPS device for the compass object.
   *
   * It is never null.
   */
  private CompassGps compassGps;

  /** A search-helper object for the compass search.
   *
   * It is never null.
   */
  private CompassSearchHelper searchHelper;

  /** The compass object that the work will be delegated to.
   *
   * It is never null.
   */
  private Compass compass;

  /** The adapters supplied by the different modules for searchable entities.
   *
   * There should be one for every searchable entity registered with compass.
   * It is never null.
   */
  private List<SearchAdapter> adapters;

  /** The {@link SecureUrlAccessHelper} used to check if the user can see the
   * object to search for.
   *
   * It is never null.
   */
  private final SecureUrlAccessHelper urlAccessHelper;

  /** Constructor, builds an index repository.
   *
   * @param gps the compass gps implementation, it cannot be null.
   *
   * @param helper the compass search helper, it cannot be null.
   *
   * @param theCompass the compass session factory, it cannot be null.
   *
   * @param theAdapters the adapters that adapt the compass result to the
   * SearchResultElement. It cannot be null.
   *
   * @param theUrlAccessHelper validates if the current user can access to the
   * given url. It cannot be null.
   */
  public IndexRepository(final CompassGps gps,
      final CompassSearchHelper helper, final Compass theCompass,
      final List<SearchAdapter> theAdapters, final
      SecureUrlAccessHelper theUrlAccessHelper) {
    Validate.notNull(gps, "The compass gps implementation cannot be null.");
    Validate.notNull(helper, "The compass helper cannot be null.");
    Validate.notNull(theCompass,
        "The compass session factory cannot be null.");
    Validate.notNull(theAdapters, "The adapters cannot be null.");
    Validate.notNull(theUrlAccessHelper,
        "The url access helper cannot be null.");

    compassGps = gps;
    searchHelper = helper;
    compass = theCompass;
    adapters = theAdapters;
    urlAccessHelper = theUrlAccessHelper;
  }

  /** Recreate the index using the database information.
   *
   * The reindex process first deletes the index and then recreates it. So
   * reindexing cannot be done concurrently with queries or incremental
   * updates. This is a potentially very expensive operation.
   */
  public void reIndex() {
    log.trace("Entering reIndex");
    if (!compassGps.isRunning()) {
      throw new RuntimeException("Compass should have been started.");
    }
    compassGps.index();
    log.trace("Leaving reIndex");
  }

  /** Search in the compass index.
   *
   * @param query Query entered by the user. It cannot be null.
   *
   * @param pageNumber Page you want to get. 0 is the first page.
   *
   * @return the result of the query in the index. It never returns null.
   */
  public SearchResult find(final String query, final int pageNumber) {

    log.trace("Entering find");

    if (query.trim().length() == 0) {
      log.trace("Leaving find with no results");
      return new SearchResult();
    }

    log.debug("Building query ...");
    StringBuilder viewUrls = new StringBuilder();
    for (SearchAdapter adapter: adapters) {
      log.debug("Considering adapter for class {}, checking url {}",
          adapter.getAdaptedClass().toString(), adapter.getViewUrl());
      if (urlAccessHelper.canAccessUrl(null, adapter.getViewUrl())) {
        log.debug("User can access url {}", adapter.getViewUrl());
        if (viewUrls.length() != 0) {
          viewUrls.append(" OR ");
        }
        viewUrls.append("(alias:");

        String alias = ((InternalCompass)compass).getMapping()
          .findRootMappingByClass(adapter.getAdaptedClass()).getAlias();

        viewUrls.append(alias);
        viewUrls.append(")");
      }
    }

    if (viewUrls.length() == 0) {
      // There is nothing I have access to.
      log.trace("Leaving find with no results");
      return new SearchResult();
    }

    // TODO: Check if the parens are balanced in query !!!
    String refinedQuery = "(" + query + ") AND (" + viewUrls.toString() + ")";

    log.debug("Executing query {}.", refinedQuery);

    CompassSearchCommand searchCommand;
    searchCommand = new CompassSearchCommand(refinedQuery);
    searchCommand.setPage(pageNumber);
    CompassSearchResults compassResults = searchHelper.search(searchCommand);

    SearchResult result;

    /* Not documented in compass, but apparently, this is the correct way to
     * check if the result was empty. This was apparent after watching the
     * source for CompassSearchHelper.
     */
    if (compassResults.getTotalHits() != 0) {
      result = new SearchResult(compassResults.getPages().length,
          convert(compassResults));
      log.trace("Leaving find with {} hits", compassResults.getTotalHits());
      return result;
    } else {
      result = new SearchResult();
      log.trace("Leaving find with no results");
      return new SearchResult();
    }
  }

//  /** Adds a new object to the index.  * * @param r object to be added.  */
//  public void addObject(final Object r) { CompassSession session =
//  compass.openSession(); CompassTransaction transaction =
//  session.beginLocalTransaction(); session.save(r); transaction.commit();
//  session.close(); }
//
//  /** Adds a new Collection of objects to the index.
//   *
//   * @param resources collection of objects to be added
//   */
//  public void addObject(final Collection< ? > resources) {
//    CompassSession session = compass.openSession();
//    CompassTransaction transaction = session.beginLocalTransaction();
//    for (Object r : resources) {
//      session.save(r);
//    }
//    transaction.commit();
//    session.close();
//  }
//
//  /** Deletes a object from the index.
//   *
//   * @param r object to be deleted
//   *
//   */
//  public void removeObject(final Object r) {
//    CompassSession session = compass.openSession();
//    CompassTransaction transaction = session.beginLocalTransaction();
//    session.delete(r);
//    transaction.commit();
//    session.close();
//  }
//
//  /** Deletes a Collection of objects from the index.
//   *
//   * @param resources objects to be deleted
//   */
//  public void removeObject(final Collection< ? > resources) {
//    CompassSession session = compass.openSession();
//    CompassTransaction transaction = session.beginLocalTransaction();
//    for (Object r : resources) {
//      session.delete(r);
//    }
//    transaction.commit();
//    session.close();
//  }
//
//  /** Gets the object from the index.
//   *
//   * @param alias the alias of the class
//   *
//   * @param id the id in the class
//   *
//   * @return the object specified
//   */
//  public Object getObject(final String alias, final String id) {
//    CompassSession session = compass.openSession();
//    CompassTransaction transaction = session.beginLocalTransaction();
//    Object o = session.load(alias, id);
//    transaction.commit();
//    session.close();
//    return o;
//  }

  /* I finally decided that the creation of the SearchResultElement wrappers
   * will be in this class. It looked like it belonged here.
   */

  /** Convert the results to a List of SearchResultElement objects.
   *
   * @param c the compass search result, it cannot be null.
   *
   * @return a collection of google like results. It never returns null.
   */
  private List<SearchResultElement> convert(final CompassSearchResults c) {
    log.trace("Entering convert");

    List<SearchResultElement> results = new ArrayList<SearchResultElement>();
    for (CompassHit hit : c.getHits()) {
      results.add(convert(hit.getData(), hit.getScore()));
    }

    log.trace("Leaving convert");
    return results;
  }

  /** Convert the object to a SearchResultElement object.
   *
   * @param object the result you want to convert. It cannet be null.
   *
   * @param score the score for this result.
   *
   * @return a SearchResultElement, never returns null.
   *
   * TODO What happens if there are no adapters? Now it is throwing an
   * exception.
   */
  private SearchResultElement convert(final Object object, final float score) {
    Validate.notNull(object, "The object cannot be null");

    for (SearchAdapter adapter : adapters) {
      if (object.getClass().equals(adapter.getAdaptedClass())) {
        return adapter.convert(object, score);
      }
    }
    throw new RuntimeException("Could not convert object.");
  }
}

