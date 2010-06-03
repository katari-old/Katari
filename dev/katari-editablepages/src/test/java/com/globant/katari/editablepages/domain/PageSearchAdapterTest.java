/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.domain;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import com.globant.katari.search.domain.SearchResultElement;
import com.globant.katari.search.domain.Action;

public class PageSearchAdapterTest {

  @Test
  public void testCanConvert() {
    PageSearchAdapter adapter;
    adapter = new PageSearchAdapter("/module/editable-pages");
    // This works due to the getAdaptedClass covariance, otherwise it fails
    // with a compilation error.
    assertThat(adapter.getAdaptedClass(), equalTo(Page.class));
  }

  @Test
  public void testConvert_unpublished() {
    Page page = new Page("admin", "test name", "test title",
        "<html><body>test content</body></html>");

    PageSearchAdapter adapter;
    adapter = new PageSearchAdapter("/module/editable-pages");

    SearchResultElement result = adapter.convert(page, 10);

    assertThat(result.getDescription(),
        is("Page - name: test name; title: test title"));

    List<Action> actions = result.getActions();
    Action action = actions.get(0);
    assertThat(actions.size(), is(1));

    assertThat(action.getName(), is("Edit"));
    assertThat(action.getIcon(), is(nullValue()));
    assertThat(action.getUrl(),
        is("/module/editable-pages/edit/edit.do?id=0"));
  }

  @Test
  public void testConvert_published() {
    Page page = new Page("admin", "test name", "test title",
        "<html><body>test content</body></html>");
    page.publish();

    PageSearchAdapter adapter;
    adapter = new PageSearchAdapter("/module/editable-pages");

    SearchResultElement result = adapter.convert(page, 10);

    assertThat(result.getDescription(),
        is("Page - name: test name; title: test title;"
         + " content: <html><body>test content</body></html>"));

    List<Action> actions = result.getActions();
    Action action = actions.get(0);
    assertThat(actions.size(), is(1));

    assertThat(action.getName(), is("Edit"));
    assertThat(action.getIcon(), is(nullValue()));
    assertThat(action.getUrl(),
        is("/module/editable-pages/edit/edit.do?id=0"));
  }

  @Test
  public void testConvert_publishedLong() {
    StringBuilder content = new StringBuilder();
    for (int i = 0; i < 100; ++ i) {
      content.append("test content<br/>");
    }
    Page page = new Page("admin", "test name", "test title",
        "<html><body>" + content.toString() + "</body></html>");
    page.publish();

    PageSearchAdapter adapter;
    adapter = new PageSearchAdapter("/module/editable-pages");

    SearchResultElement result = adapter.convert(page, 10);

    assertTrue(result.getDescription().length() < 160);

    List<Action> actions = result.getActions();
    Action action = actions.get(0);
    assertThat(actions.size(), is(1));

    assertThat(action.getName(), is("Edit"));
    assertThat(action.getIcon(), is(nullValue()));
    assertThat(action.getUrl(),
        is("/module/editable-pages/edit/edit.do?id=0"));
  }
}


