package com.fastfish;

import java.net.InetSocketAddress;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.shield.ShieldPlugin;


public class ESRemoteTest {

    private Client client;


    public void testConnect() {

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
            GetResponse response = client.prepareGet("fastfish", "logs","AVe3ng8KbVtTkaw8pXIm").get();
            System.out.println("done get");
            client.close();
        } catch ( Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        System.err.println("done");

    }



    public static void main(String[] args) {

        ESRemoteTest conn = new ESRemoteTest();
        conn.testConnect();

    }

}
