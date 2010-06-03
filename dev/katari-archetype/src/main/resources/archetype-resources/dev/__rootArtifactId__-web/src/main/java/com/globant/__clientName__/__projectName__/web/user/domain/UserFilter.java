#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package com.globant.${clientName}.${projectName}.web.user.domain;

import com.globant.${clientName}.${projectName}.web.user.domain.filter.ContainsFilter;
import com.globant.${clientName}.${projectName}.web.user.domain.filter.Paging;
import com.globant.${clientName}.${projectName}.web.user.domain.filter.Sorting;

/** Holds the information to filter.
 *
 * Contains ordering, paging and contains filter information of users.
 */
public class UserFilter {

  /** The paging component.
   */
  private Paging paging = new Paging();

  /** The sorting component.
   */
  private Sorting sorting = new Sorting();

  /** The contains filter component.
   */
  private ContainsFilter containsFilter = new ContainsFilter();

  /** Get the paging component.
   *
   * @return Returns teh pagin component.
   */
  public Paging getPaging() {
    return paging;
  }

  /** Set the paging component.
   *
   * @param thePaging The paging component. It cannot be null.
   */
  public void setPaging(final Paging thePaging) {
    paging = thePaging;
  }

  /** Get the sorting component.
   *
   * @return Returns the sorting component.
   */
  public Sorting getSorting() {
    return sorting;
  }

  /** Set the sorting component.
   *
   * @param theSorting The sorting component. It cannot be null.
   */
  public void setSorting(final Sorting theSorting) {
    sorting = theSorting;
  }

  /** Get the contains filter component.
   *
   * @return Returns the contains filter component.
   */
  public ContainsFilter getContainsFilter() {
    return containsFilter;
  }

  /** Set the contains filter component.
   *
   * @param theContainsFilter The contains filter component. It cannot be null.
   */
  public void setContainsFilter(final ContainsFilter theContainsFilter) {
    containsFilter = theContainsFilter;
  }
}
