package com.globant.katari.hibernate.search;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;
import java.util.concurrent.Future;

import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.search.SearchHit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.globant.katari.tools.SpringTestUtilsBase;


/** Unit test for elastic-search hibernate-search integration.*/
public class ElasticsearchTest {

  private MockEntityRepository repository;
  private ElasticSearchClientFactory factory;

  @Before public void setUp() throws Exception {
    repository = (MockEntityRepository) SpringTestUtils.get().getBean(
        "test.mockEntityRepository");

    factory = (ElasticSearchClientFactory) SpringTestUtils.get().getBean(
        "infrastructure.elasticsearchClientFactory");

    SpringTestUtils.get().beginTransaction();

    repository.save(new MockEntity("waabox", "a geek"));
    repository.save(new MockEntity("waabo", "a chinesee copy of waabox"));
    repository.save(new MockEntity("waab", "the second original"));
    repository.save(new MockEntity("mirabelli", "a nerd"));
    repository.save(new MockEntity("graña", "just like gandalf :p"));
    repository.save(new MockEntity("mario roman", "geiiiiiii"));

    SpringTestUtils.get().endTransaction();
  }

  @After public void tearDown() throws Exception {
    SpringTestUtils.get().beginTransaction();
    repository.deleteAll();
    SpringTestUtils.get().endTransaction();
  }

  @Test public void search_untokenizedSorted() throws Exception {
    SpringTestUtils.get().beginTransaction();
    List<MockEntity> entities =  repository.searchLikeName("w");
    assertThat(entities.size(), is(3));
    assertThat(entities.get(0).getName(), is("waab"));
    SpringTestUtils.get().endTransaction();
  }

  @Test public void search_tokenizedSorted() {
    SpringTestUtils.get().beginTransaction();
    List<MockEntity> entities = repository.searchLikeDescription("chinesee");
    assertThat(entities.size(), is(1));
    SpringTestUtils.get().endTransaction();
  }

  @Test public void reindex() throws Exception {
    Client client = factory.get();

    ElasticsearchIndexManager.recreateIndex(MockEntity.class, client);

    SpringTestUtils.get().beginTransaction();
    repository.reindex("com.globant.katari.hibernate.search.MockEntity");
    SpringTestUtils.get().endTransaction();

    SpringTestUtils.get().beginTransaction();
    List<MockEntity> entities =  repository.searchLikeName("w");
    assertThat(entities.size(), is(3));
    SpringTestUtils.get().endTransaction();

  }

  @Test public void testNonEntityIndex() throws Exception {
    Client client = factory.get();
    try {
      TypesExistsRequest typesExistsRequest;
      typesExistsRequest = new TypesExistsRequest(
          new String[] {
              AnotherIndexIndexFactory.NAME
          },
          AnotherIndex.class.getName());
      TypesExistsResponse typesExistsResponse;
      IndicesAdminClient indicesAdmin = client.admin().indices();
      typesExistsResponse = indicesAdmin.typesExists(typesExistsRequest).get();
      assertThat(typesExistsResponse.isExists(), is(true));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    SearchResponse response = client.prepareSearch(
        AnotherIndexIndexFactory.NAME)
        .setTypes(AnotherIndex.class.getName())
        .setFrom(0).setSize(60).setExplain(true)
        .execute()
        .actionGet();

    assertThat(
        (String)response.getHits().getHits()[0].getSource().get("anotherName"),
        is("Hello there!"));

    SpringTestUtils.get().beginTransaction();
    MockEntity mockEntity = repository.getByNameDirectSQL("waabox");
    mockEntity.changeAnotherName("foo");
    repository.merge(mockEntity);
    SpringTestUtils.get().endTransaction();

    ListenableActionFuture<?> future = client.prepareSearch(
        AnotherIndexIndexFactory.NAME)
        .setTypes(AnotherIndex.class.getName())
        .setFrom(0).setSize(60).setExplain(true)
        .execute();

    waitFor(future);

    response = (SearchResponse) future.get();

    boolean fail = true;
    for(SearchHit h :response.getHits().getHits()) {
      if (h.getSource().get("anotherName").equals("foo")) {
        fail = false;
      }
    }
    assertFalse("Entity not found!", fail);
  }

  /** Sleeps the current thread 100 ms.
   * @param future the future to wait for.
   * */
  private void waitFor(final Future<?> future) {
    while (!future.isDone()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }


  private static class SpringTestUtils extends SpringTestUtilsBase {

    private static SpringTestUtils INSTANCE = new SpringTestUtils();

    protected SpringTestUtils() {
      super(new String[] {
          "classpath:/com/globant/katari/hibernate/searchApplicationContext.xml"
      }, new String[]{});
    }

    private static SpringTestUtils get() {
      return INSTANCE;
    }

  }

}
