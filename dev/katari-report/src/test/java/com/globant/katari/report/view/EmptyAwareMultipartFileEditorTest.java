/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.view;

import junit.framework.TestCase;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class EmptyAwareMultipartFileEditorTest extends TestCase {

  public final void testSetValue_null() {
    EmptyAwareMultipartFileEditor editor = new EmptyAwareMultipartFileEditor();

    MultipartFile multipart;
    multipart = new MockMultipartFile("test", "", "text/html", new byte[0]);
    editor.setValue(multipart);
    assertNull(editor.getValue());
  }

  public final void testSetValue_notNull() {
    EmptyAwareMultipartFileEditor editor = new EmptyAwareMultipartFileEditor();

    byte[] data = (new String("data")).getBytes();
    MultipartFile multipart;
    multipart = new MockMultipartFile("test", "test", "text/html", data);
    editor.setValue(multipart);
    assertEquals(data, editor.getValue());
  }
}

