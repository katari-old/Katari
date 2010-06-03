package com.globant.katari.sample.time.view;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.globant.katari.sample.integration.SecurityUtils;
import com.globant.katari.sample.time.application.ViewTimeEntriesCommand;
import com.globant.katari.sample.time.domain.TimeEntry;
import com.globant.katari.sample.time.domain.TimeRepository;

/** Spring MVC Controller to save the time of a user.
 *
 * Subclasses need to override <code>createCommandBean</code> to retrieve a
 * backing object for the current form. Use method injection to override
 * <code>createCommandBean</code>.
 *
 * @author nicolas.frontini
 */
public abstract class MyTimeController extends BaseTimeController {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(MyTimeController.class);

  /** Default initialization for the controller.
   *
   * @param theTimeRepository The time entry repository.
   */
  public MyTimeController(final TimeRepository theTimeRepository) {
    super(theTimeRepository);
  }

  /** Set reference data used in the view.
   *
   * @param request The HTTP request we are processing.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return A <code>Map</code> with the reference data.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected Map referenceData(final HttpServletRequest request)
      throws Exception {
    log.trace("Entering referenceData");

    Map<String, Object> refData = super.referenceData(request);
    ViewTimeEntriesCommand viewTimeEntriesCommand =
        createViewTimeEntriesCommand();
    bindAndValidate(request, viewTimeEntriesCommand);

    // Gets the time entry list.
    List<TimeEntry> timeEntryList = viewTimeEntriesCommand.execute();

    refData.put("timeEntryList", timeEntryList);
    refData.put("user", SecurityUtils.getCurrentUser());
    refData.put("date", formatDate(viewTimeEntriesCommand.getDate()));

    log.trace("Leaving referenceData");
    return refData;
  }

  /** Returns the command to obtain the time entries from the domain.
   *
   * This method is injected by AOP.
   *
   * @return Returns the view time entries command bean injected.
   */
  protected abstract ViewTimeEntriesCommand createViewTimeEntriesCommand();
}

