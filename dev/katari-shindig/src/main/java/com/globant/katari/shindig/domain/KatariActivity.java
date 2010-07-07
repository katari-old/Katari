/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import org.apache.commons.lang.Validate;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.persistence.ElementCollection;
import javax.persistence.CollectionTable;
import javax.persistence.FetchType;
import javax.persistence.MapKeyColumn;

/** An implementation of the shindig Activity interfase, that represents an
 * open social activity.
 *
 * Note: this implementation follows the shindig defined interface. We consider
 * a bad practice to indiscriminatelly expose the object attributes with
 * get/set operations.
 */
@Entity
@Table(name = "activity")
public class KatariActivity implements Activity {

  /** The id of the activity.
   *
   * This is 0 for a newly created activity.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private long id = 0;

  /** The hibernate version for optimistic locking.
   */
  @Version
  @Column(name = "version")
  protected long version;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "app_id", length = 255)
  protected String appId;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "body", length = 255)
  protected String body;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "body_id", length = 255)
  protected String bodyId;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "external_id", length = 255)
  protected String externalId;

  /** model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "updated")
  @Temporal(TemporalType.TIMESTAMP)
  protected Date updated;

  /**
   * A list of shared media items associated with this activity, joined by the
   * table "activity_media" such that activity_media.activity_id = activity.oid
   * and activity_media.media_id = media.oid. Media items may be shared amongst
   * many activities or other entities.
   */
  @ManyToMany(targetEntity = KatariMediaItem.class, cascade = CascadeType.ALL)
  @JoinTable(name = "activity_media",
      joinColumns = @JoinColumn(name = "activity_id"),
      inverseJoinColumns = @JoinColumn(name = "media_item_id"))
  protected List<MediaItem> mediaItems;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "posted_time")
  protected Long postedTime;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "priority")
  protected Float priority;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "stream_favicon_url", length = 255)
  protected String streamFaviconUrl;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "stream_source_url", length = 255)
  protected String streamSourceUrl;

  /** model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "stream_url", length = 255)
  protected String streamUrl;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "stream_title", length = 255)
  protected String streamTitle;

  /** The template parameters associated to this activity.
   *
   * This is never null.
   */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable( name="template_parameters",
      joinColumns = @JoinColumn(name = "activity_id"))
  @MapKeyColumn(name = "name")
  @Column(name = "value")
  private Map<String, String> templateParameters = new HashMap<String,
          String>();

  /** model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "title", length = 255)
  protected String title;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "title_id", length = 255)
  protected String titleId;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "url", length = 255)
  protected String url;

  /**
   * TODO This should be a fk to the user.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "user_id", length = 255)
  protected String userId;

  /** Needed by hibernate.
   */
  KatariActivity() {
  }

  /** Creates an instance of the activity copying all the elements from the
   * source activity.
   *
   * The new entity is a 'detached' entity (see hibernate). If the id of the
   * provided source is 0, this is a new entity. Otherwise, the copy represents
   * an already persisted entity.
   *
   * @param source The source activity. It cannot be null.
   */
  public KatariActivity(final long thePostedTime, final String applicationId,
      final String theUserId, final Activity source) {

    Validate.notNull(source, "The source activity cannot be null.");

    postedTime = thePostedTime;
    appId = applicationId;
    userId = theUserId;

    if (source.getId() == null) {
      id = 0;
    } else {
      id = Long.parseLong(source.getId());
    }
    bodyId = source.getBodyId();
    body = source.getBody();
    externalId = source.getExternalId();
    titleId = source.getTitleId();
    title = source.getTitle();
    updated = new Date();
    priority = source.getPriority();
    streamFaviconUrl = source.getStreamFaviconUrl();
    streamSourceUrl = source.getStreamSourceUrl();
    streamTitle = source.getStreamTitle();
    streamUrl = source.getStreamUrl();
    url = source.getUrl();

    if(source.getMediaItems() != null) {
      List<MediaItem> items = new ArrayList<MediaItem>();
      for(MediaItem sourceItem : source.getMediaItems()) {
        KatariMediaItem mediaItem = new KatariMediaItem(sourceItem);
        items.add(mediaItem);
      }
      mediaItems = items;
    }
    if (source.getTemplateParams() != null) {
      templateParameters = new HashMap<String, String>();
      templateParameters.putAll(source.getTemplateParams());
    }
  }

  public KatariActivity(long id, String userId) {
    this.id = id;
    this.userId = userId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getAppId()
   */
  public String getAppId() {
    return appId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setAppId(java.lang.String)
   */
  public void setAppId(String appId) {
    this.appId = appId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getBody()
   */
  public String getBody() {
    return body;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setBody(java.lang.String)
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getBodyId()
   */
  public String getBodyId() {
    return bodyId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setBodyId(java.lang.String)
   */
  public void setBodyId(String bodyId) {
    this.bodyId = bodyId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getExternalId()
   */
  public String getExternalId() {
    return externalId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setExternalId(java.lang.String)
   */
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getId()
   */
  public String getId() {
    if (id == 0) {
      return null;
    } else {
      return Long.toString(id);
    }
  }

  /** {@inheritDoc}
   */
  public void setId(final String stringId) {
    if (stringId == null) {
      id = 0;
    } else {
      id = Long.parseLong(stringId);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getUpdated()
   */
  public Date getUpdated() {
    if (updated == null) {
      return null;
    }
    return new Date(updated.getTime());
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setUpdated(java.util.Date)
   */
  public void setUpdated(Date updated) {
    if (updated == null) {
      this.updated = null;
    } else {
      this.updated = new Date(updated.getTime());
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getMediaItems()
   */
  public List<MediaItem> getMediaItems() {
    return null;
    // return mediaItems;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setMediaItems(java.util.List)
   */
  public void setMediaItems(List<MediaItem> mediaItems) {
    // this.mediaItems = mediaItems;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getPostedTime()
   */
  public Long getPostedTime() {
    return postedTime;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setPostedTime(java.lang.Long)
   */
  public void setPostedTime(Long postedTime) {
    this.postedTime = postedTime;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getPriority()
   */
  public Float getPriority() {
    return priority;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setPriority(java.lang.Float)
   */
  public void setPriority(Float priority) {
    this.priority = priority;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getStreamFaviconUrl()
   */
  public String getStreamFaviconUrl() {
    return streamFaviconUrl;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setStreamFaviconUrl(java.lang.String)
   */
  public void setStreamFaviconUrl(String streamFaviconUrl) {
    this.streamFaviconUrl = streamFaviconUrl;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getStreamSourceUrl()
   */
  public String getStreamSourceUrl() {
    return streamSourceUrl;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setStreamSourceUrl(java.lang.String)
   */
  public void setStreamSourceUrl(String streamSourceUrl) {
    this.streamSourceUrl = streamSourceUrl;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getStreamTitle()
   */
  public String getStreamTitle() {
    return streamTitle;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setStreamTitle(java.lang.String)
   */
  public void setStreamTitle(String streamTitle) {
    this.streamTitle = streamTitle;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getStreamUrl()
   */
  public String getStreamUrl() {
    return streamUrl;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setStreamUrl(java.lang.String)
   */
  public void setStreamUrl(String streamUrl) {
    this.streamUrl = streamUrl;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getTemplateParams()
   */
  public Map<String, String> getTemplateParams() {
    return templateParameters;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setTemplateParams(java.util.Map)
   */
  public void setTemplateParams(Map<String, String> templateParams) {
    templateParameters = templateParams;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getTitle()
   */
  public String getTitle() {
    return title;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setTitle(java.lang.String)
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getTitleId()
   */
  public String getTitleId() {
    return titleId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setTitleId(java.lang.String)
   */
  public void setTitleId(String titleId) {
    this.titleId = titleId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getUrl()
   */
  public String getUrl() {
    return url;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setUrl(java.lang.String)
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#getUserId()
   */
  public String getUserId() {
    return userId;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.shindig.social.opensocial.model.Activity#setUserId(java.lang.String)
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }
}

