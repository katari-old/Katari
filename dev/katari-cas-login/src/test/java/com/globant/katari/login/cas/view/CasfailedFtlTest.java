/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.cas.view;

import com.globant.katari.tools.FreemarkerTestEngine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.acegisecurity.AuthenticationServiceException;

public class CasfailedFtlTest {

  @Test
  public final void testCasfailedFtl() throws Exception {

    List<String> valid = new ArrayList<String>();
    valid.add(".*Reason: error message.*");

    List<String> invalid = new ArrayList<String>();
    invalid.add(".*Exception.*");

    FreemarkerTestEngine engine = new FreemarkerTestEngine(
        "/com/globant/katari/login/cas/view", Locale.ENGLISH, buildModel());
    engine.runAndValidate("casfailed.ftl", valid, invalid);
  }

  private Map<java.lang.String, java.lang.Object> buildModel() {
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("ACEGI_SECURITY_LAST_EXCEPTION",
        new AuthenticationServiceException("error message"));
    return model;
  }
}

