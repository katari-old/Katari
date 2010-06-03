/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.Validate;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import com.octo.captcha.service.image.ImageCaptchaService;

/** Generates a captcha image based on the session id.
 */
public class CaptchaGeneratorController extends AbstractController {

  /** The class logger.
   */
  private static Log log = LogFactory.getLog(CaptchaGeneratorController.class);

  /** Service used to create and validate captcha images.
   *
   * We only use the image creation here. It is never null.
   */
  private ImageCaptchaService captchaService;

  /** Creates the controller.
   *
   * @param theCaptchaService The service used to create the captcha image. It
   * cannot be null.
   */
  public CaptchaGeneratorController(final ImageCaptchaService
      theCaptchaService) {
    Validate.notNull(theCaptchaService, "The captchaService cannot be null.");
    captchaService = theCaptchaService;
  }

  /** Use the session id to generate a captcha image.
   *
   * Creates an image in jpeg format based on the session id and sends it back
   * to the client.
   *
   * @param request The HTTP request we are processing. It cannot be null.
   *
   * @param response The HTTP response we are creating. It cannot be null.
   *
   * @exception Exception if the application logic throws an exception.
   *
   * @return null, this operation directly sends the image to the client.
   */
  public ModelAndView handleRequestInternal(final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    log.trace("Entering handleRequestInternal");

    ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

    String captchaId = request.getSession().getId();

    BufferedImage challenge =
      captchaService.getImageChallengeForID(captchaId, request.getLocale());

    ImageIO.write(challenge, "jpeg", jpegOutputStream);

    byte[] captchaChallengeAsJpeg = jpegOutputStream.toByteArray();

    // flush it in the response
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
    response.setContentType("image/jpeg");
    ServletOutputStream responseOutputStream = response.getOutputStream();
    responseOutputStream.write(captchaChallengeAsJpeg);
    responseOutputStream.flush();
    responseOutputStream.close();
    log.trace("Leaving handleRequestInternal");

    return null;
  }
}

