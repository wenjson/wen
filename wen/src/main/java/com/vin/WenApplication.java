package com.vin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 应用启动类
 * @author Vincent
 * @date 2018-04-24
 * @Description: 
 * ServletComponentScan 启动扫描Servlet，druid 监控需要
 * Swagger2：http://localhost:8098/swagger-ui.html
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableSwagger2	//开启Swagger2 API注解
@ServletComponentScan
public class WenApplication {

	public static void main(String[] args) {
		SpringApplication.run(WenApplication.class, args);
	}
}
