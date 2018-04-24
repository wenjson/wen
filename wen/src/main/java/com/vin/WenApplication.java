package com.vin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 应用启动类
 * @author Vincent
 *
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class WenApplication {

	public static void main(String[] args) {
		SpringApplication.run(WenApplication.class, args);
	}
}
