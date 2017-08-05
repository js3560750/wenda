package com.nowcoder.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;

//事件处理器一定要继承EventHandler，这样EventConsumer才能发现这个事件处理器
//功能：如果有用户“点赞”，那么发一条站内信给被点赞的用户
//这里一定要添加为组件！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！不添加组件，EventConsumer初始化时就搜索不到该LikeHandler
@Component
public class LikeHandler implements EventHandler{
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	UserService userService;

	//写该事件处理器的具体处理逻辑
	//这里是，如果有用户“点赞”，那么发一条站内信给被点赞的用户
	@Override
	public void doHandle(EventModel model) {
		// TODO Auto-generated method stub
		
		Message message = new Message();
		
		//消息发送者，默认系统管理员
		message.setFromId(WendaUtil.SYSTEM_USERID);
		
		//消息接受者
		message.setToId(model.getEntityOwnerId());
		
		//日期
		message.setCreatedDate(new Date());
		
		//谁点了这个赞
		User user = userService.getUser(model.getActorId());
		message.setContent("用户"+ user.getName()+"赞了你的评论"+"评论的链接地址，http://127.0.0.1:8080/question/"+model.getExts("questionId"));
		
		//添加到消息服务里去
		messageService.addMessage(message);
		
		
	}

	@Override
	public List<EventType> getSupportEventTypes() {
		// TODO Auto-generated method stub
		//返回这个事件处理器处理的事件类型
		return Arrays.asList(EventType.LIKE);
	}
}