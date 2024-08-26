package com.emna.micro_service3.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
/*@EnableElasticsearchRepositories(basePackages = "com.emna.micro_service3.repository")*/
public class ESClient {

    @Bean
    public ElasticsearchClient getElasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "https"));

        RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback = new HttpClientConfigImpl();
        builder.setHttpClientConfigCallback(httpClientConfigCallback);

        RestClient restClient = builder.build();

        RestClientTransport restClientTransport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(restClientTransport);
    }
}
