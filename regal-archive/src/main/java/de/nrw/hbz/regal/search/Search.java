/*
 * Copyright 2012 hbz NRW (http://www.hbz-nrw.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.nrw.hbz.regal.search;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class Search {

    @SuppressWarnings("serial")
    class InvalidRangeException extends RuntimeException {
    }

    @SuppressWarnings("serial")
    class SyncSearchCallException extends RuntimeException {
	public SyncSearchCallException(Throwable e) {
	    super(e);
	}
    }

    Client client = null;

    /**
     * Used for testing
     */
    public Search() {

	Node node = nodeBuilder().local(true).node();
	client = node.client();

    }

    /**
     * @param cluster
     *            the name must match to the one provided in
     *            elasticsearch/conf/elasticsearch.yml
     */
    public Search(String cluster) {
	InetSocketTransportAddress server = new InetSocketTransportAddress(
		"localhost", 9300);
	client = new TransportClient(ImmutableSettings.settingsBuilder()
		.put("cluster.name", cluster).build())
		.addTransportAddress(server);

	// Node node = nodeBuilder().clusterName(cluster).client(true).node();
	// client = node.client();

    }

    /**
     * @param index
     *            name of the elasticsearch index. will be created, if not
     *            exists.
     * @param type
     *            the type of the indexed item
     * @param id
     *            the id of the indexed item
     * @param data
     *            the actual item
     * @return the Response
     */
    public ActionResponse indexSync(String index, String type, String id,
	    String data) {

	try {
	    return indexAsync(index, type, id, data).get();
	} catch (InterruptedException e) {
	    throw new SyncSearchCallException(e);
	} catch (ExecutionException e) {
	    throw new SyncSearchCallException(e);
	}
    }

    private Future<ActionResponse> indexAsync(String index, String type,
	    String id, String data) {
	ElasticsearchFuture f = new ElasticsearchFuture();
	f.exec(client.prepareIndex(index, type, id).setSource(data).execute()
		.actionGet());
	return f;
    }

    /**
     * @param index
     *            name of the elasticsearch index. will be created, if not
     *            exists.
     * @param type
     *            the type of the indexed item
     * @param from
     *            use from and until to page through the results
     * @param until
     *            use from and until to page through the results
     * @return hits for the search
     */
    public SearchHits listResources(String index, String type, int from,
	    int until) {
	if (from >= until)
	    throw new InvalidRangeException();

	SearchRequestBuilder builder = null;
	if (index == null || index.equals(""))
	    builder = client.prepareSearch();
	else
	    builder = client.prepareSearch(index);
	if (type != null && !type.equals(""))
	    builder.setTypes(type);

	builder.setFrom(from).setSize(until - from);

	SearchResponse response = builder.execute().actionGet();

	return response.getHits();
    }

    /**
     * Gives a list of id's
     * 
     * @param index
     *            name of the elasticsearch index. will be created, if not
     *            exists.
     * @param type
     *            the type of the indexed item
     * @param from
     *            use from and until to page through the results
     * @param until
     *            use from and until to page through the results
     * @return a list of ids
     */
    public List<String> listIds(String index, String type, int from, int until) {
	SearchHits hits = listResources(index, type, from, until);
	Iterator<SearchHit> it = hits.iterator();
	List<String> list = new Vector<String>();
	while (it.hasNext()) {
	    SearchHit hit = it.next();
	    list.add(hit.getId());
	}
	return list;
    }

    /**
     * Deletes a certain item
     * 
     * @param index
     *            name of the elasticsearch index. will be created, if not
     *            exists.
     * @param type
     *            the type of the indexed item
     * @param id
     *            the item's id
     * @return the response
     */
    public ActionResponse deleteSync(String index, String type, String id) {
	try {
	    return deleteAsync(index, type, id).get();
	} catch (InterruptedException e) {
	    throw new SyncSearchCallException(e);
	} catch (ExecutionException e) {
	    throw new SyncSearchCallException(e);
	}
    }

    private ElasticsearchFuture deleteAsync(String index, String type, String id) {
	ElasticsearchFuture f = new ElasticsearchFuture();
	f.exec(client.prepareDelete(index, type, id)
		.setOperationThreaded(false).execute().actionGet());
	return f;
    }

    class ElasticsearchFuture implements Future<ActionResponse> {
	private volatile ActionResponse result = null;
	private volatile boolean cancelled = false;
	private final CountDownLatch countDownLatch;

	public ElasticsearchFuture() {
	    countDownLatch = new CountDownLatch(1);
	}

	@Override
	public boolean cancel(final boolean mayInterruptIfRunning) {
	    if (isDone()) {
		return false;
	    } else {
		countDownLatch.countDown();
		cancelled = true;
		return !isDone();
	    }
	}

	@Override
	public ActionResponse get() throws InterruptedException,
		ExecutionException {
	    countDownLatch.await();
	    return result;
	}

	@Override
	public ActionResponse get(long timeout, TimeUnit unit)
		throws InterruptedException, ExecutionException,
		TimeoutException {
	    countDownLatch.await(timeout, unit);
	    return result;
	}

	@Override
	public boolean isCancelled() {
	    return cancelled;
	}

	@Override
	public boolean isDone() {
	    return countDownLatch.getCount() == 0;
	}

	public void exec(final ActionResponse result) {
	    this.result = result;
	    countDownLatch.countDown();
	}
    }
}
