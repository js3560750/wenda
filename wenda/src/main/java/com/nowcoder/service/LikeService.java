package com.nowcoder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;


/**
 * 评论的点赞、踩 服务
 * @author 18894
 *
 */
@Service
public class LikeService {

	@Autowired
	JedisAdapter jedisAdapter;
	
	
	//点赞
	public long like(int userId ,int entityType, int entityId,int commentId){
		
		//如果该用户“点赞”了
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId,commentId);
		jedisAdapter.sadd(likeKey, String.valueOf(userId));
		
		//那么该用户的踩就要删除（不管该用户是否点击了“踩”）
		String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId,commentId);
		jedisAdapter.srem(disLikeKey, String.valueOf(userId));
		
		//返回likeKey这个评论即entityType和entityId确定的评论的点赞数
		return jedisAdapter.scard(likeKey);
	}
	
	//踩
	public long disLike(int userId ,int entityType, int entityId,int commentId){
		
		//如果该用户“踩”了
		String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId,commentId);
		jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
		
		//那么该用户的点赞就要删除（不管该用户是否点击了“赞”）
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId,commentId);
		jedisAdapter.srem(likeKey, String.valueOf(userId));
		
		//返回likeKey这个评论即entityType和entityId确定的评论的"赞"数
		//依旧返回的是赞数，因为前台只显示点赞数
		return jedisAdapter.scard(likeKey);
	}
	
	
	//单纯的获得某个评论的点赞数
	public long getLikeCount(int entityType,int entityId,int commentId){
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId,commentId);
		
		return jedisAdapter.scard(likeKey);
	}
	
	//返回用户是否给该评论点过赞
	//如果该用户给该评论点了赞，则返回1
	//如果点了“踩”，则返回-1
	//如果没点赞也没点踩，则返回0
	public int getLikeStatus(int userId, int entityType, int entityId,int commentId){
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId,commentId);
		if(jedisAdapter.sismember(likeKey, String.valueOf(userId))){
			return 1;
		}
		String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId,commentId);
		return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
