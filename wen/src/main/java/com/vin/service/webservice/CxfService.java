package com.vin.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * webservice 接口
 * 
 * @ClassName:   CxfService
 * @Instructions:
 * @author: Vincent
 * @date:   2018年8月28日 上午9:58:06
 *
 */
@WebService
public interface CxfService {
	@WebMethod
	public String Hello(@WebParam(name = "str")String str);
}
