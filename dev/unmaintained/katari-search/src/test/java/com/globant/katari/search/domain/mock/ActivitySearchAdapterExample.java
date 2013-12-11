/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.domain.mock;

import java.util.ArrayList;

import com.globant.katari.search.domain.SearchAdapter;
import com.globant.katari.search.domain.SearchResultElement;
import com.globant.katari.search.domain.Action;

/** An example handler, for converting Activity objects into search results.
 */
public class ActivitySearchAdapterExample implements SearchAdapter {

  public boolean canConvert(final Object o) {
    return o instanceof Activity;
  }

  public SearchResultElement convert(final Object o, final float score) {

    Activity activity = (Activity) o;

    ArrayList<Action> actions;
    actions = new ArrayList<Action>();

    actions.add(new Action("Edit", null,
          "userEdit.do?id=" + activity.getId()));
    actions.add(new Action("Delete", null,
          "userDelete.do?id=" + activity.getId()));

    StringBuilder description = new StringBuilder();
    description.append("Activity - name: " + activity.getName());

    return new SearchResultElement("Activity", activity.getName(),
        description.toString(), "activity.do?id=" + activity.getId(), actions,
        score);
  }

  public String getViewUrl() {
    return "/module/user/activity.do";
  }

  public Class<?> getAdaptedClass() {
    return Activity.class;
  }
}

