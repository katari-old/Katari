package com.globant.katari.hibernate.search;

import java.lang.reflect.Field;

import org.apache.commons.lang.Validate;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.search.annotations.Index;

/**
 * This class creates a QueryBuilder for elasticsearch based on a Lucene query.
 */
final class LuceneQueryTranslator {

  /** The lucene query, it's never null. */
  private final Query luceneQuery;

  /** The entity, it's never null. */
  private final Class<?> entity;

  /** Creates a new instance of the factory.
   * @param theLuceneQuery the Lucene query.
   * @param targetEntity the target entity.
   */
  private LuceneQueryTranslator(final Query theLuceneQuery,
      final Class<?> targetEntity) {
    Validate.notNull(theLuceneQuery, "The lucene query cannot be null");
    luceneQuery = theLuceneQuery;
    entity = targetEntity;
  }

  /** Transform the given Lucene query into an Elastic-Search query builder.
   * @param luceneQuery the Lucene query, cannot be null.
   * @param targetEntity the target entity, cannot be null.
   * @return the elasticsearch query builder.
   */
  public static QueryBuilder toLuceneQueryBuilder(final Query luceneQuery,
      final Class<?> targetEntity) {
    Validate.notNull(luceneQuery, "The lucene query cannot be null");
    Validate.notNull(targetEntity, "The target entity cannot be null");
    return new LuceneQueryTranslator(luceneQuery, targetEntity).translate();
  }

  /** Translate the Lucene query into an ElasticSearch query builder.
   * @return the query builder, never null.
   */
  private QueryBuilder translate() {

    // case for non-query.
    if (luceneQuery.toString().equals("()")) {
      return QueryBuilders.matchAllQuery();
    }

    // case for queries :D
    if (luceneQuery instanceof BooleanQuery) {
      BooleanQuery boolQuery = (BooleanQuery) luceneQuery;
      return booleanQuery(boolQuery.getClauses());

    } else if (luceneQuery instanceof WildcardQuery) {
      return wildcard(luceneQuery);

    } else {
      return QueryBuilders.queryString(luceneQuery.toString());
    }

  }

  /** Resolves the boolean query.
   * @param clauses the clauses to evaluate.
   * @return the elasticsearch boolean query builder.
   */
  private BoolQueryBuilder booleanQuery(final BooleanClause[] clauses) {
    BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();

    for (BooleanClause clause : clauses) {

      if (clause.getQuery() instanceof BooleanQuery) {

        BooleanQuery booleanQuery = (BooleanQuery) clause.getQuery();
        if (booleanQuery.getClauses().length > 0) {
          occur(booleanQueryBuilder, clause,
              booleanQuery(booleanQuery.getClauses()));
        }// { else } should never been here, do I decide to ignore it.

      } else {
        Query query = clause.getQuery();
        // Here you can add lot of types, fuzzy, etc.
        if (query instanceof WildcardQuery) {
          occur(booleanQueryBuilder, clause, wildcard(query));
        } else {
          occur(booleanQueryBuilder, clause,
              QueryBuilders.queryString(query.toString()));
        }
      }
    }
    return booleanQueryBuilder;
  }

  /** Generates a wildcard based on the given lucene query.
   *
   * Note: This query, will use the "_raw" value instead of the analyzed one
   *  if the field has been marked as "un-tokenized".
   *
   * @param theLuceneQuery the lucene query.
   * @return the query builder.
   */
  private QueryBuilder wildcard(final Query theLuceneQuery) {
    Term term = ((WildcardQuery) theLuceneQuery).getTerm();
    String termField = term.field();
    try {
      Field field = entity.getDeclaredField(termField);
      org.hibernate.search.annotations.Field searchField;
      searchField = field.getAnnotation(
          org.hibernate.search.annotations.Field.class);

      if (searchField.index() == Index.NO) {
        // So, if it's not-tokenized, we use the raw value to search.
        return QueryBuilders.wildcardQuery(termField + "_raw", term.text());
      } else {
        return QueryBuilders.queryString(theLuceneQuery.toString());
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** Assign the boolean association within the given boolean query builder.
   * @param booleanQueryBuilder the boolean query builder.
   * @param clause the boolean clause.
   * @param elasticsearchQuery the elasticsearch query.
   */
  private void occur(final BoolQueryBuilder booleanQueryBuilder,
      final BooleanClause clause, final QueryBuilder elasticsearchQuery) {
    switch (clause.getOccur()) {
    case MUST:
      booleanQueryBuilder.must(elasticsearchQuery);
      break;
    case MUST_NOT:
      booleanQueryBuilder.mustNot(elasticsearchQuery);
      break;
    case SHOULD:
      booleanQueryBuilder.should(elasticsearchQuery);
      break;
    default:
      throw new IllegalStateException("Boolean clause must contains accour");
    }
  }

}
