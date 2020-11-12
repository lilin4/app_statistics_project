package com.atguigu.collet.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.app.common.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.atguigu.app.util.GeoUtil;
import com.atguigu.app.util.PropertiesUtil;
import org.apache.hive.service.auth.HiveAuthFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Ints;

@RestController
@RequestMapping("coll")
@Slf4j
public class CollectLogController {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(CollectLogController.class);

    
    Map<String,GeoInfo> cache = new HashMap();
    
    

    @PostMapping("index")
    private Object list(@RequestBody AppLogEntity e, HttpServletRequest request) {
        // 1 获取服务器时间
        long myTime = System.currentTimeMillis();
        // 2 获取客户端时间
        long clientTime = Long.parseLong(request.getHeader("clientTime"));
        // 3 计算服务器和客户端时间差
        long diff = clientTime - myTime;
        //1.修正时间
        verifyTime(e, diff);
        //2.基本属性复制
        copyBaseProperties(e);
        //3.处理IP
        //String ip = request.getRemoteAddr();
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        processIp(ip,e);
        //4. 发送消息的kafka
        sendMessage(e);
        return e;
    }

    /**
     * 消息发送
     */
    private void sendMessage(AppLogEntity e) {
        sendSingleLog(Constants.TOPIC_APP_STARTUP,e.getAppStartupLogs());
        sendSingleLog(Constants.TOPIC_APP_ERRROR,e.getAppErrorLogs());
        sendSingleLog(Constants.TOPIC_APP_EVENT,e.getAppEventLogs());
        sendSingleLog(Constants.TOPIC_APP_PAGE,e.getAppPageLogs());
        sendSingleLog(Constants.TOPIC_APP_USAGE,e.getAppUsageLogs());
    }

    /**
     * 发送单个的log消息给kafka
     */
    private void sendSingleLog( String topic, AppBaseLog[] logs) {
        for (AppBaseLog log : logs) {
            String logMsg = JSONObject.toJSONString(log);
            //创建消息
            kafkaTemplate.send(topic,logMsg).addCallback(success->{
                // 消息发送到的topic
                String topicFalg = success.getRecordMetadata().topic();
                // 消息发送到的分区
                int partition = success.getRecordMetadata().partition();
                // 消息在分区内的offset
                long offset = success.getRecordMetadata().offset();
                logger.info("发送消息成功:{}", topicFalg , "-{}" , partition + "-{}" , offset);
            }, failure -> {
                logger.info("发送消息失败:{}" , failure.getMessage());
            });
        }
    }

    private void processIp(String clientIp, AppLogEntity e) {
        GeoInfo info = cache.get(clientIp);
        if (null == info){
            info = new GeoInfo();
            info.setCountry(GeoUtil.getCountry(clientIp));
            info.setProvince( GeoUtil.getProvince(clientIp));
            cache.put(clientIp,info);

        }
        for (AppStartupLog log : e.getAppStartupLogs()) {
            log.setCountry(info.getCountry());
            log.setProvince(info.getProvince());
            log.setIpAddress(clientIp);
        }
    }

    /*private void processLogs(AppLogEntity e){
        for(AppStartupLog log : e.getAppStartupLogs()){
            PropertiesUtil.copyProperties(e,log);
        }
        for(AppErrorLog log : e.getAppErrorLogs()){
            PropertiesUtil.copyProperties(e,log);
        }
        for(AppEventLog log : e.getAppEventLogs()){
            PropertiesUtil.copyProperties(e,log);
        }
        for(AppPageLog log : e.getAppPageLogs()){
            PropertiesUtil.copyProperties(e,log);
        }
        for(AppUsageLog log : e.getAppUsageLogs()){
            PropertiesUtil.copyProperties(e,log);
        }
    }*/

    /**
     * 复制基本属性
     */
    private void copyBaseProperties(AppLogEntity e) {
        PropertiesUtil.copyProperties(e, e.getAppStartupLogs());
        PropertiesUtil.copyProperties(e, e.getAppErrorLogs());
        PropertiesUtil.copyProperties(e, e.getAppEventLogs());
        PropertiesUtil.copyProperties(e, e.getAppPageLogs());
        PropertiesUtil.copyProperties(e, e.getAppUsageLogs());
    }

    /**
     * 修正时间
     */
    private void verifyTime(AppLogEntity e, long diff) {
        //startuplog
        for (AppBaseLog log : e.getAppStartupLogs()) {
            log.setCreatedAtMs(log.getCreatedAtMs() + diff);
        }
        for (AppBaseLog log : e.getAppUsageLogs()) {
            log.setCreatedAtMs(log.getCreatedAtMs() + diff);
        }
        for (AppBaseLog log : e.getAppPageLogs()) {
            log.setCreatedAtMs(log.getCreatedAtMs() + diff);
        }
        for (AppBaseLog log : e.getAppEventLogs()) {
            log.setCreatedAtMs(log.getCreatedAtMs() + diff);
        }
        for (AppBaseLog log : e.getAppErrorLogs()) {
            log.setCreatedAtMs(log.getCreatedAtMs() + diff);
        }
    }
}
