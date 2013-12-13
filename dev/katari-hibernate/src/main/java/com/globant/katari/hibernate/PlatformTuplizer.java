package com.globant.katari.hibernate;

import java.lang.reflect.Method;

import org.hibernate.tuple.Tuplizer;

/** Base tuplizer. */
public interface PlatformTuplizer extends Tuplizer {

  /** Initilize the tuplizer.
   * @param factory the factory.
   * @param method the method.
   */
  void initialize(final Object factory, final Method method);

}
