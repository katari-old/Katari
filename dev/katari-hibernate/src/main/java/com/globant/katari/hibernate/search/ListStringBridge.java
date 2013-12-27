package com.globant.katari.hibernate.search;

import java.util.List;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

/** Custom bridge to separate String list into several field in Lucene index.
 *
 * This bridge should be used when the query is looking for a specific value in
 * a List.
 * */
public class ListStringBridge implements FieldBridge {

  /**{@inheritDoc}.*/
  @SuppressWarnings("unchecked")
  public void set(final String name, final Object value,
      final Document document, final LuceneOptions luceneOptions) {
    if (value == null) {
      return;
    }
    if (!(value instanceof List)) {
      throw new IllegalArgumentException(
          "This FieldBridge only supports string lists.");
    }
    for (String s : (List<String>) value) {
      luceneOptions.addFieldToDocument(name, s, document);
    }
  }

}
