package com.hanzec.yats.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.time.ZonedDateTime;

@Configuration
@EnableCaching
public class SpringBootConfig {


    @Bean
    public Gson getGsonInstance() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    /**
     * Gson's configuration in order to use gson as serializer
     */
    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter(Gson gson) {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(gson);
        return converter;
    }
}