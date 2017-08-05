package com.nowcoder.async;

/*
 * 这是一个枚举型即Enum类型
 * 
 * 专门用来放事件的类型
 * 
 */
public enum EventType {

	LIKE(0),
	COMMENT(1),
	LOGIN(2),
	MAIL(3),
	FOLLOW(4),
    UNFOLLOW(5);
	
	private int value;
	
	//添加了这个构造器后，上面的LIKE(0)啥的就不报错了
	EventType(int value){
		this.value=value;
	}
	
	public int getValue(){
		return value;
	}
}
