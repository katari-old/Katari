package com.globant.katari.gadgetcontainer.domain;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.Validate.notEmpty;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Represents a page with gadgets.
 * 
 * @author waabox(emiliano[dot]arango[at]globant[dot]com)
 *
 */
@Entity
@Table(name = "gadget_group")
public class GadgetGroup {
  
  /** id of the page.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  /** {@link String} name of the page.
   */
  @Column(nullable = false)
  private String name;
  
  /** {@link Set<GadgetInstance>} gadgets of the page.
   * it's never null.
   */
  @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
  private Set<GadgetInstance> gadgets = new HashSet<GadgetInstance>();
  
  /** {@link String} the owner of this gadget instance. 
   */
  @Column(nullable = false)
  private String canvasUser;
  
  /** Hibernate constructor.
   */
  GadgetGroup() {
  }
  
  /** Constructor.
   * 
   * @param user {@link CanvasUser} the user. Can not be null.
   * @param pageName {@link String} name of the page. Can not be null
   * @param pageGadgets {@link Set<GadgetInstance>} page's gadgets. Can be null,
   * if null then a new Set of GadgetInstance will be created.
   */
  public GadgetGroup(final String user, final String pageName,
      final Set<GadgetInstance> pageGadgets) {
    notEmpty(pageName, "page name can not be null");
    notEmpty(user, "canvas user can not be null");
    if(isNotEmpty(pageGadgets)) {
      gadgets = pageGadgets;
    }
    name = pageName;
    canvasUser = user;
  }
  
  /** @return long the id.
   */
  public long getId() {
    return id;
  }
  
  /** @return {@link String} the name.
   */
  public String getName() {
    return name;
  }
  
  /** @return {@link List<GadgetInstance>} the gadgets. It never returns null.
   */
  public Set<GadgetInstance> getGadgets() {
    return gadgets;
  }
  
  /** @param instance {@link GadgetInstance}
   */
  public void addGadget(final GadgetInstance instance) {
    gadgets.add(instance);
  }
  
  /**@return @link{String} the canvasUser
   */
  public String getCanvasUser() {
    return canvasUser;
  }
}
