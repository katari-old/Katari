/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.jsmodule.domain;

import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/** Bundles a list of files by minifying each one and then concatenating the
 * resulting files.
 */
public class DependenciesBundler {

  /**
   * {@link Logger} used for logging the {@link CompressorErrorReporter}.
   */
  private static Logger log =
      LoggerFactory.getLogger(DependenciesBundler.class.getName());

  /** Column number where to insert a break line.
   *
   * If it's -1 no break line will be inserted. The default value is 80.
   */
  private static final int LINE_BREAK_POSTION = 80;

  /** Indicates if the file will be minified or not.
   *
   * The default value is true.
   */
  private static final boolean MUNGE = true;

  /** Indicates if messages and warnings will be logged or not.
   *
   * The default value is false.
   */
  private static final boolean VERBOSE = false;

  /** Indicates if all semicolons will be preserved or not.
   *
   * The default value is false.
   */
  private static final boolean PRESERVE_ALL_SEMICOLONS = false;

  /** Indicates if all micro optimizations will be disabled or not.
   *
   * The default value is true.
   */
  private static final boolean DISABLE_OPTIMIZATIONS = false;

  /** Bundles a list of files by minifying each of them, and then concatenating
   * the minified files.
   *
   * @param resources The list of resources to be bundled. Cannot be null.
   * @return A string representing the content of the bundled file.
   */
  public String bundleFiles(final List<String> resources) {
    Validate.notNull(resources, "The files to be bundled cannot be null.");

    StringBuilder bundle = new StringBuilder();
    for (String resource : resources) {
      bundle.append("/***************************************************\n");
      bundle.append(" * Bundled from '" + resource + "'\n");
      bundle.append(" ***************************************************/\n");
      bundle.append(compressFile(resource));
      bundle.append("\n");
    }
    return bundle.toString();
  }

  /** Compresses a single js file.
   *
   * @param resource the resource name of the file to compress. The resource
   * will be loaded from the current class' class loader. It cannot be null.
   *
   * @return a string with the compressed file. It never returns null.
   */
  private String compressFile(final String resource) {
    Validate.notNull(resource, "The resource cannot be null.");

    JavaScriptCompressor compressor;
    InputStream resourceContent = getClass().getResourceAsStream(resource);

    // This writer does not need to be closed.
    CharArrayWriter compressedFile;
    try {
      compressor = new JavaScriptCompressor(
          new InputStreamReader(resourceContent, "UTF-8"),
          new CompressorErrorReporter());
      compressedFile = new CharArrayWriter();
      compressor.compress(compressedFile, LINE_BREAK_POSTION, MUNGE,
          VERBOSE, PRESERVE_ALL_SEMICOLONS, DISABLE_OPTIMIZATIONS);
    } catch (RuntimeException e) {
      // Rethrow, don't wrap a runtime.
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Error compressing " + resource, e);
    } finally {
      IOUtils.closeQuietly(resourceContent);
    }
    return compressedFile.toString();
  }

  /** Contain callback functions that will be called when the compressor finds
   * warnings or errors in the JavaScript code.
   *
   * It's used by the constructor of the {@link JavaScriptCompressor}.
   */
  private static class CompressorErrorReporter implements ErrorReporter {

    /** {@inheritDoc} */
    public void warning(final String message, final String sourceName,
        final int line, final String lineSource, final int lineOffset) {
      if (line < 0) {
        log.warn(message);
      } else {
        log.warn(line + ':' + lineOffset + ':' + message);
      }
    }

    /** {@inheritDoc} */
    public void error(final String message, final String sourceName,
        final int line, final String lineSource, final int lineOffset) {
      if (line < 0) {
        log.error(message);
      } else {
        log.error(line + ':' + lineOffset + ':' + message);
      }
    }

    /** {@inheritDoc} */
    public EvaluatorException runtimeError(final String message,
        final String sourceName, final int line, final String lineSource,
        final int lineOffset) {
      error(message, sourceName, line, lineSource, lineOffset);
      return new EvaluatorException(message);
    }
  }
}
