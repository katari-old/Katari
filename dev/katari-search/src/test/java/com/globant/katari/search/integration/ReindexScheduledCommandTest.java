/* vim: set ts=2 et sw=2 cindent fo=qroca: */
package com.globant.katari.search.integration;

import org.junit.Test;

import static org.easymock.classextension.EasyMock.*;

import com.globant.katari.search.domain.IndexRepository;

public class ReindexScheduledCommandTest {

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_nullRepo() {
    new ReindexScheduledCommand(null);
  }

  @Test public void testExecute() {
    IndexRepository repository = createMock(IndexRepository.class);
    repository.reIndex();
    replay(repository);
    ReindexScheduledCommand command = new ReindexScheduledCommand(repository);
    command.execute();
    verify(repository);
  }

}

