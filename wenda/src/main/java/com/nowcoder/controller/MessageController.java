package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;



@Controller
public class MessageController {

	//日志
	private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
	
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	UserService userService;
	
	
	//添加消息
	//这里的toName字段是String类型的，是收件人昵称
	@RequestMapping(path={"/msg/addMessage"},method={RequestMethod.POST})
	@ResponseBody
	public String addMessage(@RequestParam("toName") String toName,@RequestParam("content") String content){
		
		try {
			if(hostHolder.getUser()== null){
				return WendaUtil.getJSONString(999,"未登录");
			}
			
			User user= userService.selectByName(toName);
			if(user==null){
				return WendaUtil.getJSONString(2,"用户不存在");
			}
			
			Message msg= new Message();
			msg.setContent(content);
			msg.setFromId(hostHolder.getUser().getId());
			msg.setCreatedDate(new Date());
			msg.setHasRead(0);	//0代表消息未读 ，1代表消息已读
			msg.setToId(user.getId());
			
			messageService.addMessage(msg);

			return WendaUtil.getJSONString(0);	//发送0代表成功
			
		} catch (Exception e) {
			logger.error("添加站内信消息发生错误"+e.getMessage());
			return WendaUtil.getJSONString(1,"添加站内信消息失败");
		}
	}
	

    
    
    //获取某一个具体的会话内容
    //比如http://localhost:8080/msg/detail?conversationId=13_14
    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @Param("conversationId") String conversationId) {
        try {
        	//点进了具体的会话中，将该会话的未读消息全置为已读消息，也就是has_read从0置为1
        	//两个判断条件，一个是会话Id，一个是收件人是当前登录的用户
        	int localUserId = hostHolder.getUser().getId();
        	messageService.setUnreadToRead(localUserId,conversationId);
        	
        	//注意这里用到了分页，getConversationDetail()
        	//根据会话ID获取两个人之间的所有会话
        	//根据这个会话的每条消息获取发件人的信息，因为是某一个具体的会话，因此发件人应该都是同一个人
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        } catch (Exception e) {
            logger.error("获取详情消息失败" + e.getMessage());
        }
        return "letterDetail";
    }
	
    
    
    
    
    
	//显示消息列表
    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                //下面这个意思是消息列表里只显示对方发给我的消息，而不显示我发给对方的消息
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                //显示未读的消息数目
                vo.set("unread", messageService.getConvesationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
                model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
	}
	
	
}
