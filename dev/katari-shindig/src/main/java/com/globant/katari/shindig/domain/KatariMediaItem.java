/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import java.util.List;

/** Media items are stored in the media_item table, Items may be shared amongst
 * activities and are related to people.
 */
@Entity
@Table(name = "media_item")
public class KatariMediaItem implements MediaItem {

  /** The id of the media item.
   *
   * This is 0 for a newly created item.
   */
  @SuppressWarnings("unused")
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private long id = 0;

  /** The hibernate version for optimistic locking.
   */
  @Version
  @Column(name = "version")
  protected long version;

  /** The list of activities which this media item is reference in, this
   * relationship is specified by the java property mediaItems in the class
   * KatariActivity.
   *
   * @see ActivityDb for more information on this mapping.
   */
  @ManyToMany(targetEntity = KatariActivity.class, mappedBy = "mediaItems")
  protected List<Activity> activities;

  /**
   * model field.
   * @see org.apache.shindig.social.opensocial.model.MediaItem
   */
  @Column(name = "mime_type", length = 255)
  private String mimeType;

  /**
   * model field
   * @see org.apache.shindig.social.opensocial.model.MediaItem
   */
  @Column(name = "thumbnail_url", length = 255)
  private String thumbnailUrl;

  /**
   * model field.
   * @see org.apache.shindig.social.opensocial.model.MediaItem
   */
  @Transient
  @Column(name = "media_type")
  private Type type;

  /**
   * model field.
   * @see org.apache.shindig.social.opensocial.model.MediaItem
   */
  @Column(name = "url", length = 255)
  private String url;

  /** Create a new blank media item.
   */
  KatariMediaItem() {
  }

  /**
   * Create a media item specifying the mimeType, type and url.
   * @param mimeType the mime type of the media item.
   * @param type the type of the media items (see the specification)
   * @param url the url pointing to the media item.
   */
  public KatariMediaItem(String mimeType, Type type, String url) {
    this.mimeType = mimeType;
    this.type = type;
    this.url = url;
  }

  /** Creates a media item with all the attributes copied from source.
   */
  public KatariMediaItem(final MediaItem source) {
    mimeType = source.getMimeType();
    type = source.getType();
    thumbnailUrl = source.getThumbnailUrl();
    url = source.getUrl();
  }

  /**
   * {@inheritDoc}
   * @see org.apache.shindig.social.opensocial.model.MediaItem#getMimeType()
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * {@inheritDoc}
   * @see org.apache.shindig.social.opensocial.model.MediaItem#setMimeType(java.lang.String)
   */
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  /**
   * {@inheritDoc}
   * @see org.apache.shindig.social.opensocial.model.MediaItem#getType()
   */
  public Type getType() {
    return type;
  }

  /**
   * {@inheritDoc}
   * @see org.apache.shindig.social.opensocial.model.MediaItem#setType(org.apache.shindig.social.opensocial.model.MediaItem.Type)
   */
  public void setType(Type type) {
    this.type = type;
  }

  /**
   * {@inheritDoc}
   * @see org.apache.shindig.social.opensocial.model.MediaItem#getUrl()
   */
  public String getUrl() {
    return url;
  }

  /**
   * {@inheritDoc}
   * @see org.apache.shindig.social.opensocial.model.MediaItem#setUrl(java.lang.String)
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * {@inheritDoc}
   * @see org.apache.shindig.social.opensocial.model.MediaItem#getThumbnailUrl()
   */
  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  /**
   * {@inheritDoc}
   * @see org.apache.shindig.social.opensocial.model.MediaItem#setThumbnailUrl(java.lang.String)
   */
  public void setThumbnailUrl(String url) {
    this.thumbnailUrl = url;
  }
}

