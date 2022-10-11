package com.project;

import com.project.bilibili.service.webSocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class BiliBiliApp {
    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(BiliBiliApp.class,args);
        WebSocketService.setApplicationContext(app);
    }
}
