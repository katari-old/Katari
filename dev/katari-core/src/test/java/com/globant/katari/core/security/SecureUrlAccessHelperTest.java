package com.globant.katari.core.security;

import junit.framework.TestCase;

import org.acegisecurity.AccessDecisionManager;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.InsufficientAuthenticationException;
import org.acegisecurity.intercept.web.AbstractFilterInvocationDefinitionSource;
import org.acegisecurity.context.SecurityContextHolder;

import org.apache.commons.lang.Validate;

import static org.easymock.classextension.EasyMock.*;

/**
 * SecureUrlAccessHelper test case.
 * @author gerardo.bercovich
 */
public class SecureUrlAccessHelperTest extends TestCase {

  /** Helper instance under test. */
  private SecureUrlAccessHelper helper;

  /** Valid edit report uri. */
  private static final String SOURCE_URI = "/ctx/module/m/list.do";

  /** Valid edit report target url. */
  private static final String ABSOLUTE_GRANTED = "/module/m/edit.do";

  /** Valid delete report target uri. */
  private static final String ABSOLUTE_DENIED = "/module/m/delete.do";

  /** Valid delete report target uri. */
  private static final String MODULE_ROOT = "/module/m/";

  /**
   * Constructs the instance 'helper' of SecureUrlAccessHelper type with:
   *
   * A mocked DefinitionSource that returns a GrantAccess or DenyAccess
   * instance.
   * A DummyDecisionManager that decides based on the given DefinitionSource
   * type.
   */
  @Override
  protected void setUp() {

    AbstractFilterInvocationDefinitionSource sourceMock =
        createMock(AbstractFilterInvocationDefinitionSource.class);

    expect(sourceMock.lookupAttributes(ABSOLUTE_GRANTED))
      .andReturn(new GrantAccess()).anyTimes();

    expect(sourceMock.lookupAttributes(ABSOLUTE_DENIED))
      .andReturn(new DenyAccess()).anyTimes();

    expect(sourceMock.lookupAttributes(MODULE_ROOT))
      .andThrow(new AccessDeniedException("msg")).anyTimes();

    replay(sourceMock);

    GrantedAuthority editor = createMock(GrantedAuthority.class);
    expect(editor.getAuthority()).andReturn("IS_AUTHENTICATED_ANONYMOUSLY");
    expectLastCall().anyTimes();
    replay(editor);

    GrantedAuthority[] authorities = {editor};

    Authentication authentication = createMock(Authentication.class);
    expect(authentication.getAuthorities()).andReturn(authorities);
    expectLastCall().anyTimes();
    replay(authentication);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    helper = new SecureUrlAccessHelper(sourceMock, new DummyDecisionManager());
  }

  /**
   * Test grant access to relative edit url from valid uri location.
   */
  public void testCanAccesUrl_grantRelative() {
    assertTrue(helper.canAccessUrl(SOURCE_URI, "edit.do"));
  }
  /**
   * Test deny access to relative delte url from valid uri location.
   */
  public void testCanAccesUrl_denyRelative() {
    assertFalse(helper.canAccessUrl(SOURCE_URI, "delete.do"));
  }
  /**
   * Test grant access to absolute edit url from valid uri location.
   */
  public void testCanAccesUrl_grantAbsolute() {
    assertTrue(helper.canAccessUrl(SOURCE_URI, "/ctx" + ABSOLUTE_GRANTED));
  }

  /**
   * Test deny access to absolute delete url from valid uri location.
   */
  public void testCanAccesUrl_denyAbsolute() {
    assertFalse(helper.canAccessUrl(SOURCE_URI, "/ctx" + ABSOLUTE_DENIED));
  }

  /**
   * Test Exception trying to access to absolute edit url from invalid uri
   * location.
   */
  public void testCanAccesUrl_grantFromNoModuleAbsolute() {
    assertTrue(helper.canAccessUrl("/ctx/index.html", "/ctx"
        + ABSOLUTE_GRANTED));
  }

  /**
   * Test Exception trying to access to absolute edit url from invalid uri
   * location.
   */
  public void testCanAccesUrl_exceptionOnNoModuleRelative() {
    try {
      helper.canAccessUrl("/ctx/index.html", "edit.do");
      fail("cannot use outside a module");
    } catch (IllegalArgumentException e) {
    }
  }

  /**
   * Test Exception trying to access to full absolute edit url with protocol
   * from valid uri location.
   */
  public void testCanAccesUrl_exceptionOnUrlWithProtocol() {
    try {
      helper.canAccessUrl(SOURCE_URI,
          "http://localhost:8080/ctx/module/report/editReport.do");
      fail("cannot use protocols in absolute paths");
    } catch (IllegalArgumentException e) {
    }
  }

  public void testCanAccesUrl_rootModuleDenied() {
    assertFalse(helper.canAccessUrl(SOURCE_URI, "/ctx" + MODULE_ROOT));
  }

 /**
   * AccessDecisionManager implementation thats depends on the given
   * ConfigAttributeDefinition subtype to grant or deny access.
   * It is suited for use with mock objects for testing purpose.
   *
   * @author gerardo.bercovich
   */
  private static class DummyDecisionManager implements AccessDecisionManager {

    /**
     * decide upon the type of config passed.
     */
    public void decide(final Authentication authentication,
        final Object object, final ConfigAttributeDefinition config)
        throws AccessDeniedException, InsufficientAuthenticationException {

      Validate.isTrue(config instanceof DenyAccess
          || config instanceof GrantAccess);

      if (config instanceof DenyAccess) {
        throw new AccessDeniedException("test access denied exception");
      }
    }

    /**
     * not implemented.
     */
    public boolean supports(final ConfigAttribute attribute) {
      throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * not implemented.
     */
    @SuppressWarnings("unchecked")
    public boolean supports(final Class clazz) {
      throw new UnsupportedOperationException("Not implemented!");
    }
  }

  @SuppressWarnings("serial")
  private static class DenyAccess extends ConfigAttributeDefinition{}

  @SuppressWarnings("serial")
  private static class GrantAccess extends ConfigAttributeDefinition{}
}

