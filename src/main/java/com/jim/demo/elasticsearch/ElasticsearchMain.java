package com.jim.demo.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

public class ElasticsearchMain {

	public static void main(String args[]) throws IOException {
		RestClient restClient = RestClient.builder(new HttpHost("10.18.19.48", 9200)).build();
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		ElasticsearchClient client = new ElasticsearchClient(transport);

		SearchResponse<Content> search = client.search(s -> s
						.index("test0107")
						.query(q -> q
								.term(t -> t
										.field("content")
										.value(v -> v.stringValue("北京"))
								)),
				Content.class);

		for (Hit<Content> hit: search.hits().hits()) {
			System.out.println(hit.toString());
		}
	}
}
