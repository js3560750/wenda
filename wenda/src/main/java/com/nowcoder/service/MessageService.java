package com.nowcoder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;



@Service
public class MessageService {

	@Autowired
	MessageDAO messageDAO;
	
	@Autowired
	SensitiveService sensitiveService;
	
	//添加消息
	//注意插入是 insert into 不是insert to!!!!!!
	public int addMessage(Message message){
		message.setContent(sensitiveService.filter(message.getContent()));
		return messageDAO.addMessage(message);
	}
	
	//获得一个会话的所有详细信息
	//注意这里用到了分页，limit #{offset},#{limit}，offset限制了搜索起始点，一般为0，limit限制了搜索的数目
	public List<Message> getConversationDetail(String conversationId,int offset,int limit){
		return messageDAO.getConversationDetail(conversationId, offset, limit);
	}
	
	//获得发给某人的一个会话中未读消息的总数目，这里是多个发件人发给同一个收件人，这个收件人的未读消息总数
	//就是未读邮件
	public int getConvesationUnreadCount(int userId,String conversationId){
		return messageDAO.getConvesationUnreadCount(userId, conversationId);
	}
	
	//获得某个用户所有的会话列表
    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);
    }
    
    //点进了具体的会话中，将该会话的未读消息全置为已读消息，也就是has_read从0置为1
    public void setUnreadToRead(int userId,String conversationId){
    	messageDAO.setUnreadToRead(userId,conversationId);
    };
}
