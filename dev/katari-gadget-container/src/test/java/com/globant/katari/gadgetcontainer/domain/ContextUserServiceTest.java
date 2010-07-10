/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import static org.junit.Assert.assertThat;

import static org.easymock.classextension.EasyMock.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;
import com.globant.katari.hibernate.coreuser.domain.CoreUserDetails;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

public class ContextUserServiceTest {

  @Test
  public void testGetCurrentUserId() {
    CoreUser user = createMock(CoreUser.class);
    expect(user.getId()).andReturn(1l);
    replay(user);

    CoreUserDetails details = new CoreUserDetails(user) {
      private static final long serialVersionUID = 1L;
      public GrantedAuthority[] getAuthorities() {
        return null;
      }
      public String getPassword() {
        return null;
      }
    };
    details.getCoreUser();

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(details, "");
    authentication.setDetails(details);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    ContextUserService service = new ContextUserService();
    assertThat(service.getCurrentUserId(), is(1l));
  }
}

