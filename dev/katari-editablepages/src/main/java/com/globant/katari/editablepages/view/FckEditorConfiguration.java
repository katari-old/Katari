/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.editablepages.view;

/** Holds the configuraton information for the FKCEditor.
 *
 * This class is instantiated in the module.xml and injected in the controllers
 * that finally show the FCKEditor.
 *
 * It is intended to be only in spring.
 */
public class FckEditorConfiguration {

  /** The url where the FCKEditor custom configuration can be found.
   *
   * This should be an absolute path. It can be null, in which case FCKEditor
   * uses the default configuration.
   */
  private String configurationUrl = null;

  /** The name of the toolbar to use.
   *
   * It can be null, in which case FCKEditor uses the default toolbar.
   */
  private String toolbarSet = null;

  /** The width of the textarea that contains the editor in pixels or percent.
   *
   * This is apparently the only reliable way of setting the editor width. If
   * null, it uses the default.
   */
  private String width = null;

  /** The height of the textarea that contains the editor in pixels or percent.
   *
   * This is apparently the only reliable way of setting the editor height. If
   * null, it uses the default.
   */
  private String height = null;

  /** The height of the textarea that contains the editor in pixels or percent.
   *
   * This is apparently the only reliable way of setting the editor height. If
   * null, it uses the default.
   */
  private String editorAreaCss = null;

  /** Sets the url where the FCKEditor custom configuration can be found.
   *
   * @param url The url relative to the context path. It can be null, in which
   * case FCKEditor uses the default configuration.
   */
  public void setConfigurationUrl(final String url) {
    configurationUrl = normalizeUrl(url);
  }

  /** Returns the url where the FCKEditor custom configuration can be found.
   *
   * @return An absolute path, or null for the FCKEditor default configuration.
   */
  public String getConfigurationUrl() {
    return configurationUrl;
  }

  /** Sets the name of the toolbar to use.
   *
   * @param toolbar the toolbar name. It can be null, in which case FCKEditor
   * uses the default toolbar.
   */
  public void setToolbarSet(final String toolbar) {
   toolbarSet = toolbar;
  }

  /** Returns the name of the toolbar to use.
   *
   * @return the toolbar name, null for the default.
   */
  public String getToolbarSet() {
    return toolbarSet;
  }

  /** Sets the width of the textarea that contains the editor in pixels or
   * percent.
   *
   * This is apparently the only reliable way of setting the editor width. If
   * null, it uses the default.
   *
   * @param theWidth a string with the width in pixels (eg: 500) or percent
   * (90%). Null for the default width.
   */
  public void setWidth(final String theWidth) {
    width = theWidth;
  }

  /** Returns the width of the textarea that contains the editor in pixels or
   * percent.
   *
   * @return a string with the width in pixels (eg: 500) or percent (90%).
   * Null for the default width.
   */
  public String getWidth() {
    return width;
  }

  /** Sets the height of the textarea that contains the editor in pixels or
   * percent.
   *
   * This is apparently the only reliable way of setting the editor height. If
   * null, it uses the default.
   *
   * @param theHeight a string with the height in pixels (eg: 500) or percent
   * (90%).  Null for the default height.
   */
  public void setHeight(final String theHeight) {
    height = theHeight;
  }

  /** Returns the height of the textarea that contains the editor in pixels or
   * percent.
   *
   * @return a string with the height in pixels (eg: 500) or percent (90%).
   * Null for the default height.
   */
  public String getHeight() {
    return height;
  }

  /** Sets the EditorAreaCSS location to customize the text inside the editor.
   *
   * @param location a string with the location of a css file, relative to the
   * web application context. If null, the editor will use the default css.
   */
  public void setEditorAreaCss(final String location) {
    editorAreaCss = normalizeUrl(location);
  }

  /** Returns the EditorAreaCSS location to customize the text inside the
   * editor.
   *
   * @return the string of the location of a css file, relative to the web
   * application context, or null for the default css.
   */
  public String getEditorAreaCss() {
    return editorAreaCss;
  }

  /** Normalizes a url so that it always starts with /.
   *
   * @param url the url to normalize. It can be null, in which case null is
   * returned.
   *
   * @return a string that always starts with /, or null if the url was null.
   */
  private String normalizeUrl(final String url) {
    if (url == null) {
      return null;
    } else if (url.startsWith("/")) {
      return url;
    } else {
      return '/' + url;
    }
  }
}

