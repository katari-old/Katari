/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.report.view;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

/** A multipart byte array editor that reads the content from an uploaded file.
 *
 * Although there is a similar editor in spring, this editor distinguishes
 * between an empty uploaded file and not specifying a file at all. When the
 * user does not specify a file, it sets the value to null.
 */
public class EmptyAwareMultipartFileEditor
    extends ByteArrayMultipartFileEditor {

  /** Sets the value of this editor from the uploaded file.
   *
   * @param value The uploaded file. It is ignored for everything that is not a
   * MultipartFile, even null.
   */
  public void setValue(final Object value) {
    if (value instanceof MultipartFile) {
      MultipartFile multipartFile = (MultipartFile) value;
      if (multipartFile.getOriginalFilename().equals("")) {
        // The user did not select a file.
        super.setValue(null);
      } else {
        super.setValue(value);
      }
    }
  }
}

