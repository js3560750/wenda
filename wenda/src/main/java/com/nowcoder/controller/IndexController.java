package com.nowcoder.controller;

import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nowcoder.model.User;
import com.nowcoder.service.WendaService;

@Controller	//表明这是一个控制器
public class IndexController {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@RequestMapping(path = {"/test"},method={RequestMethod.GET})	//这里返回一个模板，所以去掉了@ResponseBody
	public String test(){
		
		return "test";	//指定返回的模板也就是html文件
	}
	
	//多个控制器中所有的路径都不能重复，即使不同的控制器，访问路径也不能重复，否则也会报错即whitelabel error page
	
	@RequestMapping(path = {"/hometest"}, method = {RequestMethod.GET})
	@ResponseBody
	public String index(HttpSession httpSession) {
	        logger.info("VISIT HOME");
	        return "Hello NowCoder" + httpSession.getAttribute("msg");
	}
	 
	@RequestMapping(path={"/profile/{gorupName}/{userId}"})	//指定映射地址
	@ResponseBody			//表示返回的是字符串而不是模板
	public String profile(@PathVariable("userId") int userId,
						@PathVariable("gorupName") String gorupName,	//@PathVariable定义url路径的名称并接受进来
						@RequestParam("type") int type,		//@RequestParam接受url的参量
						@RequestParam(value="key",defaultValue="默认值",required=false) String key){
		return String.format("Profile Page of %s / %d,t:%d ,k:%s",gorupName, userId,type,key);	//String.format是java字符串格式化，%d整数型，%s字符串，%f浮点型
		
	}
	
	@RequestMapping(path = {"/vm"},method={RequestMethod.GET})	//这里返回一个模板，所以去掉了@ResponseBody
	public String template(Model model){
		
		model.addAttribute("value1","value1的值");
		
		List<String> colors=Arrays.asList(new String[]{"red","green","blue"});
		model.addAttribute("colors",colors);
		
		Map<String,String> map=new HashMap<>();
		for (int i = 0; i < 4; i++) {
			map.put(String.valueOf(i), String.valueOf(i*i));
		}
		model.addAttribute("map", map);
		
		model.addAttribute("user", new User("LEE"));
		
		
		return "home";	//指定返回的模板也就是html文件
	}
	
	@RequestMapping(path={"/request"},method={RequestMethod.GET})	
	@ResponseBody	//没有调用模板，因此要加上这句，即返回是一个文本
	public String request(Model model,
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession httpSession,
			@CookieValue("JSESSIONID") String seesionID	){	//JSESSIONID是这个Cookie的名字？？
		
		StringBuilder sb=new StringBuilder();
		//获得Cookie 方法1
		sb.append("Cookie:"+seesionID+"<br/>");
		
		//获得request请求头里的信息并存入headerNames并逐个存入StringBuilder里
		Enumeration<String> headerNames=request.getHeaderNames();
		while(headerNames.hasMoreElements()){
			String name=headerNames.nextElement();
			sb.append(name+":"+request.getHeader(name)+"<br/>");
		}
		//读取Cookie 方法2
		if(request.getCookies()!=null){
			for(Cookie cookie:request.getCookies()){
				sb.append("Cookies:"+cookie.getName()+" Value:"+cookie.getValue());
			}
		}
		sb.append(request.getMethod()+"<br/>");
		sb.append(request.getPathInfo()+"<br/>");
		sb.append(request.getQueryString()+"<br/>");
		sb.append(request.getRequestURI()+"<br/>");
		
		response.addHeader("JinSong", "handsome");	//添加一个返回头，能在浏览器解析器中的Response Headers中查看到
		response.addCookie(new Cookie("username","js"));	//添加一个Cookie，能在Response Cookies中查看到
		return sb.toString();
		
	}
	
	//重定向，输入http://127.0.0.1:8080/redirect/int型时直接跳转到return指定的页面并传递了一个msg的Session过去
	@RequestMapping(path={"/redirect/{code}"},method={RequestMethod.GET})
	public String redirect(@PathVariable("code")int code,
			HttpSession httpSession){
		httpSession.setAttribute("msg", "Jump form Redirect!");
		return "redirect:/";	//重定向到首页127.0.0.1:8080去了
		/*
		 * 或者写成这样判断code的值来进行跳转
		 * RedirectView red =new RedirectView("/",true)	//设置跳转的页面，设置true就是相对路径。这里也是设置为跳转到首页127.0.0.1:8080/
		 * if(code==301)
		 * {
		 * 	red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);	//还能设置状态码，MOVED_PERMANENTLY代表301
		 * }
		 * return red;
		 * 
		 */
	}
	
	//异常的捕获，定义了异常的统一处理
	@ExceptionHandler
	@ResponseBody
	public String error(Exception e){
		return "error:"+e.getMessage();
	}
	
	//异常示例
	@RequestMapping(path={"/admin"},method={RequestMethod.GET})
	@ResponseBody
	public String admin(@RequestParam("key") String key){
		if("admin".equals(key)){
			return "hello admin";
		}
		throw new IllegalArgumentException("参数不对");	//抛出异常，并调用上面的error函数进行异常的统一处理
	}
	
	//IOC,Spring特有的控制反转、依赖注入
	@Autowired	//这个依赖注入的不能写进方法里啊！在com.nowcoder.service中的WendaService.java中定义了WendaService这个对象
	WendaService wendaService;
	
	@RequestMapping(path={"/ioc"},method={RequestMethod.GET})
	@ResponseBody
	public String ioc(){
		//@Autowired 写在这里就不行
		//WendaService wendaService;
		return "控制反转"+wendaService.getMessage();
	}
}
