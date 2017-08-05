package com.nowcoder.aspect;


import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.*;

@Aspect	//表明这是一个切面,在调用控制器任意方法之前，运行这个类里的beforeMethod方法，调用控制器任意方法之后，运行这个类里的afterMethod方法
@Component	//只要写了这个，那么在运用到切面的时候就会把这个对象构造出来
public class LogAspect {

	//日志用的 org.slf4j.*包
	private static final Logger logger=LoggerFactory.getLogger(LogAspect.class);
	
	@Before("execution(* com.nowcoder.controller.IndexController.*(..))")	//表示com.nowcoder.controller.IndexController中的任意方法任意参数
	public void beforeMethod(){
		logger.info("before method");	//会显示在控制台中
	}
	
	@After("execution(* com.nowcoder.controller.IndexController.*(..))")
	public void afterMethod(){
		logger.info("after method");	//会显示在控制台中
		
	}
}
