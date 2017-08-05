package com.nowcoder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;

@Service
public class CommentService {

	@Autowired
	private CommentDAO commentDAO;
	
	//获得某一问题或者某个其他人评论下的所有评论具体内容
	public List<Comment> getCommentsByEntity(int entityId,int entityType){
		return commentDAO.selectByEntity(entityId, entityType);
	}
	
	//添加评论
	public int addComment(Comment comment){
		return commentDAO.addComment(comment);
	}
	
	//获取某一问题或者某个其他人评论下所有评论的数量
	public int getCommentCount(int entityId,int entityType){
		return commentDAO.getCommentCount(entityId, entityType);
	}
	
	//更新评论状态，如果status为0则评论正常显示，status为1则不显示该评论，相当于删除评论
	public void deleteComment(int entityId,int entityType){
		commentDAO.updateStatus(entityId, entityType, 1);
	}
	
	//根据CommentId获取评论
	public Comment getCommentById(int id){
		return commentDAO.getCommentById(id);
	}
	
	//获取该用户总的评论数
    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }
}
