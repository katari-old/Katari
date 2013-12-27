package com.globant.katari.hibernate.search;

import java.io.Serializable;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

/** Another index factory. */
public class AnotherIndexIndexFactory extends ElasticSearchIndexFactory {

  public static final String NAME = "another";

  public AnotherIndexIndexFactory(final String theIndexName) {
    super(theIndexName);
  }

  /** {@inheritDoc}. */
  @Override
  protected XContentBuilder getMappingBuilder(final Class<?> entityClass) {
    XContentBuilder mappingBuilder;
    try {
      mappingBuilder = XContentFactory.jsonBuilder();
      mappingBuilder.startObject();
      mappingBuilder.startObject(getType().getName());
      mappingBuilder.startObject("properties");
      mappingBuilder.startObject("anotherName").field("type").value("string")
        .endObject();
      mappingBuilder.endObject().endObject().endObject();
      return mappingBuilder;
    } catch (Exception e) {
      throw new RuntimeException("Unable to create the mapping builder", e);
    }
  }

  /** {@inheritDoc}. */
  @Override
  public Document createDocument(final Object entity,
      final Serializable id) {
    AnotherIndex a = (AnotherIndex) entity;
    Document document = new Document();
    Field f = new Field("anotherName", a.getAnotherName(), Field.Store.YES,
        Field.Index.ANALYZED);
    document.add(f);
    return document;
  }

  /** {@inheritDoc}. */
  @Override
  public boolean supports(final Class<?> entityClass) {
    return AnotherIndex.class.isAssignableFrom(entityClass);
  }

  /** {@inheritDoc}. */
  @Override
  public Class<?> getType() {
    return AnotherIndex.class;
  }

  @Override
  public Class<?> extractDataType(final Object entity) {
    return AnotherIndex.class;
  }

}
