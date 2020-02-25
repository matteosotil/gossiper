package com.bloobirds.training.gossiper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GossiperApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(GossiperApp.class, args);
    }

}
