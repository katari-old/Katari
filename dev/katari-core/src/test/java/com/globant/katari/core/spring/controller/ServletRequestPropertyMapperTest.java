package com.globant.katari.core.spring.controller;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class ServletRequestPropertyMapperTest {

  @Test
  public void test() {
    HttpServletRequest request = createMock(HttpServletRequest.class);
    HttpServletResponse response = createMock(HttpServletResponse.class);

    Hashtable<String, Object> attributes = new Hashtable<String, Object>();
    Hashtable<String, Object> parameters = new Hashtable<String, Object>();

    Object oneAttribute = new Object();
    String oneParameter = "the parameter value";

    attributes.put("attribute", oneAttribute);
    parameters.put("parameter", oneParameter);

    expect(request.getAttributeNames()).andReturn(attributes.keys());
    expect(request.getParameterNames()).andReturn(parameters.keys());

    expect(request.getParameterValues("parameter")).andReturn(
        new String[]{oneParameter});

    expect(request.getAttribute("attribute")).andReturn(oneAttribute);

    replay(request, response);

    ServletRequestPropertyMapper mapper;
    mapper = new ServletRequestPropertyMapper();
    Map<String, Object> finalMap = mapper.createPropertyMap(request, response);

    assertThat((String) finalMap.get("parameter"), is(oneParameter));
    assertThat(finalMap.get("attribute"), is(oneAttribute));

    verify(request, response);
  }

}
