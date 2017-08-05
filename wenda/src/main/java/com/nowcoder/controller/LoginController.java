package com.nowcoder.controller;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.service.UserService;

@Controller
public class LoginController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	EventProducer eventProducer;
	


	//日志
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	//在localhost:8080/reglogin页面点击“注册”所执行的操作
	@RequestMapping(path = {"/reg"},method={RequestMethod.POST})	//这里返回一个模板，所以去掉了@ResponseBody
	public String reg(Model model,
					  @RequestParam("username") String username,
					  @RequestParam("password") String password,
					  @RequestParam("next") String next,	
					  @RequestParam(value="rememberme",defaultValue="false") boolean rememberme,
					  HttpServletResponse response
					 ){
			
		try {
			Map<String,Object> map=userService.register(username, password);
			
			if(map.containsKey("ticket")){
				Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
				//正常的cookie只能在一个应用中共享，即一个cookie只能由创建它的应用获得
				//可在同一应用服务器内共享方法：设置cookie.setPath("/");
				cookie.setPath("/");
				if(rememberme){	//如果用户在登录界面勾选了“记住我”
					cookie.setMaxAge(3600*24*5);//过期时间5天
				}
				response.addCookie(cookie);
				
				//next属性，该属性记录了用户未登录访问某些页面跳转到登录页面后之前访问页面的地址，以便登录成功后再重新跳转回之前的页面 
				//next参数应该还需要增加一个判断，不能等于http://www.baidu.com这样的，否则就跳转到外链去了，是不安全的
				if(StringUtils.isNotBlank(next)){
					return "redirect:"+next;
				}
				
				//登录成功，跳转到首页
				return "redirect:/";
			}else{
				//注册失败，返回到登录界面
				model.addAttribute("msg", map.get("msg"));
				return "login";
			}
			
		} catch (Exception e) {
			//注册异常返回到login页面
			logger.error("注册异常"+e.getMessage());
			model.addAttribute("msg", "服务器错误");
			return "login";
		}
		
	}
	
	//登录注册的页面
	@RequestMapping(path = {"/reglogin"},method={RequestMethod.GET})	//这里返回一个模板，所以去掉了@ResponseBody
		public String reg(Model model,
				@RequestParam(value="next",required=false) String next
				){
		
		model.addAttribute("next",next);
		//返回login.html
		return "login";
	}
	
	//在localhost:8080/reglogin页面点击“登录”所执行的操作
    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login(Model model, 
    					@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value="next", required = false) String next,
                        @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response
                       ) {
        try {
        	//将用户名和密码传给userService中的login函数处理，login函数负责用户登录的逻辑处理
        	//返回的map，如果包含“ticket”则登录成功，如果没有登录成功，会包含“msg”记录错误信息
            Map<String, Object> map = userService.login(username, password,next);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                
                //异步的事件处理器
                //LoginExceptionHandler
                //如果登录异常了，就把登录的相关信息发过去
                //其实没有写异常判断的逻辑
                //只要用户登录了，都会发送邮件
               eventProducer.fireEvent(new EventModel(EventType.LOGIN)
            		   .setExts("username", username).setExts("email", "188949420@qq.com") 	//这里设置email设置的收件人,QQ可以给自己发邮件
            		   .setActorId((int)map.get("userId")));
               
                
                //next属性，该属性记录了用户未登录访问某些页面跳转到登录页面后之前访问页面的地址，以便登录成功后再重新跳转回之前的页面
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                //如果登录成功则跳转到首页
                return "redirect:/";
            } else {
            	//如果登录失败返回登录页面，并携带错误信息“msg”
                model.addAttribute("msg", map.get("msg"));
                //额外添加了next信息，如果用户未登录访问/user/*页面，则需要登录当用户登录失败时，仍然记录用户之前访问的页面，以便登录成功后跳转回该页面
                //把next信息传给前端隐藏的next属性，以便用户第一次登录失败，第二次登录成功后仍然可以跳转回之前要访问的页面
                model.addAttribute("next", map.get("next"));	
                return "login";
            }

        } catch (Exception e) {
        	//登录异常也返回登录页面
            logger.error("登陆异常" + e.getMessage());
            return "login";
        }
    }
    
    //退出操作
  	@RequestMapping(path = {"/logout"},method={RequestMethod.GET})	//这里返回一个模板，所以去掉了@ResponseBody
  		public String logout(@CookieValue("ticket") String ticket){
  		
  		//调用userService中的logout方法，将该ticket的status状态置为1
  		userService.logout(ticket);
  		//返回login.html
  		return "redirect:/";
  	}
}
