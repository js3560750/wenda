package com.nowcoder.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nowcoder.model.HostHolder;

/**
 * 拦截器
 * 
 * 当访问特定页面时检测用户是否登录，未登录跳转到登录页面，登录成功后跳转回用户要访问的页面
 * @author 18894
 *
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor{

	@Autowired
	private HostHolder hostHolder;
	
	/**
	 * 作用在请求发生之前
	 */
	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object arg2) throws Exception {
		//如果用户没有登录，则跳转到reglogin登录注册页面，并记录下当前的访问地址，以便登陆后跳转回该地址
		//可以在注册拦截器那里配置哪些页面调用该拦截器
		if(hostHolder.getUser()==null){
			httpServletResponse.sendRedirect("/reglogin?next="+httpServletRequest.getRequestURI());
			//跳转到登录页面去了，返回false
			return false;
		}
		//如果用户登陆了，返回true，以便接下来的操作
		return true;
	}
	
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}



	
}
