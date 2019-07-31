package com.fonkwill.fogstorage.client.configuration;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.*;

@Configuration
@EnableFeignClients()
public class FeignConfiguration {

    //@Bean
    //@Primary
    //@Scope("prototype")
    //public Encoder feignFormEncoder() {
     //   return new SpringFormEncoder();
    //}

}
