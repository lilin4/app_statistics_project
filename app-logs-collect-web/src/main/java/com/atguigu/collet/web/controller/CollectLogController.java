package com.atguigu.collet.web.controller;

import com.atguigu.app.common.AppBaseLog;
import com.atguigu.app.common.AppLogEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

@RestController
@RequestMapping("coll")
@Slf4j
public class CollectLogController {

    @PostMapping("index")
    private Object list(@RequestBody AppLogEntity appLogEntity, HttpServletRequest request) {
        // 1 获取服务器时间
        long myTime = System.currentTimeMillis();
        // 2 获取客户端时间
        long clientTime = Long.parseLong(request.getHeader("clientTime"));
        // 3 计算服务器和客户端时间差
        long diff = clientTime - myTime;
        return diff;
    }
}
