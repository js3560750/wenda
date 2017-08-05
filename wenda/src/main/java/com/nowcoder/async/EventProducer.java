package com.nowcoder.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;

/**
 * 事件的入口，事件的产生者
 * 
 * 我们用Redis来实现把事件推送入队列，所以使用Redis中的队列操作
 * 或者使用java里的BlockingQueue是一样的
 * @author 18894
 *
 */
@Service
public class EventProducer {

	@Autowired
	JedisAdapter jedisAdapter;
	

	
	/**
	 * 把事件发送到队列
	 * @param eventModel
	 * @return
	 */
	public boolean fireEvent(EventModel eventModel){
		
		try {
			//这里也可以用BlockingQueue实现队列
			
			//使用Redis实现队列
			//把eventModel序列化成JSON字符串
			String json = JSONObject.toJSONString(eventModel);
			//所有往Redis中存入的键名都通过RedisKeyUtil得到
			String key = RedisKeyUtil.getEventQueueKey();
			//把序列化后的evetnModel存入Redis的队列中,lpush，从左边往队列中塞入
			jedisAdapter.lpush(key, json);
			
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
}
