/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.spring;

import org.springframework.beans.factory.config.MapFactoryBean;

import java.util.Map;

/** A spring factory bean that builds a map from two source maps.
 *
 * This factory uses a source map and an overriding optional map. The map
 * created by this factory is a copy of the source map with the keys in the
 * overriding map added to, or replacing the keys in the source map.
 */
public class OverridingMapFactoryBean extends MapFactoryBean {

  /** The map with the keys to add or replace the existing keys in the source
   * map.
   *
   * It can be null, in with case the source map is unmodified.
   */
  @SuppressWarnings("unchecked")
  private Map overridingMap = null;

  /** Set the overriding map.
   *
   * @param theOverridingMap the overriding map. If null, the factory creates
   * the map based on the source map.
   */
  @SuppressWarnings("unchecked")
  public void setOverridingMap(final Map theOverridingMap) {
    overridingMap = theOverridingMap;
  }

  /** Creates a map with the keys in the source map overriden by the keys in
   * the overriding map.
   *
   * @return The new map, never null.
   */
  @SuppressWarnings("unchecked")
  protected Object createInstance() {
    Map map = (Map) super.createInstance();
    if (overridingMap != null) {
      map.putAll(overridingMap);
    }
    return map;
  }
}

