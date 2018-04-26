package com.vin.service;

import java.util.List;
import java.util.Map;

import com.vin.entity.BaseUser;

/**
 * 基础信息服务类
 * @author Vincent
 *
 */
public interface BaseService {

	/**
	 * 根据条件获取用户表信息
	 * @param map
	 * @return
	 */
	public List<BaseUser> getUserInfoByConditon(Map<String,Object> map);
}
