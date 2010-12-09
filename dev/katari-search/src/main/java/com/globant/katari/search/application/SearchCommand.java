/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.application;

import org.apache.commons.lang.Validate;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.katari.core.application.Command;

import com.globant.katari.search.domain.IndexRepository;
import com.globant.katari.search.domain.SearchResult;
import com.globant.katari.search.domain.SearchResultElement;

/** This command performs a search.
 *
 * @author nira.amit@globant.com
 */
public class SearchCommand implements Command<List<SearchResultElement>> {

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(SearchCommand.class);

  /** Compass index repository.
   *
   * It is never null.
   */
  private IndexRepository indexRepository;

  /** The search Query.
   *
   * It is never null.
   */
  private String query = "";

  /** The page number corresponding to the search result returned by the
   * command.
   */
  private int pageNumber;

  /** The total number of pages found that matches the query.
   */
  private int totalPages;

  /** Constructor, builds a search command.
   *
   * @param repository the index repository where this command delegates the
   * searches to. It cannot be null.
   */
  public SearchCommand(final IndexRepository repository) {
    Validate.notNull(repository, "The index repository cannot be null.");
    indexRepository = repository;
  }

  /** Perform the search.
   *
   * @return a collection of SearchResultElement that matched the query. It
   * never returns null.
   */
  public List<SearchResultElement> execute() {
    log.trace("Entering execute");
    SearchResult result = indexRepository.find(query, pageNumber);
    totalPages = result.getTotalPages();
    List<SearchResultElement> elements = result.getElements();
    log.trace("Leaving execute");
    return elements;
  }

  /** The search query as entered by the user.
   *
   * @return the query that the user entered, or the empty string. Never
   * returns null.
   */
  public String getQuery() {
    return query;
  }

  /** Sets the query as entered by the user.
   *
   * @param theQuery the query. It cannot be null.
   */
  public void setQuery(final String theQuery) {
    Validate.notNull(theQuery, "The query cannot be null.");
    query = theQuery;
  }

  /** Access Method.
   *
   * @return the page number.
   */
  public int getPageNumber() {
    return pageNumber;
  }

  /** Determines which page executing the query will return.
   *
   * @param thePageNumber the page number.
   */
  public void setPageNumber(final int thePageNumber) {
    pageNumber = thePageNumber;
  }

  /** Obtains the total number of pages that matched the query.
   *
   * @return total pages.
   */
  public int getTotalPages() {
    return totalPages;
  }
}

