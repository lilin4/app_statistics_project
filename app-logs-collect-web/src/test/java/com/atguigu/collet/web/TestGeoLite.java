package com.atguigu.collet.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;
import org.junit.Test;

import java.io.InputStream;
import java.net.InetAddress;

/**
 * @Classname GeoLite
 * @Description TODO
 * @Date 2020/10/29 15:55
 * @Created by 林立
 */
public class TestGeoLite {

    @Test
    public void test1() {
        try (InputStream in = ClassLoader.getSystemResourceAsStream("GeoLite2-City.mmdb")){
            assert in !=null;
            Reader reader = new Reader(in);
            JsonNode jsonNode = reader.get(InetAddress.getByName("61.135.169.121"));
            //洲
            String continent = jsonNode.get("continent").get("names").get("zh-CN").textValue();
            //国家
            String country = jsonNode.get("country").get("names").get("zh-CN").textValue();
            //省
            String are = jsonNode.get("subdivisions").get(0).get("names").get("zh-CN").textValue();
            //市
            String city = jsonNode.get("city").get("names").get("zh-CN").textValue();

            System.out.println(continent + " "+country + " "+are + " "+city);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
