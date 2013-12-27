package com.globant.katari.hibernate.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.globant.katari.hibernate.BaseRepository;

/**
 * Convenient super class for Hibernate Search-based data access objects.
 *
 * <p>This base class is mainly intended for {@link FullTextSession} usage but
 * can also be used when working with a Hibernate Session directly, for example
 * when relying on transactional Sessions. Convenience
 * {@link #getFullTextSession} methods are provided for that usage style.
 *
 * @see HibernateDaoSupport
 */
public class HibernateSearchSupport extends BaseRepository {

  /** The default length for the fold to ascii operation. */
  private static final int FOLD_TO_ASCII_DEFAULT_LENGTH = 512;

  /** The elastic search session factory.*/
  private final SearchSessionFactory searchSessionFactory;

  /** Creates a new instance of the hibernate search support.
   * @param factory the search session factory.
   */
  public HibernateSearchSupport(final SearchSessionFactory factory) {
    Validate.notNull(factory, "The search session factory cannot be null.");
    searchSessionFactory = factory;
  }

  /** Creates a fullTextQuery to be used for the {@link LocationRepository}.
   * @return A {@link FullTextQuery}, never null.
   */
  protected final FullTextSession getFullTextSession() {
    Session currentSession = getSessionFactory().getCurrentSession();
    FullTextSession fullTextSession;
    fullTextSession = searchSessionFactory.getFullTextSession(currentSession);
    fullTextSession.setDefaultReadOnly(true);
    return fullTextSession;
  }

  /** Creates a {@link BooleanQuery} for several Keywords using
   *     {@link Occur#SHOULD}.
   * @param builder The {@link QueryBuilder}, cannot be null.
   * @param values The values to look for, cannot be null nor empty.
   * @param field The field to use by hibernate search,
   *     cannot be null nor empty.
   * @return A {@link BooleanQuery} for several Keywords using
   *     {@link Occur#SHOULD}, never null.
   */
  protected BooleanQuery createKeyWordQuery(final QueryBuilder builder,
      final List<?> values, final String field) {
    Validate.notNull(builder, "The builder cannot be null.");
    Validate.notEmpty(values, "The values cannot be null nor empty.");
    Validate.notEmpty(field, "The field cannot be null nor empty.");
    BooleanQuery booleanQuery = new BooleanQuery();
    for (Object value : values) {
      Query query = builder.keyword().onField(field).
          matching(value).createQuery();
      booleanQuery.add(query, Occur.SHOULD);
    }
    return booleanQuery;
  }

  /** Creates a {@link BooleanQuery} for several wildcards using
   *     {@link Occur#MUST}.
   * The returned query supports several words at the same time.
   * Note: The indexed class has to have and {@link Analyzer} the
   * {@link ASCIIFoldingFilterFactory}.
   * @param builder The {@link QueryBuilder}, cannot be null.
   * @param value The value to look for, cannot be null nor empty.
   * @param field The field to use by hibernate search,
   *     cannot be null nor empty.
   * @return A {@link BooleanQuery} for several Keywords using
   *     {@link Occur#MUST}, never null.
   */
  protected BooleanQuery createWildCardQuery(
      final QueryBuilder builder, final String value, final String field) {
    Validate.notNull(builder, "The builder cannot be null.");
    Validate.notEmpty(value, "The value cannot be null nor empty.");
    Validate.notEmpty(field, "The field cannot be null nor empty.");
    BooleanQuery booleanQuery = new BooleanQuery();
    for (String val : value.split(" ")) {
      WildcardQuery query = new WildcardQuery(new Term(field,
          foldToASCII(val.trim()) + "*"));
      booleanQuery.add(query, Occur.MUST);
    }
    return booleanQuery;
  }

  /** Removes all the special characters from the user input.
   *
   * This method applies a filter that searches for the especial characters
   * such as À, ⱥ ... and replaces with an ascii character such as A, a ...
   *
   * The filter is used by the {@link WildcardQuery} or {@link PrefixQuery}
   * because this type of queries do not use the defined {@link Analyzer}s.
   * This means that the information is saved through the {@link Analyzer} but
   * the reading is raw. The consequence is that the especial characters have
   * to be entered by the user, this filter solves this issue.
   *
   * @param theUserInput The user input, it is never null.
   * @return The user input without special characters, never null.
   */
  protected final String foldToASCII(final String theUserInput) {
    char[] output = new char[FOLD_TO_ASCII_DEFAULT_LENGTH];
    final int maxSizeNeeded = 4 * theUserInput.length();
    if (output.length < maxSizeNeeded) {
      output = new char[ArrayUtil.oversize(maxSizeNeeded,
          RamUsageEstimator.NUM_BYTES_CHAR)];
    }
    int outputPosition = ASCIIFoldingFilter.foldToASCII(
        theUserInput.toCharArray(), 0, output, 0, theUserInput.length());
    return new String(Arrays.copyOfRange(output, 0, outputPosition));
  }

}
