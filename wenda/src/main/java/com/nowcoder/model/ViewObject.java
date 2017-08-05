package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {	//不是视图，是传递对象跟velocity中间的一个对象

	private Map<String,Object> objs=new HashMap<String,Object>();
	
	public void set(String key,Object value){
		objs.put(key, value);
	}
	
	public Object get(String key){
		return objs.get(key);
	}
}
