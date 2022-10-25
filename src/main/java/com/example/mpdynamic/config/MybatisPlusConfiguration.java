package com.example.mpdynamic.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"com.example.mpdynamic.repository"})
public class MybatisPlusConfiguration {
}
