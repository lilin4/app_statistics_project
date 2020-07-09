package com.jd.es.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class ElasticSearchClient {

    @Autowired
    @Qualifier("esConfig")
    private TransportClient transportConfig;

    private String hostName = "192.168.199.129";

    private Integer transport = 9300;

    private String clusterName = "my-application";

    TransportClient transportClient = null;


    private TransportClient init() throws UnknownHostException {
        //创建settings对象，配置集群名称
        Settings build = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        //创建transportAddress对象，配置连接信息
        transportClient = new PreBuiltTransportClient(build);
        InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(hostName), transport);
        transportClient.addTransportAddress(transportAddress);
        return transportClient;
    }

    /**
     * 创建索引
     */
    @Test
    public void createIndex() throws UnknownHostException {
        //创建settings对象，配置集群名称
        Settings build = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        //创建transportAddress对象，配置连接信息
        TransportClient transportClient = new PreBuiltTransportClient(build);
        InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(hostName), transport);
        transportClient.addTransportAddress(transportAddress);
        //创建索引
        transportClient.admin().indices().prepareCreate("index_hello")
                //执行操作
                .get();
        //关闭索引
        transportClient.close();
    }

    /**
     * 创建mappings
     */
    @Test
    public void saveMappings() throws Exception {
        //创建settings对象，配置集群名称
        Settings build = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        //创建transportAddress对象，配置连接信息

        TransportClient transportClient = new PreBuiltTransportClient(build)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostName), transport));

        // 拼接json串
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("article")
                        .startObject("properties")
                            .startObject("id")
                                .field("type", "long")
                                .field("store", true)
                            .endObject()
                            .startObject("title")
                                .field("type", "text")
                                .field("store", true)
                                .field("analyzer", "ik_smart")
                            .endObject()
                            .startObject("content")
                                .field("type", "text")
                                .field("store", true)
                                .field("analyzer", "ik_smart")
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();

        //设置索引
        transportClient.admin().indices().preparePutMapping("index_hello")
                //设置要做的映射类型
                .setType("article")
                //设置映射信息 可以是XContentBuilder也可以是map类型
                .setSource(builder)
                //执行操作
                .get();
        //关闭索引
        transportClient.close();
    }

    @Test
    public void testAddCocument()throws Exception {

        //创建settings对象，配置集群名称
        Settings build = Settings.builder()
                .put("cluster.name", clusterName)
                .build();
        //创建transportAddress对象，配置连接信息
        transportClient = new PreBuiltTransportClient(build);
        InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(hostName), transport);
        transportClient.addTransportAddress(transportAddress);

        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("id", 1l)
                .field("title", "测试一下")
                .field("content", "测试第二下")
                .endObject();

        transportClient.prepareIndex()
                //设置索引
                .setIndex("index_hello")
                // 设置映射类型
                .setType("article")
                //设置ID
                .setId("1")
                //设置文档信息
                .setSource(builder)
                .get();

        transportClient.close();
    }
    @Test
    public void testAddCocument2()throws Exception {
        TransportClient transportClient = null;
        for (int i=4;i<100;i++) {
            transportClient = this.init();
            Article article = new Article();
            article.setId(i);
            article.setTitle("测试"+i+"下");
            article.setContent("测试第"+i+"下");

            String jsonDocument = new ObjectMapper().writeValueAsString(article);

            transportClient.prepareIndex()
                    //设置索引
                    .setIndex("index_hello")
                    // 设置映射类型
                    .setType("article")
                    //设置ID
                    .setId(""+i)
                    //设置文档信息
                    .setSource(jsonDocument, XContentType.JSON)
                    .get();
        }

        transportClient.close();
    }

    void baseBuilder(QueryBuilder queryBuilder) throws UnknownHostException {

    }

    @Test
    void serachQueryBudir() throws UnknownHostException {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<emg>");
        highlightBuilder.postTags("</emg>");
        QueryBuilder queryBuilder =null;
        //queryBuilder = QueryBuilders.idsQuery().addIds("1", "2");
        queryBuilder = QueryBuilders.termQuery("title", "测试");
        //queryBuilder = QueryBuilders.queryStringQuery("测试").defaultField("title");
        /*BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        queryBuilder = QueryBuilders.queryStringQuery("测一").defaultField("title");
        boolQueryBuilder.must(queryBuilder);*/
        TransportClient client= this.init();
        SearchResponse searchResponse = client.prepareSearch("index_hello")
                .setTypes("article")
                .setQuery(queryBuilder)
                .setFrom(0)
                .setSize(3)
                .highlighter(highlightBuilder)
                .get();
        //取查询结果
        SearchHits searchHits = searchResponse.getHits();
        //条数
        System.out.println(searchHits.getTotalHits());
        //结果列表
        searchHits.iterator().forEachRemaining(hits ->{
            //打印文档格式
            System.out.println(hits.getSourceAsString());
            //取属性
            /*Map<String, Object> source = hits.getSource();
            System.out.println(source.toString());*/
            Map<String, HighlightField> highlightFields = hits.getHighlightFields();
            System.out.println(highlightFields);
        } );
        transportClient.close();
    }
}
