package com.globant.katari.hibernate.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.search.backend.AddLuceneWork;
import org.hibernate.search.backend.LuceneWork;

/** Holds add Lucene works.*/
public class AddLuceneWorks extends AddLuceneWork implements LuceneWorks {

  /** The serial version. */
  private static final long serialVersionUID = 1L;

  /** The list of lucene works, it's never null.*/
  private final List<LuceneWorkHolder> works;

  /** Creates a new instance of the AddLuceneWorks.
   * @param work the original work.
   */
  public AddLuceneWorks(final LuceneWork work) {
    super(work.getId(), work.getIdInString(), work.getEntityClass(),
        work.getDocument());
    works = new ArrayList<LuceneWorks.LuceneWorkHolder>();
    works.add(new LuceneWorkHolder(work));
  }

  /** Creates a new instance of the AddLuceneWorks.
   * @param work the lucene work holder.
   */
  public AddLuceneWorks(final LuceneWorkHolder work) {
    super(work.getLuceneWork().getId(), work.getLuceneWork().getIdInString(),
        work.getLuceneWork().getEntityClass(),
        work.getLuceneWork().getDocument());
    works = new ArrayList<LuceneWorks.LuceneWorkHolder>();
    works.add(work);
  }

  /** {@inheritDoc}. */
  public List<LuceneWorkHolder> getWorks() {
    return works;
  }

  /** {@inheritDoc}. */
  public void addWork(final LuceneWorkHolder workHolder) {
    Validate.notNull(workHolder, "The lucene work cannot be null");
    works.add(workHolder);
  }

}
