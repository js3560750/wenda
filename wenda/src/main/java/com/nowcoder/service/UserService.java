package com.nowcoder.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.controller.HomeController;
import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;

@Service
public class UserService {
	
	//日志 slf4j包里的
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private LoginTicketDAO loginTicketDAO;
	
	//通过id获取用户的各种信息
	public User getUser(int id){
		return userDAO.selectById(id);
	}
	
	//通过昵称获取用户的各种信息
    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }
	
	/**
	 * 用户注册
	 */
	public Map<String,Object> register(String username ,String password){
		
		//用map对象存放错误提示给前端
		Map<String,Object> map=new HashMap<String,Object>();
		//验证用户名是否为空
		if(StringUtils.isBlank(username)){
			map.put("msg", "用户名不能为空");
			return map;
		}
		//验证密码是否为空
		if(StringUtils.isBlank(password)){
			map.put("msg", "密码不能为空");
			return map;
		}
		
		//验证用户是否已经被注册
		User user=userDAO.selectByName(username);
		if(user!=null){
			map.put("msg", "用户名已经被注册");
			return map;
		}
		
		//新建一个User，把注册数据存入这个User
		user = new User();
		user.setName(username);
		//给密码增加salt
		//UUID全局唯一标识符
		//UUID.randomUUID().toString()会生成一串字符串，比如5c315f57-8df3-47e4-8eca-dbb120637846
		user.setSalt(UUID.randomUUID().toString().substring(0,5));
		//把密码和salt相连，并用MD5处理后存入数据库作为用户密码
		user.setPassword(WendaUtil.MD5(password+user.getSalt()));
		//头像地址
		String head=String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
		user.setHeadUrl(head);
		//进行数据库操作，添加新用户
		userDAO.addUser(user);
		
		//注册成功，则给一个T票方便控制器那边验证是否成功
		String ticket=addLoginTicket(user.getId());
		map.put("ticket", ticket);
		
		return map;
	}
	
	/**
	 * 用户登录
	 * 
	 * 基本的逻辑与用户注册是一样的,额外添加了next信息，如果用户未登录访问/user/*页面，则需要登录当用户登录失败时，仍然记录用户之前访问的页面，以便登录成功后跳转回该页面
	 */
    public Map<String, Object> login(String username, String password,String next) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msg", "用户名不能为空");
            map.put("next", next);	//额外添加了next信息，如果用户未登录访问/user/*页面，则需要登录当用户登录失败时，仍然记录用户之前访问的页面，以便登录成功后跳转回该页面
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            map.put("next", next);
            return map;
        }

        //根据输入的用户名去数据库中查找是否有这个用户名，如果没有，则返回NULL
        User user = userDAO.selectByName(username);

        if (user == null) {
            map.put("msg", "用户名不存在");
            map.put("next", next);
            return map;
        }

        //数据库中用户存的密码也是经过添加salt后MD5加密的
        if (!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())) {
            map.put("msg", "密码不正确");
            map.put("next", next);	//额外添加了next信息，如果用户未登录访问/user/*页面，则需要登录当用户登录失败时，仍然记录用户之前访问的页面，以便登录成功后跳转回该页面
            return map;
        }

        //登录成功，返回一个T票
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        //添加这一句是为了在LoginController中登录时发送邮件的事件处理器 eventProducer.fireEvent(new EventModel(EventType.LOGIN)那里调用
        map.put("userId", user.getId());
        return map;
    }
	
	/**
	 * 登录或者注册成功往数据库里添加T票
	 * 
	 */
	private String addLoginTicket(int userId){
		LoginTicket ticket = new LoginTicket();
		//设置userId
		ticket.setUserId(userId);
		//设置过期时间，这里是一天
		Date date = new Date();
		date.setTime(date.getTime()+1000*3600*24);	//1000毫秒
		ticket.setExpired(date);
		//设置状态
		ticket.setStatus(0);
		//设置T票，随机生成的全局唯一标示，比如5c315f57-8df3-47e4-8eca-dbb120637846，并且将标示中的“-”去掉了
		ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
		loginTicketDAO.addTicket(ticket);
		
		//返回刚设置的T票
		return ticket.getTicket();
	}
	
	/**
	 * 退出登录
	 */
	public void logout(String ticket){
		//将T票状态更新为1,1表示失效
		loginTicketDAO.updateStatus(ticket, 1);
	}
	

	
}
