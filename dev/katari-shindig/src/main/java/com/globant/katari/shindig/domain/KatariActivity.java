/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.domain;

import org.apache.commons.lang.Validate;

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
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.ElementCollection;
import javax.persistence.CollectionTable;
import javax.persistence.FetchType;
import javax.persistence.MapKeyColumn;

import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;

import com.globant.katari.hibernate.coreuser.domain.CoreUser;

/** An implementation of the shindig Activity interfase, that represents an
 * open social activity.
 *
 * Note: this implementation follows the shindig defined interface. We consider
 * a bad practice to indiscriminatelly expose the object attributes with
 * get/set operations.
 */
@Entity
@Table(name = "shindig_activities")
public class KatariActivity implements Activity {

  /** The id of the activity.
   *
   * This is 0 for a newly created activity.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private long id = 0;

  /** The application that generated this activity.
   *
   * This is never null.
   */
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Application application;

  /** The user that generated this activity.
   *
   * This is never null.
   */
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private CoreUser user;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "body", length = 255)
  private String body;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "body_id", length = 255)
  private String bodyId;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "external_id", length = 255)
  private String externalId;

  /** model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "updated")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updated;

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
  private List<MediaItem> mediaItems;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "posted_time")
  private Long postedTime;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "priority")
  private Float priority;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "stream_favicon_url", length = 255)
  private String streamFaviconUrl;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "stream_source_url", length = 255)
  private String streamSourceUrl;

  /** model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "stream_url", length = 255)
  private String streamUrl;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "stream_title", length = 255)
  private String streamTitle;

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
  @Column(name = "title", length = 255, nullable = false)
  private String title;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "title_id", length = 255)
  private String titleId;

  /**
   * model field.
   *
   * @see org.apache.shindig.social.opensocial.model.Activity
   */
  @Column(name = "url", length = 255)
  private String url;

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
   * @param thePostedTime when the activity was posted, as the number of
   * milliseconds since the epoch.
   *
   * @param theApplication the application that generated the activity. It
   * cannot be null.
   *
   * @param theUser the user that generated this activity. It cannot be null.
   *
   * @param source The source activity. It cannot be null. The source activity
   * title must not be null. The id, userId, appId and postedTime of the source
   * activity are ignored.
   */
  public KatariActivity(final long thePostedTime,
      final Application theApplication, final CoreUser theUser,
      final Activity source) {

    Validate.notNull(theApplication, "The application cannot be null.");
    Validate.notNull(theUser, "The user cannot be null.");
    Validate.notNull(source, "The source activity cannot be null.");
    Validate.notNull(source.getTitle(),
        "The source activity title cannot be null.");

    postedTime = thePostedTime;
    application = theApplication;
    user = theUser;
    id = 0;

    title = source.getTitle();
    titleId = source.getTitleId();
    body = source.getBody();
    bodyId = source.getBodyId();
    externalId = source.getExternalId();
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

  /** Shindig expects this to the the application url.
   *
   * {@inheritDoc}
   */
  public String getAppId() {
    return application.getUrl();
  }

  /** {@inheritDoc}
   */
  public void setAppId(final String applicationId) {
    throw new RuntimeException("This is not supported."
       + " Call the constructor instead.");
  }

  /** {@inheritDoc}
   */
  public String getBody() {
    return body;
  }

  /** {@inheritDoc}
   */
  public void setBody(final String theBody) {
    body = theBody;
  }

  /** {@inheritDoc}
   */
  public String getBodyId() {
    return bodyId;
  }

  /** {@inheritDoc}
   */
  public void setBodyId(final String theBodyId) {
    bodyId = theBodyId;
  }

  /** {@inheritDoc}
   */
  public String getExternalId() {
    return externalId;
  }

  /** {@inheritDoc}
   */
  public void setExternalId(final String theExternalId) {
    externalId = theExternalId;
  }

  /** {@inheritDoc}
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

  /** {@inheritDoc}
   */
  public Date getUpdated() {
    if (updated == null) {
      return null;
    }
    return new Date(updated.getTime());
  }

  /** {@inheritDoc}
   */
  public void setUpdated(final Date when) {
    if (when == null) {
      updated = null;
    } else {
      updated = new Date(when.getTime());
    }
  }

  /** {@inheritDoc}
   */
  public List<MediaItem> getMediaItems() {
    return mediaItems;
  }

  /** {@inheritDoc}
   */
  public void setMediaItems(final List<MediaItem> theMediaItems) {
    mediaItems = theMediaItems;
  }

  /** {@inheritDoc}
   */
  public Long getPostedTime() {
    return postedTime;
  }

  /** {@inheritDoc}
   */
  public void setPostedTime(final Long when) {
    postedTime = when;
  }

  /** {@inheritDoc}
   */
  public Float getPriority() {
    return priority;
  }

  /** {@inheritDoc}
   */
  public void setPriority(final Float thePriority) {
    priority = thePriority;
  }

  /** {@inheritDoc}
   */
  public String getStreamFaviconUrl() {
    return streamFaviconUrl;
  }

  /** {@inheritDoc}
   */
  public void setStreamFaviconUrl(final String url) {
    streamFaviconUrl = url;
  }

  /** {@inheritDoc}
   */
  public String getStreamSourceUrl() {
    return streamSourceUrl;
  }

  /** {@inheritDoc}
   */
  public void setStreamSourceUrl(final String url) {
    streamSourceUrl = url;
  }

  /** {@inheritDoc}
   */
  public String getStreamTitle() {
    return streamTitle;
  }

  /** {@inheritDoc}
   */
  public void setStreamTitle(final String titile) {
    streamTitle = titile;
  }

  /** {@inheritDoc}
   */
  public String getStreamUrl() {
    return streamUrl;
  }

  /** {@inheritDoc}
   */
  public void setStreamUrl(final String url) {
    streamUrl = url;
  }

  /** {@inheritDoc}
   */
  public Map<String, String> getTemplateParams() {
    return templateParameters;
  }

  /** {@inheritDoc}
   */
  public void setTemplateParams(final Map<String, String> templateParams) {
    templateParameters = templateParams;
  }

  /** {@inheritDoc}
   */
  public String getTitle() {
    return title;
  }

  /** {@inheritDoc}
   */
  public void setTitle(final String theTitle) {
    title = theTitle;
  }

  /** {@inheritDoc}
   */
  public String getTitleId() {
    return titleId;
  }

  /** {@inheritDoc}
   */
  public void setTitleId(final String id) {
    titleId = id;
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
  public String getUserId() {
    return Long.toString(user.getId());
  }

  /** {@inheritDoc}
   */
  public void setUserId(final String id) {
    throw new RuntimeException("This is not supported."
       + " Call the constructor instead.");
  }
}

