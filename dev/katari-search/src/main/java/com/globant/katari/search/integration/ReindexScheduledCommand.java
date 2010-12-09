/* vim: set ts=2 et sw=2 cindent fo=qroca: */
package com.globant.katari.search.integration;

import org.apache.commons.lang.Validate;

import java.util.Map;
import java.util.HashMap;

import com.globant.katari.search.domain.IndexRepository;
import com.globant.katari.quartz.domain.ScheduledCommand;

/** Scheduled command that recreates the search index.
 */
public class ReindexScheduledCommand implements ScheduledCommand {

  /** The index repository used to perform the reindex process.
   *
   * This is never null.
   */
  private IndexRepository indexRepository;

  /** Creates a command to schedule a reindex.
   *
   * @param repository the index repository. It cannot be null.
   */
  public ReindexScheduledCommand(final IndexRepository repository) {
    Validate.notNull(repository, "The index repository cannot be null.");
    indexRepository = repository;
  }

  /** {@inheritDoc}
   */
  public String getDisplayName() {
    return "Reindex search database";
  }

  /** {@inheritDoc}
   *
   * This implementation returns null, we cannot yet measure progress.
   */
  public Integer getProgressPercent() {
    return null;
  }

  /** {@inheritDoc}
   */
  public Map<String, String> getInformation() {
    return new HashMap<String, String>();
  }

  /** {@inheritDoc}
   */
  public Void execute() {
    indexRepository.reIndex();
    return null;
  }
}

