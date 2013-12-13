package com.globant.katari.hibernate;

public class OneEmbeddedFactory {
  OneEmbedded create() {
    return new OneEmbedded("hello there");
  }
}
