/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.web;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class ModuleWithEntryPointsTest extends TestCase {

  @SuppressWarnings("deprecation")
  public void testInit() {
    Map<String, ServletAndParameters> entryPoints;
    entryPoints = new HashMap<String, ServletAndParameters>();

    ModuleWithEntryPoints userModule = new ModuleWithEntryPoints(entryPoints,
        new MenuBar("root", "root"));

    ModuleContext context = createMock(ModuleContext.class);
    context.registerEntryPoints(entryPoints);
    context.registerMenu(new MenuBar("root", "root"));
    replay(context);

    userModule.init(context);
    verify(context);
  }
}
