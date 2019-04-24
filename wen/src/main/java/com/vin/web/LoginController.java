package com.vin.web;

import java.util.List;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.core.redis.RedisOpt;
import com.vin.core.redis.RedisUtils;
import com.vin.entity.BaseUser;
import com.vin.service.BaseService;
import com.vin.service.webservice.CxfService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(value = "swagger2测试")
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
	
	@RequestMapping("/hello")
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
	
	/**
	 * webservice 测试
	 * 
	 * @return
	 */
	@ApiOperation(value="webservice测试", notes="传值输出")
	@ApiImplicitParam(name = "vv", value = "参数，字符串", required = true, dataType = "String",paramType = "query")
	@GetMapping("/wsHello")
	public String wsHello(String vv){
		try {

            // 接口地址
            String address = "http://localhost:8098/webservice/ws?wsdl";
            
			////********* 方式一 *******************
            // 代理工厂
            JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
            // 设置代理地址
            jaxWsProxyFactoryBean.setAddress(address);
            // 设置接口类型
            jaxWsProxyFactoryBean.setServiceClass(CxfService.class);
            // 创建一个代理接口实现
            CxfService cs = (CxfService) jaxWsProxyFactoryBean.create();
            // 调用代理接口的方法调用并返回结果
            String result = cs.Hello(vv);
            System.out.println("返回结果:" + result);
            
            
			////********* 方式二 *******************
            JaxWsDynamicClientFactory dcf =JaxWsDynamicClientFactory.newInstance();
	        org.apache.cxf.endpoint.Client client =dcf.createClient(address);
	        String param = vv;
	        Object[] objects=client.invoke("Hello",param);
	        //输出调用结果
	        System.out.println("=============================="+objects[0].toString());
            
    		return result+";"+objects[0].toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
	}
}
