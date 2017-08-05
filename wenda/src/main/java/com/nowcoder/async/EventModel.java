package com.nowcoder.async;



import java.util.HashMap;
import java.util.Map;

/**
 * 事件的模型
 * @author 18894
 *
 */
public class EventModel {

	private EventType type;	//事件类型
	private int actorId;	//发送者，触发者
	private int entityType;	//entityType和entityId共同确定具体的事件
	private int entityId;
	//entityType和entityId共同确定具体的事件的所有者、相关者
	//比如actorId评论了某个问题，那么这个entityOwnerId就是这个问题的提出者
	private int entityOwnerId;	
	
	//扩展字段，类似ViewObject，存储于这个事件发生时一些其他相关的属性
	//上面的actorId啥的其实都可以放入这个Map
	private Map<String, String> exts = new HashMap<String,String>();

	//改写getExts和setExts，纯粹用来方便读取和设置一些字段
	public String getExts(String key) {
		//return exts;
		return exts.get(key);
	}

	//改写getExts和setExts，纯粹用来方便读取和设置一些字段
	public EventModel setExts(String key,String value) {
		//this.exts = exts;
		exts.put(key, value);
		return this;	//this指的是EventModel对象
	}
	
	/**
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!Exts属性默认的get和set方法也要保留！！！！不然LikeController中的setExts方法没有效果，questionId传值为null
	 * 虽然我也觉得很迷，按理说调用的应该是上面改写的setExts方法呀!!!!!!!!!!!!!!!!!!!
	 * @return
	 */
	public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
	
	//默认构造函数
	public EventModel() {
		super();
	}

	//重写一个构造器，方便在LikeController中调用
	public EventModel(EventType eventType) {
		this.type=eventType;
		
	}

	/**
	 * 在所有的set函数里，都添加return EventModel，这样可以实现“链式调用”
	 * 链式调用： EventModel.setType().setTime().setEntityType.set.....这就是链式调用
	 * 
	 * @return EventModel
	 */
	
	
	public EventType getType() {
		return type;
	}

	public EventModel setType(EventType type) {
		this.type = type;
		return this;
	}

	public int getActorId() {
		return actorId;
	}

	public EventModel setActorId(int actorId) {
		this.actorId = actorId;
		return this;
	}

	public int getEntityType() {
		return entityType;
	}

	public EventModel setEntityType(int entityType) {
		this.entityType = entityType;
		return this;
	}

	public int getEntityId() {
		return entityId;
	}

	public EventModel setEntityId(int entityId) {
		this.entityId = entityId;
		return this;
	}

	public int getEntityOwnerId() {
		return entityOwnerId;
	}

	public EventModel setEntityOwnerId(int entityOwnerId) {
		this.entityOwnerId = entityOwnerId;
		return this;
	}


	
	
}