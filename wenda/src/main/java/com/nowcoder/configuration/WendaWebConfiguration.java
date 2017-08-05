package com.nowcoder.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.nowcoder.interceptor.LoginRequiredInterceptor;
import com.nowcoder.interceptor.PassportInterceptor;

@Component
public class WendaWebConfiguration extends WebMvcConfigurerAdapter{

	@Autowired
	PassportInterceptor passportInterceptor;
	
	@Autowired
	LoginRequiredInterceptor loginRequiredInterceptor;
	
	/**
	 * 全局配置，加载我们自己定义的passportInterceptor拦截器，这样才能使用
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//拦截器注册顺序也是调用顺序
		//就是这句话，注册我们自己的拦截器到整个链路上，未指定具体路径，则所有路径都会调用该拦截器
		registry.addInterceptor(passportInterceptor);
		
		//注册第二个拦截器
		//这里有addPathPatterns()属性，指定访问localhost:8080/user/*页面时调用该拦截器
		registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*");
		// TODO Auto-generated method stub
		super.addInterceptors(registry);
	}
}
