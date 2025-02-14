package com.hanzec.yats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class YahsApplication {

    public static void main(String[] args) {
        SpringApplication.run(YahsApplication.class, args);
    }

}
