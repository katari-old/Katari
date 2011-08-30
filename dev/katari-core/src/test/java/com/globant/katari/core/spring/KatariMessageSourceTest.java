/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class KatariMessageSourceTest {

  @Test public void calculateFilenamesForLocale_noDebug() {
    KatariMessageSource messageSource = new KatariMessageSource();

    List<String> fileNames = messageSource.calculateFilenamesForLocale(
        "classpath:messages", Locale.US);
    assertThat(fileNames.size(), is(2));
    assertThat(fileNames.get(0), is("classpath:messages_en_US"));
    assertThat(fileNames.get(1), is("classpath:messages_en"));
  }

  @Test public void calculateFilenamesForLocale_withFallback() {
    KatariMessageSource messageSource = new KatariMessageSource(Locale.UK);

    List<String> fileNames = messageSource.calculateFilenamesForLocale(
        "classpath:messages", Locale.US);
    assertThat(fileNames.size(), is(3));
    assertThat(fileNames.get(0), is("classpath:messages_en_US"));
    assertThat(fileNames.get(1), is("classpath:messages_en"));
    assertThat(fileNames.get(2), is("classpath:messages_en_GB"));
  }

  @Test public void calculateFilenamesForLocale_noDebugAndDir() {
    KatariMessageSource messageSource = new KatariMessageSource();

    List<String> fileNames = messageSource.calculateFilenamesForLocale(
        "classpath:com/lang/messages", Locale.US);
    assertThat(fileNames.size(), is(4));
    assertThat(fileNames.get(0), is("classpath:com/lang_en_US/messages"));
    assertThat(fileNames.get(1), is("classpath:com/lang/messages_en_US"));
    assertThat(fileNames.get(2), is("classpath:com/lang_en/messages"));
    assertThat(fileNames.get(3), is("classpath:com/lang/messages_en"));
  }

  @Test public void calculateFilenamesForLocale_debug() {
    KatariMessageSource messageSource = new KatariMessageSource();

    messageSource.setDebug(true);
    messageSource.setDebugPrefix("fs");

    List<String> fileNames = messageSource.calculateFilenamesForLocale(
        "classpath:messages", Locale.US);
    assertThat(fileNames.size(), is(4));
    assertThat(fileNames.get(0), is("file:fs/messages_en_US"));
    assertThat(fileNames.get(1), is("file:fs/messages_en"));
    assertThat(fileNames.get(2), is("classpath:messages_en_US"));
    assertThat(fileNames.get(3), is("classpath:messages_en"));
  }

  @Test public void calculateFilenamesForLocale_debugAndDir() {
    KatariMessageSource messageSource = new KatariMessageSource();

    messageSource.setDebug(true);
    messageSource.setDebugPrefix("fs");

    List<String> fileNames = messageSource.calculateFilenamesForLocale(
        "classpath:com/lang/messages", Locale.US);
    assertThat(fileNames.size(), is(8));

    assertThat(fileNames.get(0), is("file:fs/com/lang_en_US/messages"));
    assertThat(fileNames.get(1), is("file:fs/com/lang/messages_en_US"));
    assertThat(fileNames.get(2), is("file:fs/com/lang_en/messages"));
    assertThat(fileNames.get(3), is("file:fs/com/lang/messages_en"));

    assertThat(fileNames.get(4), is("classpath:com/lang_en_US/messages"));
    assertThat(fileNames.get(5), is("classpath:com/lang/messages_en_US"));
    assertThat(fileNames.get(6), is("classpath:com/lang_en/messages"));
    assertThat(fileNames.get(7), is("classpath:com/lang/messages_en"));
  }

  @Test public void getMessage_fromClasspath() {
    KatariMessageSource messageSource = new KatariMessageSource();

    messageSource.setDebug(false);
    messageSource.setDebugPrefix(
        "src/test/resources/com/lang/katari/core/spring");
    messageSource.setBasename("classpath:katariMessageSource");

    String message = messageSource.getMessage("test1", null, Locale.US);

    assertThat(message, is("original_1"));
  }

  @Test public void getMessage_fromFs() {
    KatariMessageSource messageSource = new KatariMessageSource();

    messageSource.setDebug(true);
    messageSource.setDebugPrefix(
        "src/test/resources/com/globant/katari/core/spring");
    messageSource.setBasename("classpath:katariMessageSource");

    String message = messageSource.getMessage("test1", null, Locale.US);

    assertThat(message, is("overriden_1"));
  }

  @Test public void getMessage_fromFsNoCached() throws Exception {

    KatariMessageSource messageSource = new KatariMessageSource();
    messageSource.setDebug(true);
    messageSource.setDebugPrefix("target/test-data");
    messageSource.setBasename("classpath:katariMessageSource");

    // Copy katariMessageSource.properties to target. This does not override
    // test2.
    File dest = new File("target/test-data");
    dest.mkdirs();
    File src = new File("src/test/resources/com/globant/katari/core/"
        + "spring/katariMessageSource_en.properties");
    File dst = new File("target/test-data/katariMessageSource_en.properties");
    FileCopyUtils.copy(src, dst);
    // The message sources checks the timpestamp of the file to reload.
    dst.setLastModified(System.currentTimeMillis() - 10000);

    String message = messageSource.getMessage("test2", null, Locale.US);
    assertThat(message, is("original_2"));

    // Copy katariMessageSource2.properties to target. This overrides test2.
    File src2 = new File("src/test/resources/com/globant/katari/core/"
        + "spring/katariMessageSource2.properties");
    FileCopyUtils.copy(src2, dst);
    dst.setLastModified(System.currentTimeMillis());

    message = messageSource.getMessage("test2", null, Locale.US);
    assertThat(message, is("overriden_2"));
  }

  @Test public void getMessage_overrideInParent() {
    KatariMessageSource parent = new KatariMessageSource();
    parent.setBasename(
        "classpath:com/globant/katari/core/spring/katariMessageSource");
    KatariMessageSource messageSource = new KatariMessageSource();
    messageSource.setBasename("classpath:katariMessageSource");
    messageSource.setParentMessageSource(parent);

    String message = messageSource.getMessage("test1", null, Locale.US);
    assertThat(message, is("overriden_1"));
  }

  @Test public void getMessage_overrideForModule() {
    KatariMessageSource parent = new KatariMessageSource();
    parent.setBasename(
        "classpath:com/globant/katari/core/spring/katariMessageSource");
    KatariMessageSource messageSource = new KatariMessageSource();
    messageSource.setBasename("classpath:katariMessageSource");
    messageSource.setParentMessageSource(parent);
    messageSource.setModuleName("local-login");

    String message = messageSource.getMessage("test2", null, Locale.US);
    assertThat(message, is("overriden_2_for_module_name"));
  }
}

