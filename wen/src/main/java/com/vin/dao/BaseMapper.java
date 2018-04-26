package com.vin.dao;

import java.util.List;
import java.util.Map;

import com.vin.entity.BaseUser;

/**
 * 基本信息mapper
 * @author Vincent
 * @date 2018-04-26
 *
 */
public interface BaseMapper {
	
	/**
	 * 根据条件获取用户信息
	 * @param map
	 * @return
	 */
	public List<BaseUser> getUserInfoByCondition(Map<String,Object> map);
}
