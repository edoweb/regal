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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import de.nrw.hbz.regal.fedora.CopyUtils;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class Search {

    @SuppressWarnings("serial")
    class InvalidRangeException extends RuntimeException {
    }

    @SuppressWarnings("serial")
    class SearchException extends RuntimeException {
	public SearchException(Throwable e) {
	    super(e);
	}
    }

    Client client = null;
    Node node = null;

    /**
     * Used for testing
     */
    public Search() {

	node = nodeBuilder().local(true).node();
	client = node.client();

	client.admin().indices().prepareDelete().execute().actionGet();
	init("test");
    }

    /**
     * Used for testing. Clean up!
     */
    public void down() {
	client.admin().indices().prepareDelete().execute().actionGet();
	node.close();
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
	init("edoweb");
    }

    private void init(String index) {
	try {
	    String indexConfig = CopyUtils.copyToString(
		    Thread.currentThread().getContextClassLoader()
			    .getResourceAsStream("index-config.json"), "utf-8");
	    client.admin().indices().prepareCreate(index)
		    .setSource(indexConfig).execute().actionGet();

	} catch (IOException e) {
	    throw new SearchException(e);
	}
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
    public ActionResponse index(String index, String type, String id,
	    String data) {

	return client.prepareIndex(index, type, id).setSource(data).execute()
		.actionGet();

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
	client.admin().indices().refresh(new RefreshRequest()).actionGet();
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
    public ActionResponse delete(String index, String type, String id) {
	return client.prepareDelete(index, type, id)
		.setOperationThreaded(false).execute().actionGet();
    }

    public SearchHits query(String index, String fieldName, String fieldValue) {

	client.admin().indices().refresh(new RefreshRequest()).actionGet();
	SearchResponse response = client.prepareSearch(index)
		.setQuery(QueryBuilders.fieldQuery(fieldName, fieldValue))
		.execute().actionGet();
	return response.getHits();
    }

}
