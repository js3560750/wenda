package com.nowcoder.model;

import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * 评论中心
 * @author 18894
 *
 */
//模型可以不加@Commpent，但HostHolder加了@Commpent
public class Comment {

	private int id;
	private int userId;
	private int entityId;
	private int entityType;
	private String content;
	private Date createdDate;
	private int status;	//因为评论有可能被删除或者隐藏，评论是否正常显示根据status字段判断
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getEntityId() {
		return entityId;
	}
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}
	public int getEntityType() {
		return entityType;
	}
	public void setEntityType(int entityType) {
		this.entityType = entityType;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
