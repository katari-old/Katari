package com.globant.katari.hibernate.search;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.search.backend.LuceneWork;

/** Defines a collections of lucene works wrapped into a holder that
 * indicates the index where should be place the given lucene work.
 */
public interface LuceneWorks {

  /** Retrieves the list of works to process.
   * @return the list of works, never null.
   */
  List<LuceneWorkHolder> getWorks();

  /** Adds a new work into the list of works.
   * @param workHolder the work holder to add.
   */
  void addWork(final LuceneWorkHolder workHolder);

  /** Holds a lucene work. */
  public class LuceneWorkHolder {

    /** the index name, can be null if we want to delegate the naming of the
     * index into hibernate.*/
    private String index = null;

    /** The lucene work, it's never null.*/
    private LuceneWork luceneWork;

    /** Creates a new instance of the holder.
     * @param work the work unit, cannot be null.
     */
    public LuceneWorkHolder(final LuceneWork work) {
      Validate.notNull(work, "The lucene work cannot be null");
      luceneWork = work;
    }

    /** Creates a new instance of the holder.
     * @param work the work unit, cannot be null.
     * @param theIndex the name of the index, cannot be null.
     */
    public LuceneWorkHolder(final LuceneWork work, final String theIndex) {
      this(work);
      Validate.notNull(theIndex, "The index cannot be null");
      index = theIndex;
    }

    /** Retrieves the index name, or null if delegates the index name into
     * hibernate.
     * @return the index or null.
     */
    public String getIndex() {
      return index;
    }

    /** Retrieves the lucene work.
     * @return the lucene work
     */
    public LuceneWork getLuceneWork() {
      return luceneWork;
    }
  }

}
