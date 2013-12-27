package com.globant.katari.hibernate.search;

import java.lang.reflect.Modifier;
import java.util.List;

import java.io.Serializable;

import org.apache.commons.lang.Validate;
import org.apache.lucene.document.Document;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices
  .IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices
  .IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** Factory for those entities that needs special behavior.*/
public abstract class ElasticSearchIndexFactory {

  /** The class logger.*/
  private static Logger log = LoggerFactory.getLogger(
    ElasticSearchIndexFactory.class);

  /** The name of the index that the factory manages, never null.*/
  private final String indexName;

  /** Elastic Search Index Factory Constructor.
   *
   * @param theIndexName The name of the index that will be managed by the
   * factory. Can not be null or empty.
   */
  public ElasticSearchIndexFactory(final String theIndexName) {
    Validate.notEmpty(theIndexName, "The index name cannot be null or empty");
    indexName = theIndexName;
  }

  /** Creates the index and all the required mappings.
   *
   * @param client The client required to perform the index creation. Can not
   * be null.
   * @param thePersistentClasses The list of persistent classes. Can not be
   * null.
   */
  public void create(final Client client,
      final List<Class<?>> persistentClasses) {
    Validate.notNull(client, "The client cannot be null");

    IndicesAdminClient indicesAdmin = client.admin().indices();
    if (!indexExists(client)) {
      CreateIndexRequest createIndexRequest;
      createIndexRequest = new CreateIndexRequest(indexName);
      CreateIndexResponse createIndexResponse;
      try {
        createIndexResponse = indicesAdmin.create(createIndexRequest).get();
        if (!createIndexResponse.acknowledged()) {
          throw new RuntimeException("The " + indexName + " index creation "
            + "has not been acknowledged by all nodes");
        }
      } catch (Exception e) {
        throw new RuntimeException("Unable to create index " + indexName, e);
      }
    }
    waitFor(client);

    //Create Mapping for each persistent class that is supported by the index
    //factory and is not abstract.
    for (Class<?> clazz : persistentClasses) {
      if (!Modifier.isAbstract(clazz.getModifiers())
        && supports(clazz)) {
        mapping(client, clazz);
      }
    }
  }

  /** Deletes the index and all its content.
   *
   * @param client The client required to perform the index delete. Can not
   * be null.
   */
  public void delete(final Client client) {
    Validate.notNull(client, "The client cannot be null");
    IndicesAdminClient indicesAdminClient = client.admin().indices();

    if (indexExists(client)) {
      try {
        DeleteIndexRequest deleteRequest;
        deleteRequest = new DeleteIndexRequest(indexName);
        DeleteIndexResponse deleteResponse;
        deleteResponse = indicesAdminClient.delete(deleteRequest).get();
        if (!deleteResponse.acknowledged()) {
          throw new RuntimeException("Delete of " + indexName + " index "
            + "has not been acknowledged by all nodes");
        }
      } catch (Exception e) {
        throw new RuntimeException("Unable to delete index " + indexName, e);
      }
    }
  }

  /** Define the mapping for an entity class supported by the index.
   *
   * @param client the elastic search client, cannot be null.
   * @param entityClass the entity class for which the mapping will be defined,
   * can not be null.
   */
  private void mapping(final Client client, final Class<?> entityClass) {
    if (getMappingBuilder(entityClass) != null) {
      try {
        PutMappingRequest putMappingRequest;
        putMappingRequest = new PutMappingRequest(indexName);
        putMappingRequest.type(entityClass.getName());
        putMappingRequest.source(getMappingBuilder(entityClass));
        putMappingRequest.ignoreConflicts(true);
        PutMappingResponse putMappingResponse;
        IndicesAdminClient indicesAdmin = client.admin().indices();
        putMappingResponse = indicesAdmin.putMapping(putMappingRequest).get();
        if (!putMappingResponse.acknowledged()) {
          throw new RuntimeException("Mapping definition over " + indexName
            + " index for type " + entityClass.getClass().getName()
            + " has not been acknowledged by all nodes");
        }
      } catch (Exception e) {
        throw new RuntimeException("Unable to define mapping for index "
          + indexName + " and type " + entityClass.getClass().getName(), e);
      }
    }
  }

 /** Retrieves the index name.
   *
   * @return a String with the name of the index. Never null.
   */
  public String getIndexName() {
    return indexName;
  }

  /**  Retrieves the mapping builder of the given class.
   *
   * @return An XContentBuilder with the expected mapping of the type. Never
   * null if the element represents a type inside the index and null otherwise.
   */
  protected abstract XContentBuilder getMappingBuilder(
    final Class<?> entityClass);

  /** Determines if the index exists.
   *
   * @param client The client required to perform the index creation. Can not
   * be null.
   * @return true if the index exists, false otherwise.
   */
  private boolean indexExists(final Client client) {
    IndicesAdminClient indicesAdmin = client.admin().indices();
    IndicesExistsRequest indicesExistsRequest;
    indicesExistsRequest = new IndicesExistsRequest(indexName);
    IndicesExistsResponse indicesExistsResponse;
    try {
      indicesExistsResponse = indicesAdmin.exists(indicesExistsRequest).get();
    } catch (Exception e) {
      throw new RuntimeException("Unable to determine the existance of the "
        + indexName + " index", e);
    }
    return indicesExistsResponse.isExists();
  }

  /** Creates a lucene document based on the given entity.
   * @param entity the entity, cannot be null.
   * @param id the id, cannot be null.
   * @return the lucene document.
   */
  public abstract Document createDocument(final Object entity,
      final Serializable id);

  /** Checks if the given class is supported or not by this implementation.
   * @param entityClass the entity class to check.
   * @return true if the given class is supported.
   */
  public abstract boolean supports(final Class<?> entityClass);

  /** Retrieves the type that this factory handles.
   * @return the type that this factory handles.
   */
  public abstract Class<?> getType();

  /** Wait until the given index finish its initialization.
   * @param indexName the index, cannot be null.
   * @param client the elasticsearch client, cannot be null.
   */
  private void waitFor(final Client client) {
    Validate.notNull(client, "The client cannot be null");

    log.debug("waiting...");
    ClusterHealthRequest healthRequest = new ClusterHealthRequest(
        indexName);
    healthRequest.waitForYellowStatus();
    client.admin().cluster().health(healthRequest).actionGet();
    log.debug("done!, index named:" + indexName + " created");
  }

  /** Extracts the appropriate type of the entity to be applied over the index
   * that the index factory represents.
   *
   * @param entity The entity from which we need to infer the type, can not be
   * null.
   * @return The class that represents the type of the entity to be persisted
   * in the index.
   */
  public abstract Class<?> extractDataType(final Object entity);
}
