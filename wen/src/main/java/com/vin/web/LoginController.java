package com.vin.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
	
	@Value("${file_base_path}")
	private String filePath;
	
	@RequestMapping("/")
	public String index(){
		return "Hello World!";
	}
	
	@RequestMapping("/show")
	public String show(){
		return filePath;
	}
}
