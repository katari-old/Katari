/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.user.domain.filter;

/** Holds the information to filter by pagination.
 *
 * For an entity to match this filter, the result set must to contain
 * elements correspondign to the paging information specified.
 */
public class Paging {

  /** The default page size.
   */
  private static final int DEFAULT_PAGE_SIZE = 10;

  /** Number of the current page.
   */
  private int pageNumber = 0;

  /**
   * Size of the page.
   */
  private int pageSize = DEFAULT_PAGE_SIZE;

  /**
   * The total Page number.
   */
  private int totalPageNumber = 0;

  /** Get the page Number.
   *
   * @return Returns the page number.
   */
  public final int getPageNumber() {
    return pageNumber;
  }

  /** Set the page number.
   *
   * @param thePageNumber The number of the page.
   */
  public final void setPageNumber(final int thePageNumber) {
    pageNumber = thePageNumber;
  }

  /** Get the size of the page.
   *
   * @return Returns the size of the page. If the size page is 0 or negative
   * (less or equal than 0), the filter is not aplied.
   */
  public final int getPageSize() {
    return pageSize;
  }

  /** Set the size of the page.
   *
   * @param thePageSize The size of the page. If the size page is 0 or negative
   * (less or equal than 0), the filter is not aplied.
   */
  public final void setPageSize(final int thePageSize) {
    pageSize = thePageSize;
  }

  /** Gets the total page number.
   *
   * @return Returns the total page number.
   */
  public final int getTotalPageNumber() {
    return totalPageNumber;
  }

  /** Sets the total page number.
   *
   * @param theTotalPageNumber The total page number.
   */
  public final void setTotalPageNumber(final int theTotalPageNumber) {
    this.totalPageNumber = theTotalPageNumber;
  }
}
