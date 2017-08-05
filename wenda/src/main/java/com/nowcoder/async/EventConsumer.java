package com.nowcoder.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;

/**
 * 处理所有队列里的事件
 * 
 * 我们有很多EventHandler，通过这个EventConsumer把事件分发到各个事件处理器里
 * 
 * @author 18894
 *
 */
//实现InitializingBean 是为了初始化，在初始化的时候就调用这个类
//实现ApplicationContextAware接口 是为了存储applicationContext对象
@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
	//日志
	private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
	
	@Autowired
	JedisAdapter jedisAdapter;
	
	
	//Map第一个参数放事件类型，第二个参数放这个事件类型所对应的所有事件处理器
	//比如EventType=点赞，当点赞这个事件发生时，我们就有活跃度Handler，成就Handler等来相应这个点赞事件
	private Map<EventType, List<EventHandler>> config = new HashMap<EventType,List<EventHandler>>();

	//上下文对象，通过它可以找出本工程所有的EventHandler的实现类
	//注意这个ApplicationContext所在的包，是Spring的包， 别搞错了
	private org.springframework.context.ApplicationContext applicationContext;
	
	//工程初始化时进行的操作
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		//上下文对象，通过它可以找出本工程所有的EventHandler的实现类
		//注意这个ApplicationContext所在的包，别搞错了
		Map<String, EventHandler> beans =  applicationContext.getBeansOfType(EventHandler.class);
		
		//如果存在EventHandler的实现类，把这些EventHandler和各个事件Event关联起来
		if(beans!=null){
			for(Map.Entry<String, EventHandler> entry : beans.entrySet()){
				List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
				
				for(EventType type: eventTypes){
					if(!config.containsKey(type)){
						config.put(type, new ArrayList<EventHandler>());
					}
					config.get(type).add(entry.getValue());
				}
			}
		}
		
		Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key);

                    for (String message : events) {
                        if (message.equals(key)) {
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }

                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}
	
	
}
