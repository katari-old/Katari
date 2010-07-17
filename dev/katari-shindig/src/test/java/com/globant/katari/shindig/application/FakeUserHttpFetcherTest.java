/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.shindig.application;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static org.easymock.classextension.EasyMock.*;

import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.uri.Uri;
import org.apache.shindig.config.ContainerConfig;
import org.apache.shindig.gadgets.http.HttpFetcher;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;

public class FakeUserHttpFetcherTest {

  private KatariActivityService service;

  ContainerConfig config;
  BlobCrypter crypter;

  @Before
  public void setUp() {
    LinkedList<String> containers = new LinkedList<String>();
    containers.add("default");
    config = createMock(ContainerConfig.class);
    expect(config.getContainers()).andReturn(containers);
    replay(config);
    crypter = createMock(BlobCrypter.class);
  }

  @Test
  public void testFetch_noToken() throws Exception {

    Uri uri = Uri.parse("http://host?p=1");

    HttpRequest request = createMock(HttpRequest.class);
    expect(request.getUri()).andReturn(uri);
    expect(request.setUri(Uri.parse("http://host?p=1&st=default%3Anull")))
        .andReturn(request);
    replay(request);

    HttpResponse response = new HttpResponse();

    HttpFetcher fetcher = createMock(HttpFetcher.class);
    expect(fetcher.fetch(request)).andReturn(response);
    replay(fetcher);

    FakeUserHttpFetcher fakeFetcher;
    fakeFetcher = new FakeUserHttpFetcher(config, fetcher, crypter);
    fakeFetcher.fetch(request);

    verify(request);
    verify(fetcher);
  }

  @Test
  public void testFetch_withToken() throws Exception {

    Uri uri = Uri.parse("http://host?p=1&st=sometoken");

    // We should get the request untouched.
    HttpRequest request = createMock(HttpRequest.class);
    expect(request.getUri()).andReturn(uri);
    replay(request);

    HttpResponse response = new HttpResponse();

    HttpFetcher fetcher = createMock(HttpFetcher.class);
    expect(fetcher.fetch(request)).andReturn(response);
    replay(fetcher);

    FakeUserHttpFetcher fakeFetcher;
    fakeFetcher = new FakeUserHttpFetcher(config, fetcher, crypter);
    fakeFetcher.fetch(request);

    verify(request);
    verify(fetcher);
  }
}

