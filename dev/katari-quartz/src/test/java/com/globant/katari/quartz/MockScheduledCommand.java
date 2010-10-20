package com.globant.katari.quartz;

import java.util.HashMap;
import java.util.Map;

import com.globant.katari.quartz.domain.ScheduledCommand;

/** This is a simple Mock Implementation of the interface ScheduledCommand.
 *
 * It is just used for tests. All operations return a hardcoded value.
 *
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class MockScheduledCommand implements ScheduledCommand {

  /** {@inheritDoc}
   */
  public String getDisplayName() {
    return "Mock Impl";
  }

  /** {@inheritDoc}
   */
  public Integer getProgressPercent() {
    return 100;
  }

  /** {@inheritDoc}
   */
  public Map<String, String> getInformation() {
    return new HashMap<String, String>();
  }

  /** {@inheritDoc}
   */
  public Void execute() {
    return null;
  }
}

