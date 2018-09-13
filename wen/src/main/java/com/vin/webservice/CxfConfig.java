package com.vin.webservice;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vin.service.webservice.CxfService;

/**
 * cxf webservice 配置发布信息
 * 
 * 访问路径：http://localhost:8098/webservice/ws?wsdl
 * 发布服务名称,application 配置中 cxf.path=/yourpath 配置
 * @ClassName:   CxfConfig
 * @Instructions:
 * @author: Vincent
 * @date:   2018年8月28日 上午9:52:46
 *
 */
@Configuration
public class CxfConfig {

    @Autowired
    private Bus bus;
    @Autowired
    private CxfService webService;
 
    /**
     * 配置发布服务名称，默认为 services
     * 【注意】 会影响路径
     * 可在application配置文件中配置 cxf.path=/yourpath
     * @return
     */
    /*@Bean
    public ServletRegistrationBean<CXFServlet> dispatcherServlet(){
        return new ServletRegistrationBean<CXFServlet>(new CXFServlet(),"/webs/*");//发布服务名称
    }*/
    
    /**
     * 配置发布的名称
     * @return
     */
    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(bus, webService); //绑定要发布的服务
        endpoint.publish("/ws"); //显示要发布的名称
        return endpoint;
    }
}