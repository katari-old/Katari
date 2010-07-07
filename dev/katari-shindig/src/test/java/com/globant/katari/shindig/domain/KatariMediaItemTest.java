/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.apache.shindig.social.opensocial.model.MediaItem;

public class KatariMediaItemTest {

  @Test
  public void testCreateMediaItem_copy() {
    MediaItem item = new KatariMediaItem("image/png", MediaItem.Type.IMAGE,
        "some-url");
    item.setThumbnailUrl("thumb-url");

    KatariMediaItem newItem = new KatariMediaItem(item);
    assertThat(newItem.getMimeType(), is("image/png"));
    assertThat(newItem.getType(), is(MediaItem.Type.IMAGE));
    assertThat(newItem.getUrl(), is("some-url"));
    assertThat(newItem.getThumbnailUrl(), is("thumb-url"));
  }
}

