/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import static org.easymock.EasyMock.*;

import javax.servlet.http.HttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Locale;

import org.springframework.web.servlet.ModelAndView;

import com.octo.captcha.service.image.ImageCaptchaService;
import java.awt.image.BufferedImage;

public class CaptchaGeneratorControllerTest {

  @Test
  public final void testHandleRequestInternal() throws Exception {

    HttpSession session = createMock(HttpSession.class);
    expect(session.getId()).andReturn("SOMEID");
    replay(session);

    HttpServletRequest request = createMock(HttpServletRequest.class);
    expect(request.getSession()).andReturn(session);
    expect(request.getLocale()).andReturn(Locale.US);
    replay(request);

    BufferedImage captcha;
    captcha = new BufferedImage(10, 20, BufferedImage.TYPE_INT_RGB);

    ImageCaptchaService captchaService = createMock(ImageCaptchaService.class);
    expect(captchaService.getImageChallengeForID("SOMEID", Locale.US))
      .andReturn(captcha);
    replay(captchaService);

    MockHttpServletResponse response = new MockHttpServletResponse();

    CaptchaGeneratorController controller;
    controller = new CaptchaGeneratorController(captchaService);
    ModelAndView mav = controller.handleRequestInternal(request, response);
    assertNull(mav);

    assertEquals("image/jpeg", response.getContentType());
    assertTrue(response.getContentAsByteArray().length != 0);

    verify(captchaService);
    verify(request);
  }
}

