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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nrw.hbz.regal.fedora.CopyUtils;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
class Search {
    final static Logger logger = LoggerFactory.getLogger(Search.class);

    @SuppressWarnings("serial")
    class InvalidRangeException extends RuntimeException {
	// It is just there to be thrown
    }

    @SuppressWarnings("serial")
    class SearchException extends RuntimeException {
	public SearchException(Throwable e) {
	    super(e);
	}
    }

    Client client = null;

    Search(Client client) {
	this.client = client;
    }

    void init(String index, String config) {
	try {
	    String indexConfig = CopyUtils.copyToString(Thread.currentThread()
		    .getContextClassLoader().getResourceAsStream(config),
		    "utf-8");
	    client.admin().indices().prepareCreate(index)
		    .setSource(indexConfig).execute().actionGet();

	} catch (org.elasticsearch.indices.IndexAlreadyExistsException e) {
	    logger.debug("", e);
	} catch (Exception e) {
	    throw new SearchException(e);
	}
    }

    ActionResponse index(String index, String type, String id, String data) {

	return client.prepareIndex(index, type, id).setSource(data).execute()
		.actionGet();

    }

    SearchHits listResources(String index, String type, int from, int until) {
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

    List<String> listIds(String index, String type, int from, int until) {
	SearchHits hits = listResources(index, type, from, until);
	Iterator<SearchHit> it = hits.iterator();
	List<String> list = new Vector<String>();
	while (it.hasNext()) {
	    SearchHit hit = it.next();
	    list.add(hit.getId());
	}
	return list;
    }

    ActionResponse delete(String index, String type, String id) {
	return client.prepareDelete(index, type, id)
		.setOperationThreaded(false).execute().actionGet();
    }

    SearchHits query(String index, String fieldName, String fieldValue) {
	client.admin().indices().refresh(new RefreshRequest()).actionGet();
	QueryBuilder query = QueryBuilders.boolQuery().must(
		QueryBuilders.matchQuery(fieldName, fieldValue));
	SearchResponse response = client.prepareSearch(index).setQuery(query)
		.execute().actionGet();
	return response.getHits();
    }

    String getSettings(String index, String type) {
	try {

	    client.admin().indices().refresh(new RefreshRequest()).actionGet();
	    ClusterState clusterState = client.admin().cluster().prepareState()
		    .setIndices(index).execute().actionGet().getState();
	    IndexMetaData inMetaData = clusterState.getMetaData().index(index);
	    MappingMetaData metad = inMetaData.mapping(type);
	    return metad.getSourceAsMap().toString();
	} catch (IOException e) {
	    throw new SearchException(e);
	}

    }

}
