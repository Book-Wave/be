package com.test.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
//  서버가 실행되면 JRE가 이 클래스의 메서드를 찾아서 자동 실행이 된다.
    public static void main(String[] args) {
        logger.info("Application started");
        logger.debug("This is a debug message");
        logger.error("This is an error message");
        SpringApplication.run(DemoApplication.class, args);
    }

}
