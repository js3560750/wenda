package com.nowcoder.interceptor;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;

/**
 * 拦截器
 * @author 18894
 *
 */
@Component
public class PassportInterceptor implements HandlerInterceptor{
	
	@Autowired
	private LoginTicketDAO loginTicketDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private HostHolder hostHolder;
	
	/**
	 * 作用在所有请求发生之前
	 * 
	 * 本拦截器的作用就是检测用户是否登录，如果已登录，则持有一个全局变量hostHolder，hostHolder在Model层定义
	 */
	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse arg1, Object arg2) throws Exception {
		
		
		String ticket=null;
		//检测用户是否已经登录，已经登录则给ticket赋值，如果没有登录，则ticket仍为null
		//如果请求中包含cookies
		if(httpServletRequest.getCookies()!=null){
			//遍历这些cookies
			for(Cookie cookie:httpServletRequest.getCookies()){
				//如果这些cookie中有名字为“ticket”的cookie，则把它的值赋给ticket
				if(cookie.getName().equals("ticket")){
					ticket = cookie.getValue();
					break;
				}
			}
		}
		
		
		if(ticket!=null){
			//如果用户是已经登录的状态，根据ticket值在数据库中查找出loginTicket的所有信息
			LoginTicket loginTicket=loginTicketDAO.selectByTicket(ticket);
			//如果查询到的loginTicket为空，或者已经过期，或者status不为0即失效，则返回true，弹出
			//注意，loginTicket.getExpired()是过期时间，new Date()是当前时间。
			//A.before(B) 方法的含义是如果A在B之前则返回true
			if( loginTicket==null || loginTicket.getExpired().before(new Date())|| loginTicket.getStatus()!=0){
				return true;
			}
			
			//如果该loginTicket是有效的，根据T票查找出该用户信息并存入hostHolder
			//因为本代码写在preHandler拦截器里，因此这里设置的hostHolder相当于全局变量，可以在任何一个控制器调用该user
			User user= userDAO.selectById(loginTicket.getUserId());
			hostHolder.setUser(user);
			
		}
		
		
		//如果返回false，则请求结束，后面包括控制器的操作都不会进行
		//return false;
		return true;
	}


	/**
	 * 页面渲染之前调用
	 */
	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView modelAndView)
			throws Exception {
		//把user存入modelAndView，那么就能在视图页面中调用user变量
		//又因为本代码写在postHandler拦截器里，所以所有的视图页面都能调用User变量
		if(modelAndView !=null && hostHolder.getUser() !=null){
			modelAndView.addObject("user", hostHolder.getUser());
			
		}
		
	}
	
	/**
	 * 页面渲染之后调用
	 */
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
		//记得要清除hostHolder，不然会积累很多
		hostHolder.clear();
		
	}



}
