package com.globant.katari.sample.time.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.globant.katari.sample.time.application.SaveTimeEntryCommand;
import com.globant.katari.sample.time.domain.Activity;
import com.globant.katari.sample.time.domain.Project;
import com.globant.katari.sample.time.domain.TimeRepository;

/** Defines common functionality to edit and save time entries.
 *
 * Customize a binder with a date and defines the format date to be used.
 * Sets the reference data for projects and activities.
 *
 * @author nicolas.frontini
 */
public abstract class BaseTimeController extends SimpleFormController {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(BaseTimeController.class);

  /** The simple date format.
   */
  private static SimpleDateFormat simpleDateFormat =
      new SimpleDateFormat("MM/dd/yyyy");

  /** The time entry repository.
   */
  private TimeRepository timeRepository;

  /** Formats a Date into a date/time string.
   *
   * @param date the time value to be formatted into a time string.
   *
   * @return the formatted time string.
   */
  public static String formatDate(final Date date) {
    return getDateFormat().format(date);
  }

  /** Returns the date format.
   *
   * @return the date format.
   */
  public static DateFormat getDateFormat() {
    return simpleDateFormat;
  }

  /** Default initialization for the controller.
   *
   * The list of roles provide all the existing roles in the system.
   *
   * @param theTimeRepository The time entry repository.
   */
  public BaseTimeController(final TimeRepository theTimeRepository) {
    Validate.notNull(theTimeRepository,
        "The time entry repository cannot be null");
    timeRepository = theTimeRepository;
  }

  /** Receives the request to save a time entry.
   *
   * Generate a view for a specific view name, returning the ModelAndView
   * provided there.
   *
   * @param request The HTTP request we are processing.
   *
   * @param response The HTTP response we are creating.
   *
   * @param command Form object with request parameters bound onto it.
   *
   * @param errors Errors instance without errors.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return the ModelAndView for the next view.
   */
  protected final ModelAndView onSubmit(final HttpServletRequest request,
      final HttpServletResponse response, final Object command,
      final BindException errors) throws Exception {
    log.trace("Entering onSubmit");

    SaveTimeEntryCommand saveTimeEntryCommand = (SaveTimeEntryCommand) command;
    saveTimeEntryCommand.execute();

    ModelAndView successMav = new ModelAndView("redirect:myTime.do?date="
        + formatDate(saveTimeEntryCommand.getDate()));

    log.trace("Leaving onSubmit");
    return successMav;
  }

  /** Gets the time entry repository.
   *
   * @return The time entry repository.
   */
  protected TimeRepository getTimeRepository() {
    return timeRepository;
  }

  /** Initialize the given binder instance with a custom date editor.
   *
   * @param request current HTTP request.
   *
   * @param binder the new binder instance.
   *
   * @exception Exception if the application logic throws an exception.
   */
  @Override
  protected void initBinder(final HttpServletRequest request,
      final ServletRequestDataBinder binder) throws Exception {
    super.initBinder(request, binder);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(
        new SimpleDateFormat("MM/dd/yyyy"), true));
  }

  /** Retrieve a backing object for the current form from the given request.
   *
   * @param request The HTTP request we are processing.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return The command bean object.
   */
  @Override
  protected Object formBackingObject(final HttpServletRequest request)
      throws Exception {
    return createCommandBean();
  }

  /** This method is injected by AOP.
   *
   * @return Returns the command bean injected.
   */
  protected abstract Object createCommandBean();

  /** Sets reference data used in the view.
   *
   * @param request The HTTP request we are processing.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return A <code>Map</code> with the reference data.
   */
  @Override
  protected Map<String, Object> referenceData(final HttpServletRequest request)
      throws Exception {
    Map<String, Object> reference = new  LinkedHashMap<String, Object>();
    reference.put("request", request);
    reference.put("baseweb", request.getAttribute("baseweb"));

    // Projects.
    List<Project> projects = timeRepository.getProjects();
    Map<String, String> projectsMap = new LinkedHashMap<String, String>();
    for (Project project : projects) {
      projectsMap.put(String.valueOf(project.getId()), project.getName());
    }
    reference.put("projects", projectsMap);

    // Activities.
    List<Activity> activities = timeRepository.getActivities();
    Map<String, String> activitiesMap = new LinkedHashMap<String, String>();
    for (Activity activity : activities) {
      activitiesMap.put(String.valueOf(activity.getId()), activity.getName());
    }

    reference.put("activities", activitiesMap);
    return reference;
  }
}

