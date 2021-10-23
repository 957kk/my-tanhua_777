package com.tanhua.manager.user.management;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.tanhua.common.mapper") //设置mapper接口的扫描包
@MapperScan("com.tanhua.manager.user.management.mapper")
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class}) //排除mongo的自动配置
@ComponentScan(basePackages = "com.tanhua") //修改默认的扫描包范围
public class UserManagerApplicationMXY {

    public static void main(String[] args) {
        SpringApplication.run(UserManagerApplicationMXY.class, args);
    }
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
