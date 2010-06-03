/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.search.view;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.classextension.EasyMock.*;

import com.globant.katari.search.application.SearchCommand;
import com.globant.katari.search.domain.SearchResultElement;

public class SearchControllerTest {

  @SuppressWarnings("unchecked")
  @Test
  public void testHandle() throws Exception {
    SearchController controller = new SearchController() {
      protected SearchCommand createCommandBean() {
        return null;
      }
    };
    List<SearchResultElement> result = new LinkedList<SearchResultElement>();
    SearchCommand command = createNiceMock(SearchCommand.class);
    expect(command.execute()).andReturn(result);
    replay(command);
    
    ModelAndView mav = controller.handle(null, null, command, null);

    assertThat(mav.getViewName(), is("search"));
    assertThat((List<SearchResultElement>)
        mav.getModelMap().get("searchResults"), is(result));
    assertThat((SearchCommand) mav.getModelMap().get("command"), is(command));
  }

  @Test
  public void testGetCommand() {
    final SearchCommand command = createNiceMock(SearchCommand.class);
    replay(command);

    SearchController controller = new SearchController() {
      protected SearchCommand createCommandBean() {
        return command;
      }
    };

    assertThat((SearchCommand) controller.getCommand(null), is(command));
  }
}

