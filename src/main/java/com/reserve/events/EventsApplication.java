package com.reserve.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
public class EventsApplication {


    public static void main(String[] args) {
        SpringApplication.run(EventsApplication.class, args);
    }

}
