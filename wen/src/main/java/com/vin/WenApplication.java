package com.vin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 应用启动类
 * @author Vincent
 * @date 2018-04-24
 * @Description: 
 * ServletComponentScan 启动扫描Servlet，druid 监控需要
 * 
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ServletComponentScan
public class WenApplication {

	public static void main(String[] args) {
		SpringApplication.run(WenApplication.class, args);
	}
}
