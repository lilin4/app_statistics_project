package com.atguigu.collet.web.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname KafkaInitialConfiguration
 * @Description kafka初始化topic
 * @Date 2020/11/3 10:02
 * @Created by 林立
 */

//@Configuration
public class KafkaInitialConfiguration {

    // 创建一个名为testtopic的Topic并设置分区数为1，分区副本数为3
    //@Bean
    public NewTopic initialTopic() {
        return new NewTopic("testtopic", 1, (short) 3);
    }
    

    // 如果要修改分区数，只需修改配置值重启项目即可
    // 修改分区数并不会导致数据的丢失，但是分区数只能增大不能减小
   //@Bean
    public NewTopic updateTopic() {
        return new NewTopic("testtopic", 1, (short) 3);
    }

}
