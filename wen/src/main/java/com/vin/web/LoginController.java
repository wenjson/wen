package com.vin.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.core.redis.RedisOpt;
import com.vin.core.redis.RedisUtils;
import com.vin.entity.BaseUser;
import com.vin.service.BaseService;

@RestController
public class LoginController {
	
	@Autowired
	private BaseService baseService;
	
	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private RedisOpt redisOpt;
	
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
	
	@RequestMapping("/getUserName")
	public String getUserName(){
		List<BaseUser> list = baseService.getUserInfoByConditon(null);
		String userName = "";
		if(!list.isEmpty()){
			userName = list.get(0).getUserName();
		}
		return "用户名："+userName;
	}
	
	/**
	 * docker 测试
	 * @return
	 */
	@RequestMapping("/docker")
	public String docker(){
		return "hello docker";
	}
	
	/**
	 * redis 测试
	 * @return
	 */
	@RequestMapping("/redis")
	public String redis(String key){
		//redisUtils.set("lalala", "haha");
		
		String pwd = redisUtils.get(key).toString();
		return pwd;
	}
	
	/**
	 * redis 测试
	 * @return
	 */
	@RequestMapping("/redis2")
	public String redis2(String key){
		//redisUtils.set("lalala", "haha");
		
		String pwd = redisOpt.getValue(key);
		return pwd;
	}
	
	/**
	 * redis 测试
	 * @return
	 */
	@RequestMapping("/setValue")
	public String setValue(String key,String value){
		redisOpt.setKey(key, value);
		
		return "success";
	}
}
