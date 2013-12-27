package com.globant.katari.hibernate.search;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.junit.Before;
import org.junit.Test;


public class ListStringBridgeTest {

  private Document document;
  private LuceneOptions luceneOptions;

  @Before
  public void setUp() {
    document = new Document();
    luceneOptions = createMock(LuceneOptions.class);
    luceneOptions.addFieldToDocument("name", "1", document);
    expectLastCall().once();
    luceneOptions.addFieldToDocument("name", "2", document);
    expectLastCall().once();
    luceneOptions.addFieldToDocument("name", "3", document);
    expectLastCall().once();
  }


  @Test
  public void set() {
    replay(luceneOptions);
    List<String> value = new ArrayList<String>();
    value.add("1");
    value.add("2");
    value.add("3");
    new ListStringBridge().set("name", value, document, luceneOptions);
    verify(luceneOptions);
  }

  @Test
  public void set_nullValue() {
    new ListStringBridge().set("name", null, document, luceneOptions);
  }

  @Test (expected = IllegalArgumentException.class)
  public void set_illegalValueType() {
    new ListStringBridge().set("name", new HashSet<String>(),
        document, luceneOptions);
  }
}
