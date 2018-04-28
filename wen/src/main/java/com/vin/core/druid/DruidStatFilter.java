package com.vin.core.druid;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.alibaba.druid.support.http.WebStatFilter;

/**
 * @Description: 拦截druid监控请求
 * @date: 2018-04-28
 */
@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",  
initParams={  
         @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源  
 }  
)  
public class DruidStatFilter extends WebStatFilter{  
  
}  