package com.vin.service.webservice.impl;

import javax.jws.WebService;
import org.springframework.stereotype.Component;

import com.vin.service.webservice.CxfService;

/**
 * webservice 接口实现类
 * 
 * @ClassName:   CxfServiceImpl
 * @Instructions:
 * @author: Vincent
 * @date:   2018年8月28日 下午2:32:12
 *
 */
@Component
@WebService(targetNamespace="http://webservice.service.vin.com/",endpointInterface = "com.vin.service.webservice.CxfService")
public class CxfServiceImpl implements CxfService {
	
	@Override
	public String Hello(String str) {
		return "Hello "+str+"!";
	}
}
