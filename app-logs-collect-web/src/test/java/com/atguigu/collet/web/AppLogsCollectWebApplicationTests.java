package com.atguigu.collet.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.net.InetAddress;

@SpringBootTest
class AppLogsCollectWebApplicationTests {

    @Test
    void contextLoads() {
        
    }

    public static void main(String[] args) {
        try (InputStream in = ClassLoader.getSystemResourceAsStream("GeoLite2-City.mmdb")){
            assert in !=null;
            Reader reader = new Reader(in);
            JsonNode jsonNode = reader.get(InetAddress.getByName("61.135.169.121"));
            String name = jsonNode.get("").get("name").get("zh-CN").textValue();
            System.out.println(name);
        }catch (Exception e){
            e.getMessage();
        }
    }

}
