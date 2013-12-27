package com.globant.katari.hibernate.search;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.elasticsearch.common.xcontent.XContentBuilder;

/** Fieldable for Lists. This type of Fieldable should solely be used when the
 * final purpose is to transform the Lucene Document Field to an Elastic Search
 * Document Field and never to map an object directly to a Lucene Document
 * Field. */
public class ListElasticsearchFieldable implements ElasticsearchFieldable {

  private static final long serialVersionUID = 1L;

  /** The List that the Fieldable represents. Never null.*/
  private final List<?> value;

  /** The name of the field. Never null*/
  private final String name;

  /** ListElasticSearchFieldable constructor.
   *
   * @param theName The name of the field, can not be null or empty.
   * @param theValue The value of the field, can not be null.
   */
  public ListElasticsearchFieldable(final String theName,
      final List<?> theValue) {
    Validate.notEmpty(theName, "The field name can not be null or "
      + "empty.");
    Validate.notNull(theValue, "The field value can not be null.");
    name = theName;
    value = theValue;
  }

  /** {@inheritDoc}*/
  @Override
  public void appendTo(final XContentBuilder builder) {
    try {
      builder.array(name, value.toArray());
    } catch (IOException e) {
      throw new RuntimeException("Unable to append list to builder.", e);
    }
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   *  {@inheritDoc}
   */
  @Override
  public void setBoost(final float boost) {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public float getBoost() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public String name() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public String stringValue() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public Reader readerValue() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public TokenStream tokenStreamValue() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public boolean isStored() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public boolean isIndexed() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public boolean isTokenized() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public boolean isTermVectorStored() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public boolean isStoreOffsetWithTermVector() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public boolean isStorePositionWithTermVector() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public boolean isBinary() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public boolean getOmitNorms() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public void setOmitNorms(final boolean omitNorms) {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public boolean isLazy() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public int getBinaryOffset() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public int getBinaryLength() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public byte[] getBinaryValue() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public byte[] getBinaryValue(final byte[] result) {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public IndexOptions getIndexOptions() {
    throw new UnsupportedOperationException("The operation is not available");
  }

  /** Not Implemented and thus throws an UnsupportedOperationException.
   * {@inheritDoc}
   */
  @Override
  public void setIndexOptions(final IndexOptions indexOptions) {
    throw new UnsupportedOperationException("The operation is not available");
  }

}
