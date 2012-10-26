package com.globant.katari.core.spring.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * @author waabox (emiliano[dot]arango[at]globant[dot]com)
 */
public class DatePropertyEditor extends PropertyEditorSupport {

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    setValue(new Date(Long.parseLong(text)));
  }

}
