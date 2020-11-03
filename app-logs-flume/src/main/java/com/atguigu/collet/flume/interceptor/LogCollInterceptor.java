package com.atguigu.collet.flume.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.app.common.AppBaseLog;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.atguigu.collet.flume.interceptor.LogCollInterceptor.Constants.*;


/**
 * @Classname LogCollInterceptor
 * @Description TODO
 * @Date 2020/11/3 15:23
 * @Created by 林立
 */
public class LogCollInterceptor implements Interceptor {

    private final boolean preserveExisting;

    public LogCollInterceptor(boolean preserveExisting) {
        this.preserveExisting = preserveExisting;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event) {
        Map<String, String> headers = event.getHeaders();
        //处理时间
        String jsonStr = new String(event.getBody());
        AppBaseLog log = JSONObject.parseObject(jsonStr, AppBaseLog.class);
        long time = log.getCreatedAtMs();
        headers.put(TIMESTAMP, Long.toString(time));
        
        //处理log类型的头
        //pageLog
        String logType = "" ;
        if(jsonStr.contains("pageId")){
            logType = "page" ;
        }
        //eventLog
        else if (jsonStr.contains("eventId")) {
            logType = "event";
        }
        //usageLog
        else if (jsonStr.contains("singleUseDurationSecs")) {
            logType = "usage";
        }
        //error
        else if (jsonStr.contains("errorBrief")) {
            logType = "error";
        }
        //startup
        else if (jsonStr.contains("network")) {
            logType = "startup";
        }
        headers.put("logType", logType);
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> list) {
        list.forEach(event -> {
            intercept(event);
        });
        return list;
    }

    @Override
    public void close() {

    }

    /**
     *
     */
    public static class Builder implements Interceptor.Builder {

        private boolean preserveExisting = PRESERVE_DFLT;

        @Override
        public Interceptor build() {
            return new LogCollInterceptor(preserveExisting);
        }

        @Override
        public void configure(Context context) {
            preserveExisting = context.getBoolean(PRESERVE, PRESERVE_DFLT);
        }

    }

    /**
     * 保存
     */
    private void save(String log) {
        try {
            FileWriter fw = new FileWriter("/home/centos/l.log", true);
            fw.append(log + "\r\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Constants {
        public static String TIMESTAMP = "timestamp";
        public static String PRESERVE = "preserveExisting";
        public static boolean PRESERVE_DFLT = false;
    }
}
