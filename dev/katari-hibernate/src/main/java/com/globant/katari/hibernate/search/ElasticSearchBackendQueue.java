package com.globant.katari.hibernate.search;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.Validate;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

import org.elasticsearch.client.Client;

import org.elasticsearch.action.ActionFuture;

import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;

import org.hibernate.search.backend.AddLuceneWork;
import org.hibernate.search.backend.DeleteLuceneWork;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.OptimizeLuceneWork;
import org.hibernate.search.backend.PurgeAllLuceneWork;

import com.globant.katari.hibernate.search.LuceneWorks.LuceneWorkHolder;

/** Backend for elasctic search.
 * Supported operations: create/update and delete an entity.
 */
public class ElasticSearchBackendQueue implements Runnable {

  /** The class logger.*/
  private static Logger log = LoggerFactory.getLogger(
      ElasticSearchBackendQueue.class);

  /** Amount of ms to sleep.*/
  private static final int SLEEP_TIMEOUT = 100;

  /** The list of works, it's never null.*/
  private final List<LuceneWork> works;

  /** The elastic search client, it's never null.*/
  private final Client elasticSearchClient;

  /** The elastic search requestBuilder builder, it's never null.*/
  private final BulkRequestBuilder requestBuilder;

  /** Checks if should run sync or async.*/
  private final boolean sync;

  /** Creates a new instance of the backend queue.
   * @param luceneWorks the list of lucene's works, cannot be null.
   * @param client the elastic-search client, cannot be null.
   * @param syncOperation checks if should run sync or async.
   */
  public ElasticSearchBackendQueue(final List<LuceneWork> luceneWorks,
      final Client client, final boolean syncOperation) {
    Validate.notNull(luceneWorks, "The list of lucene works cannot be null.");
    Validate.notNull(client, "The elastic search client cannot be null.");
    works = luceneWorks;
    elasticSearchClient = client;
    requestBuilder = elasticSearchClient.prepareBulk();
    sync = syncOperation;
  }

  /** {@inheritDoc}.*/
  public void run() {
    log.trace("Entering run");

    for (LuceneWork work : works) {

      if (work instanceof AddLuceneWork) {
        if (work instanceof AddLuceneWorks) {
          AddLuceneWorks currentWorks = (AddLuceneWorks) work;
          for (LuceneWorkHolder holder : currentWorks.getWorks()) {
            requestBuilder.add(handleAdd(
                (AddLuceneWork) holder.getLuceneWork(), holder.getIndex()));
          }
        } else {
          requestBuilder.add(handleAdd((AddLuceneWork) work,
              getIndexName(work)));
        }

      } else if (work instanceof DeleteLuceneWork) {
        if (work instanceof LuceneWorks) {
          DeleteLuceneWorks currentWorks = (DeleteLuceneWorks) work;
          for (LuceneWorkHolder holder : currentWorks.getWorks()) {
            delete((DeleteLuceneWork) holder.getLuceneWork(),
                holder.getIndex());
          }
        } else {
          delete((DeleteLuceneWork) work, getIndexName(work));
        }

      } else if (work instanceof PurgeAllLuceneWork) {
        ElasticsearchIndexManager.recreateIndex(
            work.getEntityClass(), elasticSearchClient);

      } else if (work instanceof OptimizeLuceneWork) {
        log.debug("OptimizeLuceneWork, nothing to do here.");

      } else {
        String className = work.getClass().getName();
        log.debug("Unhandled lucene's work:{}, nothing to do here.",
            className);
      }
    }

    long start = System.currentTimeMillis();
    log.debug("Sending requestBuilder to elasticsearch");

    ListenableActionFuture<?> requestFuture = requestBuilder.execute();

    if (sync) {

      waitFor(requestFuture);

      Set<String> indices = new HashSet<String>();

      for (int i=0; i< works.size(); i++) {
        LuceneWork work = works.get(i);
        if (work instanceof LuceneWorks) {
          List<LuceneWorkHolder> holders = ((LuceneWorks) work).getWorks();
          for (LuceneWorkHolder holder : holders) {
            String indexName = holder.getIndex();
            if (indexName == null) {
              indexName = getIndexName(holder.getLuceneWork());
            }
            indices.add(indexName);
          }
        } else {
          if (work.getEntityClass() != null) {
            indices.add(getIndexName(work));
          }
        }
      }

      log.debug("The application will force the refresh of the indices:{}",
          indices);

      RefreshRequest refreshRequest = new RefreshRequest(
          indices.toArray(new String[indices.size()]));
      ActionFuture<?> refreshFuture;
      refreshFuture = elasticSearchClient.admin().indices().refresh(
          refreshRequest);

      waitFor(refreshFuture);
    }

    long duration = System.currentTimeMillis() - start;
    log.debug("elapsed time: {} ms", duration);

    log.trace("Leaving run");
  }

  /** Sleeps the current thread 100 ms.
   * @param future the future to wait for.
   * */
  private void waitFor(final Future<?> future) {
    while (!future.isDone()) {
      try {
        Thread.sleep(SLEEP_TIMEOUT);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /** Performs the creation/update of an entity.
   * @param work the Lucene work to process.
   * @param currentIndex the index name.
   * @return the index request builder for add.
   */
  private IndexRequestBuilder handleAdd(final AddLuceneWork work,
      final String currentIndex) {
    log.trace("Entering handleAdd");

    String type = work.getEntityClass().getName();
    String id = work.getIdInString();
    String index;

    if (currentIndex == null) {
      index = getIndexName(work);
    } else {
      index = currentIndex;
    }

    log.debug("Working with: {} with id: {} within the index: {}",
        new String[] {type, id, index});

    try {
      XContentBuilder builder = XContentFactory.jsonBuilder();
      builder.startObject();
      Document document = work.getDocument();
      log.debug("starting document");
      for (Fieldable fieldable : document.getFields()) {
        if (fieldable instanceof ElasticsearchFieldable) {
          ((ElasticsearchFieldable) fieldable).appendTo(builder);
        } else {
          String name = fieldable.name();
          String value = fieldable.stringValue();
          log.debug("adding the field: {}, {}", name, value);
          builder.field(name, value);
        }
      }
      log.debug("finish document");
      builder.endObject();
      IndexRequestBuilder indexRequestBuilder;
      indexRequestBuilder = elasticSearchClient.prepareIndex(index, type, id);
      indexRequestBuilder.setSource(builder);

      log.trace("Leaving handleAdd");

      return indexRequestBuilder;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Deletes the given lucene work.
   * @param work the Lucene work to delete.
   * @param currentIndex the index name.
   */
  private void delete(final DeleteLuceneWork work,
      final String currentIndex) {

    log.trace("Entering handleDelete");

    String type = work.getEntityClass().getName();
    String id = work.getIdInString();
    String indexName;

    if (currentIndex == null) {
      indexName = getIndexName(work);
    } else {
      indexName = currentIndex;
    }

    log.debug("Deleting: {} with id: {} within the index: {}",
        new String[] {type, id, indexName});

    DeleteRequestBuilder builder;
    builder = elasticSearchClient.prepareDelete(indexName, type, id);
    builder.execute();

    log.trace("Leaving handleDelete");
  }

  /** Retrieves the index name given by the lucene work.
   * @param work the lucene work.
   * @return the string name of the index.
   */
  private String getIndexName(final LuceneWork work) {
    return SearchUtils.getIndexName(work.getEntityClass());
  }

}
