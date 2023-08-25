package com.space;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.space.mapper")
@EnableRabbit
public class SpaceCodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpaceCodeApplication.class, args);
    }
}
