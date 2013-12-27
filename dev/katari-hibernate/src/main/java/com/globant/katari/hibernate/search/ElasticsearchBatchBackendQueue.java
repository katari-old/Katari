package com.globant.katari.hibernate.search;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.hibernate.search.backend.LuceneWork;
import org.apache.commons.lang.Validate;
import org.elasticsearch.client.Client;

import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;

import org.hibernate.search.backend.impl.batch.BatchBackend;

/** Performs batch operations to the index.
 * @author waabox
 */
public class ElasticsearchBatchBackendQueue implements BatchBackend {

  /** The class log.*/
  private static Logger log =
      LoggerFactory.getLogger(ElasticsearchBatchBackendQueue.class);

  /** Seconds to wait for the next iteration of lucene works. */
  private static final long TIMER_DELAY = TimeUnit.SECONDS.toMillis(10);

  /** The number of elements to queue within the TIMER_DELAY. */
  private static final int NUMBER_OF_WORKS_TO_QUEUE = 200;

  /** The list of lucene works to execute, it's never null and should be
   * syncronized.
   */
  private final List<LuceneWork> works;

  /** The elasticsearch client, it's never null.*/
  private final Client elasticsearchClient;

  /** The mass indexer monitor, it's never null.*/
  private final MassIndexerProgressMonitor monitor;

  /** The idea with this timer is that every time someone sends a works
   * into this queue, we will create a new timer with 10 seconds of delay,
   * this is because we are queueing works, and of course we will have works
   * that will not be within the batch size, for example the last 40, 30 etc.
   * Soooo, thi timer will propagate the execution fo those ones :-).
   */
  private ScheduledExecutorService timer;

  /** The bach task.*/
  private final BachTask bachTask;

  /** Creates a new instance of the batch backend.
   * @param aClient the elasticsearch client, cannot be null.
   * @param aMonitor the progress monitor, cannot be null.
   */
  public ElasticsearchBatchBackendQueue(final Client aClient,
      final MassIndexerProgressMonitor aMonitor) {
    Validate.notNull(aClient, "The es client cannot be null");
    Validate.notNull(aMonitor, "The progress monitor cannot be null");
    works = new LinkedList<LuceneWork>();
    elasticsearchClient = aClient;
    monitor = aMonitor;
    bachTask = new BachTask(this);
  }

  /** Creates a new timer.*/
  private synchronized void createTimer() {
    if (timer == null) {
      timer = Executors.newScheduledThreadPool(1);
      timer.schedule(bachTask, TIMER_DELAY, TimeUnit.MILLISECONDS);
    }
  }

  /** Destroys the timer.*/
  private void destroyTimer() {
    timer.shutdownNow();
    timer = null;
  }

  /** {@inheritDoc}. */
  @Override
  public void enqueueAsyncWork(final LuceneWork work) {
    synchronized (works) {
      works.add(work);
      createTimer();
    }

    boolean sync = false;

    if (ElasticSearchClientFactory.instance().isLocalInstance()) {
      sync = true;
    }

    flushQueue(sync);
  }

  /** {@inheritDoc}. */
  @Override
  public void doWorkInSync(final LuceneWork work) {
    log.trace("Entering flush");
    execute(Collections.singletonList(work), true);
    log.trace("Leaving flush");
  }

  /** Flush the current queue.
   * @param force this flag checks if the queue should be flushed instantly.
   */
  private void flushQueue(final boolean force) {
    synchronized (works) {
      if (works.size() >= NUMBER_OF_WORKS_TO_QUEUE || force) {
        execute(works, true);
        works.clear();
        destroyTimer();
      }
    }
  }

  /** Sends the works into the elasticsearch queue.
   * @param workList the work list to send to elasticsearch.
   * @param sync runs sync or not.
   */
  private void execute(final List<LuceneWork> workList,
      final boolean sync) {
    log.trace("Entering execute");
    monitor.addToTotalCount(workList.size());
    new ElasticSearchBackendQueue(workList, elasticsearchClient, sync).run();
    log.trace("Leaving execute");
  }

  /** {@inheritDoc}. */
  @Override
  public void flush(final Set<Class<?>> indexedRootTypes) {
    log.trace("Entering flush");
    log.debug("Nothing to do here");
    log.trace("Leaving flush");
  }

  /** {@inheritDoc}. */
  @Override
  public void optimize(final Set<Class<?>> targetedClasses) {
    log.trace("Entering optimize");
    log.debug("Nothing to do here");
    log.trace("Leaving optimize");
  }

  /** The task that will be executed every timer tick.
   * @author waabox
   */
  private static class BachTask implements Runnable {

    /** The queue that holds the works.*/
    private final ElasticsearchBatchBackendQueue queue;

    /** Creates a new instance of the batch task.
     * @param aQueue the queue, cannot be null.
     */
    private BachTask(final ElasticsearchBatchBackendQueue aQueue) {
      Validate.notNull(aQueue, "The queue cannot be null");
      queue = aQueue;
    }

    /** {@inheritDoc}. */
    @Override
    public void run() {
      log.debug("entering run");
      queue.flushQueue(true);
      log.debug("leaving run");
    }
  }
}
