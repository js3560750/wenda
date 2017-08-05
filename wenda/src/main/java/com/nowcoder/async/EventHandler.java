package com.nowcoder.async;

import java.util.List;

/**
 * 这是一个接口
 * 
 * 专门存放所有的事件处理器
 * 
 * @author 18894
 *
 */
public interface EventHandler {

	//事件处理器处理事件
	void doHandle(EventModel model);
	
	//事件处理器注册自己，让别人知道自己关注哪些EventType，当这些类型的事件发生后，就要进行处理
	List<EventType> getSupportEventTypes();
}
