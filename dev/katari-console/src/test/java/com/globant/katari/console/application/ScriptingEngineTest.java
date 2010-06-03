package com.globant.katari.console.application;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.matchers.JUnitMatchers.*;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.springframework.context.ApplicationContext;

import org.junit.Test;
import org.junit.Before;

import com.globant.katari.console.application.ScriptingEngine;

public class ScriptingEngineTest {
  
  private static final String UTF8 = "UTF-8";
  
  private ScriptingEngine scriptingEngine;
  private ApplicationContext applicationContext;
  private ByteArrayOutputStream output;
  private ByteArrayOutputStream error;
  
  @Before
  public void setUp() {
    applicationContext = createMock(ApplicationContext.class);
    scriptingEngine = new ScriptingEngine();
    scriptingEngine.setApplicationContext(applicationContext);
    output = new ByteArrayOutputStream();
    error = new ByteArrayOutputStream();
  }
  
  @Test
  public void testExecute() throws UnsupportedEncodingException {
    String helloWorld = "class Greeter {\n def greet = { name ->\n"
        + "println \"Hello, ${name}!\" \n }\n}\n\naGreeter = new Greeter();\n"
        + "aGreeter.greet(\"World\");\n";

    scriptingEngine.execute(helloWorld, output, error);
    assertThat(error.size(), is(0));
    assertThat(output.toString(UTF8), containsString("Hello, World!"));
  }
  
  @Test
  public void testExecuteTypo() {
    String helloWorld = "class Greeter {\n def gret = { name ->\n"
        + "println \"Hello, ${name}!\" \n }\n}\n\naGreeter = new Greeter();\n"
        + "aGreeter.greet(\"World\");\n";

    scriptingEngine.execute(helloWorld, output, error);
    assertThat(output.size(), is(0));
    assertThat(error.size(), not(0));
  }
  
  @Test
  public void testExecuteResult() throws UnsupportedEncodingException {
    String code = "1 == 2";

    scriptingEngine.execute(code, output, error);
    assertThat(output.toString(UTF8), containsString("false"));
    assertThat(error.size(), is(0));
    
    code = "5*8";
    
    output.reset();
    error.reset();
    
    scriptingEngine.execute(code, output, error);
    assertThat(output.toString(UTF8), containsString("40"));
    assertThat(error.size(), is(0));
  }
  
  @Test
  public void testExecuteAppContextAccess()
      throws UnsupportedEncodingException {
    String code = "def testBean = applicationContext.getBean(\"testBean\");"
        + "\ntestBean.toString();";
    
    expect(applicationContext.getBean("testBean")).andReturn("testOK");
    replay(applicationContext);
    
    scriptingEngine.execute(code, output, error);
    
    assertThat(output.toString(UTF8), containsString("testOK"));
    assertThat(error.size(), is(0));
  }
}
