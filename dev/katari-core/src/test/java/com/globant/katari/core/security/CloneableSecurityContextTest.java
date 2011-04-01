/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.security;

import static org.easymock.classextension.EasyMock.*;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.acegisecurity.Authentication;

public class CloneableSecurityContextTest {

  @Test
  public void testClone() throws Exception {
    Authentication authentication = createMock(Authentication.class);
    CloneableSecurityContext context = new CloneableSecurityContext();
    context.setAuthentication(authentication);
    assertThat(context.clone().getAuthentication(), is(authentication));
  }
}

