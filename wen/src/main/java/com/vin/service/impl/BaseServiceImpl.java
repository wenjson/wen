package com.vin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vin.dao.BaseMapper;
import com.vin.entity.BaseUser;
import com.vin.service.BaseService;

/**
 * 基础信息实现类
 * @author Vincent
 *
 */
@Service
public class BaseServiceImpl implements BaseService{
	
	@Autowired
	private BaseMapper baseMapper;

	/**
	 * 根据条件获取用户信息
	 */
	@Override
	public List<BaseUser> getUserInfoByConditon(Map<String, Object> map) {
		return baseMapper.getUserInfoByCondition(map);
	}

}
