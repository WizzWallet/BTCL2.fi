package com.wizz.fi;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.wizz"})
@EnableScheduling
@EnableAsync
@MapperScan("com.wizz.fi.dao.mapper")
@ConfigurationPropertiesScan
@Slf4j
@EnableSchedulerLock(defaultLockAtMostFor = "PT70S")
public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        log.info("-------------- 启动完成 --------------");
    }
}
