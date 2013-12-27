package com.globant.katari.hibernate.search;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.WildcardQuery;
import org.hibernate.CacheMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;

/** The hibernate search support.*/
public class MockEntityRepository extends HibernateSearchSupport {

  /** {@inheritDoc}.*/
  public MockEntityRepository(final SearchSessionFactory factory) {
    super(factory);
  }

  /** Stores the given mock entity.
   * @param entity the entity to store.
   */
  public void save(final MockEntity entity) {
    getSession().saveOrUpdate(entity);
  }

  /** Stores the given mock entity.
   * @param entity the entity to store.
   */
  public void merge(final MockEntity entity) {
    getSession().merge(entity);
  }

  /** Searchs within the database the given mock entity by its name.
   * @param name the name of the mock entity.
   * @return the mock entity.
   */
  public MockEntity getByNameDirectSQL(final String name) {
    return (MockEntity) getSession().createCriteria(MockEntity.class)
        .add(Restrictions.eq("name", name)).uniqueResult();
  }

  /** Search mock entities given by its name.
   * @param name the name to search.
   * @return a list of mock entities.
   */
  @SuppressWarnings("unchecked")
  public List<MockEntity> searchLikeName(final String name) {
    FullTextSession fullTextSession = getFullTextSession();
    WildcardQuery wilCardQuery = new WildcardQuery(new Term("name",
        foldToASCII(name.trim()) + "*"));
    FullTextQuery query = fullTextSession
        .createFullTextQuery(wilCardQuery, MockEntity.class);
    query.setSort(new Sort(new SortField("name", SortField.STRING)));
    return query.list();
  }

  /** Search mock entities given by its name.
   * @param name the name to search.
   * @return a list of mock entities.
   */
  @SuppressWarnings("unchecked")
  public List<MockEntity> searchLikeDescription(final String name) {
    FullTextSession fullTextSession = getFullTextSession();
    WildcardQuery wilCardQuery = new WildcardQuery(new Term("description",
        foldToASCII(name.trim()) + "*"));
    FullTextQuery query = fullTextSession
        .createFullTextQuery(wilCardQuery, MockEntity.class);
    query.setSort(new Sort(new SortField("description", SortField.STRING)));
    return query.list();
  }

  /** Deletes all the entries.*/
  public void deleteAll() {
    List<MockEntity> l = getSession().createQuery("from MockEntity").list();
    for (MockEntity m : l) {
      getSession().delete(m);
    }
  }

  /** Regenerates the Lucene index of the given list of classes.
  *
  * If one of the classes does not have index attached nothing is done.
  *
  * @param theClasses The list of classes which indexes have to be
  *   regenerated, cannot be null.
  */
  public synchronized void reindex(final String... theClasses) {
    FullTextSession session = getFullTextSession();
    session.setDefaultReadOnly(false);
    try {
      for (String className : theClasses) {
        session
        .createIndexer(Class.forName(className))
        .batchSizeToLoadObjects( 25 )
        .cacheMode( CacheMode.NORMAL )
        .threadsToLoadObjects( 5 )
        .threadsForSubsequentFetching( 20 )
        .startAndWait();
      }
    } catch (InterruptedException error) {
      System.out.println("Error indexing content");
    } catch (ClassNotFoundException error) {
      System.out.println("Error indexing content");
    }
  }

  /** Re-Index.*/
  public void index() {
  }

}