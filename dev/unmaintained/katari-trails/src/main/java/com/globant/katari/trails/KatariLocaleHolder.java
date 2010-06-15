/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.trails;

import java.util.Locale;

import org.trails.i18n.LocaleHolder;

public class KatariLocaleHolder implements LocaleHolder {

  public Locale getLocale() {
    return TrailsModuleApplicationServlet.getCurrentLocale();
  }
}

