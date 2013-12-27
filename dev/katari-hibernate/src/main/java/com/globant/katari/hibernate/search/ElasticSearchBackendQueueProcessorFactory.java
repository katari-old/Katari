package com.globant.katari.hibernate.search;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.elasticsearch.client.Client;
import org.hibernate.search.backend.IndexingMonitor;
import org.hibernate.search.backend.LuceneWork;

import org.hibernate.search.spi.WorkerBuildContext;

import org.hibernate.search.backend.impl.lucene.LuceneBackendQueueProcessor;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;

/** Factory for the elastic search backend queue processor.*/
public class ElasticSearchBackendQueueProcessorFactory
  extends LuceneBackendQueueProcessor {

  /** The elastic search client, can be null if it's not invoked the method
   * initialize.
   */
  private Client client;

  /** {@inheritDoc}. */
  public void initialize(final Properties props,
      final WorkerBuildContext context,
      final DirectoryBasedIndexManager indexManager) {
    if (ElasticSearchClientFactory.isActive()) {
      client = ElasticSearchClientFactory.getClient();
    } else {
      super.initialize(props, context, indexManager);
    }
  }

  /** {@inheritDoc}. */
  @Override
  public void applyStreamWork(final LuceneWork singleOperation,
      final IndexingMonitor monitor) {
    if (ElasticSearchClientFactory.isActive()) {
      applyWork(Arrays.asList(singleOperation), monitor);
    } else {
      super.applyStreamWork(singleOperation, monitor);
    }
  }

  /** {@inheritDoc}. */
  @Override
  public void applyWork(final List<LuceneWork> workList,
       final IndexingMonitor monitor) {
    Validate.notNull(workList, "The worklist cannot be null");
    if (ElasticSearchClientFactory.isActive()) {
      boolean local;
      // Ok, local instance will run sync, unless the debug mode is false.
      local = ElasticSearchClientFactory.instance().isLocalInstance();
      new ElasticSearchBackendQueue(workList, client, local).run();
    } else {
      super.applyWork(workList, monitor);
    }
  }

  /** {@inheritDoc}. */
  public void close() {
    if (ElasticSearchClientFactory.isActive()) {
      ElasticSearchClientFactory.instance().destroy();
    } else {
      super.close();
    }
  }
  /** {@inheritDoc}. */
  @Override
  public void indexMappingChanged() {
    if (!ElasticSearchClientFactory.isActive()) {
      super.indexMappingChanged();
    }
  }

}
