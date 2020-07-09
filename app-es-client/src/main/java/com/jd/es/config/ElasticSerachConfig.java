package com.jd.es.config;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class ElasticSerachConfig {

    private String hostName="192.168.199.129";

    private Integer transport=9300;

    private String clusterName="my-application";

    @Bean(name = "esConfig")
    public TransportClient transportConfig() throws Exception {
        //创建settings对象，配置集群名称
        Settings build = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        TransportClient transportClient = new PreBuiltTransportClient(build);
        TransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(hostName),transport);
        transportClient.addTransportAddress(transportAddress);
        return transportClient;
    }
}
