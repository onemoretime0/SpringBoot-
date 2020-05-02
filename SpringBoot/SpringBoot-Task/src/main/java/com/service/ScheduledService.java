package com.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {

    //特点特定的事件执行，cron表达式
    @Scheduled(cron = "0/2  * * * * ?")  //传入一个cron表达式,每两秒执行一次
    public void hello(){
        System.out.println("Hello,执行了");
    }
}
