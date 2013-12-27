package com.globant.katari.hibernate.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.search.backend.DeleteLuceneWork;

/** Holds delete Lucene works.*/
public class DeleteLuceneWorks extends DeleteLuceneWork
  implements LuceneWorks {

  /** The serial id.*/
  private static final long serialVersionUID = 1L;

  /** The list of lucene works, it's never null.*/
  private final List<LuceneWorkHolder> works;

  /** Creates a new instance of the delete Lucene works.
   * @param work the lucene delete work.
   */
  public DeleteLuceneWorks(final DeleteLuceneWork work) {
    super(work.getId(), work.getIdInString(), work.getEntityClass());
    works = new ArrayList<LuceneWorks.LuceneWorkHolder>();
    works.add(new LuceneWorkHolder(work));
  }

  /** {@inheritDoc}. */
  public List<LuceneWorkHolder> getWorks() {
    return works;
  }

  /** {@inheritDoc}. */
  public void addWork(final LuceneWorkHolder workHolder) {
    Validate.notNull(workHolder, "The work holder cannot be null");
    works.add(workHolder);
  }

}
