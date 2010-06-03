/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.event.application;

import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractApplicationContext;

import junit.framework.TestCase;

// import com.globant.katari.event.EventTestUtils;
import com.globant.katari.sample.testsupport.SpringTestUtils;

/* Tests the event handling.
 */
public class EventTest extends TestCase {

  /* Tests the event handling through the echo tcp server.
   */
  public synchronized void testEvent() throws Exception {

    ApplicationContext context = SpringTestUtils.getBeanFactory();
    TestHandler handler = new TestHandler(new String[] {"vm://testEvent"});

    Class<?> c = AbstractApplicationContext.class;
    Method m = c.getDeclaredMethod("addListener", ApplicationListener.class);
    m.setAccessible(true);
    m.invoke(context, handler);

    EventUtils.raiseEvent("testEvent", "message");

    for (int i = 0; i < 10; ++i) {
      wait(100);
      if (handler.message != null) {
        break;
      }
    }
    assertEquals("message", handler.message);
  }
}

