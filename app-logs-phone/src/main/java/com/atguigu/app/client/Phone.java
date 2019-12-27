package com.atguigu.app.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 模拟手机上报日志程序
 */
public class Phone {
    public static void main(String[] args) throws IOException {
        //输入流
        InputStream in = ClassLoader.getSystemResourceAsStream("log.json");

        URL url = new URL("http://localhost:8080/coll/index");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        //请求方式为post
        connection.setRequestMethod("POST");
        //可以携带数据
        connection.setDoOutput(true);
        //甚至请求头内容,设置内容类型
        connection.setRequestProperty("Content-Type","application/json");
        //将安装时间写入header,server端进行时钟校对
        connection.setRequestProperty("clientTime",System.currentTimeMillis()+"");

        //输出流
        OutputStream out = connection.getOutputStream();
        byte[] bytes = new byte[1024];
        int len = -1;
        while ((len = in.read(bytes)) != -1){
            out.write(bytes,0,len);
        }
        out.close();
        out.flush();
        in.close();
        System.out.println(connection.getResponseCode());
    }
}
