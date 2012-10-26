package com.globant.katari.core.spring.controller;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author waabox (waabox[at]gmail[dot]com)
 */
public class DefaultRequestParameterProcessorTest {

  @Test public void process() {
    MockHttpServletRequest request = new MockHttpServletRequest();

    request.setParameter("one", "hello");
    request.setParameter("two", "hello");

    HttpServletResponse response = new MockHttpServletResponse();

    Map<String, String> renames = new TreeMap<String, String>();

    renames.put("one", "theNewValue_1");
    renames.put("two", "theNewValue_2");

    Map<String, Object> parameters = new TreeMap<String, Object>();
    DefaultRequestParameterProcessor processor;
    processor = new DefaultRequestParameterProcessor(renames);
    processor.process(request, response, parameters);

    assertThat(parameters.get("theNewValue_1"), notNullValue());
    assertThat(parameters.get("theNewValue_2"), notNullValue());
    assertThat(parameters.keySet().size(), is(2));
  }

}
