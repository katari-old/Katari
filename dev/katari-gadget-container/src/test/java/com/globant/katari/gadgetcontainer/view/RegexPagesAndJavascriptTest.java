/**
 * 
 */
package com.globant.katari.gadgetcontainer.view;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import com.globant.katari.gadgetcontainer.SpringTestUtils;

/**
 * test the pringDispatcher defined in module.xml
 * 
 * 
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 *
 */
public class RegexPagesAndJavascriptTest {
  
  private static final String KATARI_CANVAS_JS_FILE_NAME = "katariSocialCanvas.js";
  
  /** Regex used in com.globant.katari.gadgetcontainer.module.xml 
   */
  private static String regex = "(.*do|.*katariSocialCanvas.js)";
  
  @Test
  public void testUrl_js() {
    String what = "http://www.katari.org/" + KATARI_CANVAS_JS_FILE_NAME; 
    assertTrue(what.matches(regex));
  }
   
  @Test
  public void testUrl_do() {
    assertTrue("http://www.katari.org/resourse.do".matches(regex));
  }

  @Test
  public void testUrl_others() {
    assertFalse("http://www.katari.org/resourse.jsp".matches(regex));
    assertFalse("http://www.katari.org/resourse.j".matches(regex));
    assertFalse("http://www.katari.org/resourse.".matches(regex));
    assertFalse("http://www.katari.org/resourse.a".matches(regex));
    assertFalse("http://www.katari.org/resourse.o".matches(regex));
  }
  
  /** This test should fail if someone modifies the controller name and do not
   * change the regex that match exactly with the javascript controller.
   */
  @Test
  public void testExistController() {
    assertNotNull(SpringTestUtils.getContext().getBean("/" + KATARI_CANVAS_JS_FILE_NAME));
  }
}
