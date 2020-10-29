package com.atguigu.collet.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.app.common.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.GeoUtil;
import util.PropertiesUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("coll")
@Slf4j
public class CollectLogController {
    
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
        String clientIp = request.getRemoteAddr();
        processIp(clientIp,e);
        //4. 
        return e;
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
