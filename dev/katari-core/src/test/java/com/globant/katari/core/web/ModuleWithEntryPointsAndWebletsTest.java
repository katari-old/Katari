/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class ModuleWithEntryPointsAndWebletsTest extends TestCase {

  @SuppressWarnings("deprecation")
  public void testInit() {
    Map<String, ServletAndParameters> entryPoints;
    entryPoints = new HashMap<String, ServletAndParameters>();

    Map<String, String> weblets = new HashMap<String, String>();

    ModuleWithEntryPointsAndWeblets module;
    module = new ModuleWithEntryPointsAndWeblets(entryPoints, weblets);

    ModuleContext context = createMock(ModuleContext.class);
    context.registerEntryPoints(entryPoints);
    context.registerWeblets(weblets);
    replay(context);

    module.init(context);
    verify(context);
  }
}

