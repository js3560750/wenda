package com.nowcoder.model;

import java.util.Date;

public class Message {
	
    private int id;
    private int fromId;
    private int toId;
    private String content;
    private Date createdDate;
    private int hasRead;
    private String conversationId;
	
	//重写了getConversationId方法
	//以保证A发给B和B发给A是同一个会话，比如用户13和用户ID为27的会话，conversation_id就是13_27，不论谁发给谁，都是13_27。
	public String getConversationId() {
		if(fromId<toId){
			return String.format("%d_%d", fromId,toId);
		}else{
			return String.format("%d_%d", toId,fromId);
		}
	}
	
	//删掉了setConvesationId方法，因为上面的get方法里的语句已经给getConversationId赋了值
	/*
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	*/
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFromId() {
		return fromId;
	}
	public void setFromId(int fromId) {
		this.fromId = fromId;
	}
	public int getToId() {
		return toId;
	}
	public void setToId(int toId) {
		this.toId = toId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public int getHasRead() {
		return hasRead;
	}
	public void setHasRead(int hasRead) {
		this.hasRead = hasRead;
	}
	

}
