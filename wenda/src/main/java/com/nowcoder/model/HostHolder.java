package com.nowcoder.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {

	//ThreadLocal<>的意思是看起来像是一个变量，其实这个变量每个线程都有一份拷贝
	//每个线程拷贝的这个变量所占有的内存是不一样的，但又能通过一个公共的接口来访问
	private static ThreadLocal<User> users=new ThreadLocal<User>();
	
	//当调用getUser时，会根据当前线程来找到当前线程保存的user变量，下面两个方法是一样的意思
	public User getUser(){
		return users.get();
	}
	
	public void setUser(User user){
		users.set(user);
	}
	
	public void clear(){
		users.remove();
	}
}
