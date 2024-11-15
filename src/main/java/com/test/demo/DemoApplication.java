package com.test.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

//  서버가 실행되면 JRE가 이 클래스의 메서드를 찾아서 자동 실행이 된다.
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
