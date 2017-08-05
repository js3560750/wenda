package com.nowcoder.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.util.WendaUtil;

@Controller
public class LikeController {

	@Autowired
	LikeService likeService;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	EventProducer eventProducer;
	
	//日志
	private static final Logger logger = LoggerFactory.getLogger(LikeController.class);
	
	//点赞
	@RequestMapping(path={"/like"},method={RequestMethod.POST})
	@ResponseBody
	public String like(@RequestParam("commentId") int commentId){
		//如果用户未登录则返回999JSON字符串
		if(hostHolder.getUser()==null){
			return WendaUtil.getJSONString(999);
		}
		
		//根据commentId获取该Comment，然后获得该Comment的entityType和entityId
		Comment comment = commentService.getCommentById(commentId);
		
		//async包里的事件处理器相关
		//当用户点赞之后，异步的给被点赞用户发送站内信
		//fireEvent()函数里放EventModel需要在EventModel里写一个构造器可以传入EventType
		//这里的set使用了链式调用方法，在EventModel中必须重写每个set方法的返回类型都是EventModel
		eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
                .setExts("questionId", String.valueOf(comment.getEntityId())));//额外添加的questionId，方便在LikeHandler里写评论链接
		
		

		
		//likeService.like返回的是该评论的点赞总数
		long likeCount = likeService.like(hostHolder.getUser().getId(), comment.getEntityType(), comment.getEntityId(),comment.getId());
		
		//返回0代表成功
		return WendaUtil.getJSONString(0,String.valueOf(likeCount));
	}
	
	//踩
	@RequestMapping(path={"/dislike"},method={RequestMethod.POST})
	@ResponseBody
	public String disLike(@RequestParam("commentId") int commentId){
		//如果用户未登录则返回999JSON字符串
		if(hostHolder.getUser()==null){
			return WendaUtil.getJSONString(999);
		}
		
		//根据commentId获取该Comment，然后获得该Comment的entityType和entityId
		Comment comment = commentService.getCommentById(commentId);
		
		//likeService.disLike返回的是该评论的点赞总数
		long disLikeCount = likeService.disLike(hostHolder.getUser().getId(), comment.getEntityType(), comment.getEntityId(),comment.getId());
		
		//返回0代表成功
		return WendaUtil.getJSONString(0,String.valueOf(disLikeCount));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
