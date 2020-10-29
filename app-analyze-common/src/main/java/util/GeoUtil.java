package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.maxmind.db.Reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

/**
 * @Classname GeoUtil
 * @Description TODO
 * @Date 2020/10/29 15:35
 * @Created by 林立
 */
public class GeoUtil {
    private static InputStream in;
    private static Reader reader;
    static {
        in = ClassLoader.getSystemResourceAsStream("GeoLite2-City.mmdb");
        try{
            reader = new Reader(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description 获得国家数据
     * @Date 2020/10/29 15:52
     * @Created by 林立 
     * @Param ip: 
     * @return: java.lang.String
     */
    public static String getCountry(String ip){
        try {
            return reader.get(InetAddress.getByName(ip)).get("country").get("names").get("zh-CN").textValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }  
    public static String getProvince(String ip){
        try {
            return reader.get(InetAddress.getByName(ip)).get("subdivisions").get(0).get("names").get("zh-CN").textValue();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
