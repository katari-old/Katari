package com.globant.katari.hibernate.search;

import org.apache.lucene.document.Fieldable;
import org.elasticsearch.common.xcontent.XContentBuilder;

/** A Fieldable is a Lucene representation of a field inside a lucene document.
 *  This interface allows handling specific data types that can't be
 *  transformed directly into elastic search document fields.
 */
public interface ElasticsearchFieldable extends Fieldable {

  /** Allows appending a Fieldable to the given builder.
   *
   * @param builder An XContentBuilder to which we will append the Fieldable.
   */
  void appendTo(final XContentBuilder builder);

}
