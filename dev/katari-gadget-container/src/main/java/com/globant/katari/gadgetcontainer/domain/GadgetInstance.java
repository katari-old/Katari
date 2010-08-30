/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.gadgetcontainer.domain;

import org.apache.commons.lang.Validate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;

import com.globant.katari.shindig.domain.Application;

/** Represents an instance of a {@link Gadget}.
 *
 * This one contains the information that the xml-to-html shinding
 * implementations needs to perform the rendering, also gives the rpc support.
 *
 * Even there are no mutators, this class is intended to be eventually mutable.
 *
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 */
@Entity
@Table(name = "gadget_instances")
public class GadgetInstance {

  /** The id of the gadget instance, 0 for a newly created gadget instance.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  /** The application corresponding to this gadget instance.
   *
   * It is never null.
   */
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Application application;

  /** The column where the gadget is to be positioned in the group.
   */
  @Column(name = "group_column", nullable = false)
  private int column;

  /** The order of the gadget is its column.
   */
  @Column(name = "group_order", nullable = false)
  private int order;

  /** Hibernate constructor.
   */
  GadgetInstance() {
  }

  /** Creates a new Gadget instance.
   *
   * @param theApplication the application for this gadget instance. Cannot be
   * null.
   *
   * @param theColumn the column where the gadget is to be positioned in the
   * group. It starts from 0, and must be smaller than the number of columns in
   * the containing group.
   *
   * @param theOrder the order of the gadget in the column. Two gadgets with
   * the same vaule appears in an undefined order.
   */
  public GadgetInstance(final Application theApplication, final int theColumn,
      final int theOrder) {
    Validate.notNull(theApplication, "the application can not be null");
    application = theApplication;
    column = theColumn;
    order = theOrder;
  }

  /** Creates a new Gadget instance as a copy of another.
   *
   * This is intended to be used from GadgetGroup only. It does not copy the id
   * of the source gadget.
   *
   * @param source the source gadget instance. It cannot be null.
   */
  GadgetInstance(final GadgetInstance source) {
    Validate.notNull(source, "the source gadget can not be null");
    application = source.application;
    column = source.column;
    order = source.order;
  }

  /** @return long the id of the gadget instance.
   */
  public long getId() {
    return id;
  }

  /** @return {@link String} location of the gadget xml spec.
   */
  public Application getApplication() {
    return application;
  }

  /** The title of the gadget.
   *
   * @return The title of gadget, usually obtained from the gadget xml
   * specification. Never returns null.
   */
  public String getTitle() {
    return application.getTitle();
  }

  /** The url of the gadget.
   *
   * @return The url of gadget, usually obtained from the gadget xml
   * specification. Null if not known.
   */
  public String getIcon() {
    return application.getIcon();
  }

  /** Returns the url for the gadget xml.
   *
   * @return the url, never null.
   */
  public String getUrl() {
    return application.getUrl();
  }

  /** The column where the gadget is to be positioned in the group.
   * 
   * It starts from 0, and must be smaller than the number of columns in the
   * containing group.
   *
   * @return the column of the gadget.
   */
  public int getColumn() {
    return column;
  }
 
  /** The order of the gadget in the column.
   *
   * Two gadgets with the same vaule appears in an undefined order.
   *
   * @return the order of the gadget.
   */
  public int getOrder() {
    return order;
  }

  /** Moves the gadget to the specified column and order.
   *
   * This is intended to be called only from GadgetGroup.
   *
   * @param newColumn the column to move the gadget to.
   *
   * @param newOrder the new position of the gadget in the column.
   */
  void move(final int newColumn, final int newOrder) {
    column = newColumn;
    order = newOrder;
  }
}

