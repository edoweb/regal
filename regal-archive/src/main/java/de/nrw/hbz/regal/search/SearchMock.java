package de.nrw.hbz.regal.search;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

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
import java.util.List;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHits;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
class SearchMock {

    Search search;
    Node node = null;
    Client client = null;

    /**
     * Used for testing
     */
    public SearchMock(String indexName, String configName) {
	node = nodeBuilder().local(true).node();
	client = node.client();
	client.admin().indices().prepareDelete("_all").execute().actionGet();
	search = new Search(client);
	search.init(indexName, configName);
    }

    /**
     * Used for testing. Clean up!
     */
    public void down() {
	client.admin().indices().prepareDelete("_all").execute().actionGet();
	node.close();
    }

    public int hashCode() {
	return search.hashCode();
    }

    public void init(String index, String config) {
	search.init(index, config);
    }

    public ActionResponse index(String index, String type, String id,
	    String data) {
	return search.index(index, type, id, data);
    }

    public SearchHits listResources(String index, String type, int from,
	    int until) {
	return search.listResources(index, type, from, until);
    }

    public boolean equals(Object obj) {
	return search.equals(obj);
    }

    public List<String> listIds(String index, String type, int from, int until) {
	return search.listIds(index, type, from, until);
    }

    public ActionResponse delete(String index, String type, String id) {
	return search.delete(index, type, id);
    }

    public SearchHits query(String index, String fieldName, String fieldValue) {
	return search.query(index, fieldName, fieldValue);
    }

    public String getSettings(String index, String type) {
	return search.getSettings(index, type);
    }

    public String toString() {
	return search.toString();
    }

}
