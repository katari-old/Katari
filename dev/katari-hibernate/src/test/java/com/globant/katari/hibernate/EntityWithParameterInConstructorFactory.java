/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.hibernate;

/** Entity factory used for testing. */
public class EntityWithParameterInConstructorFactory {

  public EntityWithParameterInConstructorFactory(
      final EntityWithParameterInConstructorRepository repo) {
  }

  EntityWithParameterInConstructor create() {
    return new EntityWithParameterInConstructor("parameterName");
  }
}

