package com.vin.core.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

/**
 * StringRedisTemplate 方式操作readis
 * 无法获取RedisTemplate设置的值
 * @author Vincent
 *
 */
@Component
public class RedisOpt {
	
	@Autowired
	private StringRedisTemplate template;
	
	/**
	 * 设置key值
	 * @param key
	 * @param value
	 */
	public void setKey(String key,String value){
        ValueOperations<String, String> ops = template.opsForValue();
        ops.set(key,value,1, TimeUnit.MINUTES);//1分钟过期
    }
	
	/**
	 * 获取key值
	 * @param key
	 * @return
	 */
    public String getValue(String key){
        ValueOperations<String, String> ops = template.opsForValue();
        
        String value = ops.get(key);
        
        return value==null?"":value;
    }
}
