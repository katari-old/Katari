/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.Address;
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
@Table(name = "media_items")
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

  /** Create a media item specifying the mimeType, type and url.
   *
   * @param theMimeType the mime type of the media item.
   *
   * @param theType the type of the media items (see the specification)
   *
   * @param theUrl the url pointing to the media item.
   */
  public KatariMediaItem(final String theMimeType, final Type theType,
      final String theUrl) {
    mimeType = theMimeType;
    type = theType;
    url = theUrl;
  }

  /** Creates a media item with some attributes copied from source.
   * 
   * This is a partial implementation, will be finished in future releases.
   *
   * @param source The original media item. It cannot be null.
   */
  public KatariMediaItem(final MediaItem source) {
    mimeType = source.getMimeType();
    type = source.getType();
    thumbnailUrl = source.getThumbnailUrl();
    url = source.getUrl();
  }

  /** {@inheritDoc}
   */
  public String getMimeType() {
    return mimeType;
  }

  /** {@inheritDoc}
   */
  public void setMimeType(final String theMimeType) {
    mimeType = theMimeType;
  }

  /** {@inheritDoc}
   */
  public Type getType() {
    return type;
  }

  /** {@inheritDoc}
   */
  public void setType(final Type theType) {
    type = theType;
  }

  /** {@inheritDoc}
   */
  public String getUrl() {
    return url;
  }

  /** {@inheritDoc}
   */
  public void setUrl(final String theUrl) {
    url = theUrl;
  }

  /** {@inheritDoc}
   */
  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  /** {@inheritDoc}
   */
  public void setThumbnailUrl(final String url) {
    this.thumbnailUrl = url;
  }

  /** {@inheritDoc}
   */
  public String getAlbumId() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setAlbumId(String albumId) {
  }

  /** {@inheritDoc}
   */
  public String getCreated() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setCreated(String created) {
  }

  /** {@inheritDoc}
   */
  public String getDescription() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setDescription(String description) {
  }

  /** {@inheritDoc}
   */
  public String getDuration() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setDuration(String duration) {
  }

  /** {@inheritDoc}
   */
  public String getFileSize() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setFileSize(String fileSize) {
  }

  /** {@inheritDoc}
   */
  public String getId() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setId(String id) {
  }

  /** {@inheritDoc}
   */
  public String getLanguage() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setLanguage(String language) {
  }

  /** {@inheritDoc}
   */
  public String getLastUpdated() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setLastUpdated(String lastUpdated) {
  }

  /** {@inheritDoc}
   */
  public Address getLocation() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setLocation(Address location) {
  }

  /** {@inheritDoc}
   */
  public String getNumComments() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setNumComments(String numComments) {
  }

  /** {@inheritDoc}
   */
  public String getNumViews() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setNumViews(String numViews) {
  }

  /** {@inheritDoc}
   */
  public String getNumVotes() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setNumVotes(String numVotes) {
  }

  /** {@inheritDoc}
   */
  public String getRating() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setRating(String rating) {
  }

  /** {@inheritDoc}
   */
  public String getStartTime() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setStartTime(String startTime) {
  }

  /** {@inheritDoc}
   */
  public String getTaggedPeople() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setTaggedPeople(String taggedPeople) {
  }

  /** {@inheritDoc}
   */
  public String getTags() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setTags(String tags) {
  }

  /** {@inheritDoc}
   */
  public String getTitle() {
    return null;
  }

  /** {@inheritDoc}
   */
  public void setTitle(String title) {
  }
}

