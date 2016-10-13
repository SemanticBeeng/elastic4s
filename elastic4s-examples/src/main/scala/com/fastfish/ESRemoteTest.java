package com.fastfish;

import java.net.InetSocketAddress;
import java.util.Map;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.shield.ShieldPlugin;


public class ESRemoteTest {

    public void testSearch() {

        try {
            String clusterId = "34cccf87611bad4eedae9127b94759ab";
            TransportClient client = TransportClient.builder()
                    .addPlugin(ShieldPlugin.class)
                    .settings(Settings.builder()
                            .put("cluster.name", clusterId)
                            .put("client.transport.sniff", false)
                            .put("shield.user", "admin:9v4o0cc23a6dcegeed")
                            //		    	.put("transport.ping_schedule", "5s")
                            .put("action.bulk.compress", false)
                            .put("shield.transport.ssl", true)
                            .put("request.headers.X-Found-Cluster", clusterId)
                            .build()).build().
                            addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(clusterId+".eu-west-1.aws.found.io", 9343)));
            //GetResponse response = client.prepareGet("fastfish", "logs","AVe3ng8KbVtTkaw8pXIm").get();
            SearchResponse response = client.prepareSearch("fastfish")
                    // .setTypes("type1", "type2")
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(QueryBuilders.multiMatchQuery("water", "brand_name","category_id","long_description"))
                    .setFrom(0).setSize(60).setExplain(true)
                    .execute()
                    .actionGet();

            System.out.println("done search" + response.getHits());
            for (org.elasticsearch.search.SearchHit hit : response.getHits().hits()) {
                final Map<String, Object> fields = hit.getSource();

                System.out.println("Fields =" + fields);
            }
            client.close();
        } catch ( Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        System.err.println("done");

    }



    public static void main(String[] args) {

        ESRemoteTest search = new ESRemoteTest();
        search.testSearch();

    }

}
