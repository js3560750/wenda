package com.nowcoder.util;


/**
因为key名字不能重复，否则数据会被冲掉，这就GG思密达了
而且不同类型的key用不同的业务，有的用hash，有的用sets
所以专门有这个RedisKeyUtil的工具类负责生成所有的RedisKey
 */
public class RedisKeyUtil {

	private static String SPLIT = ":";
	private static String BIZ_LIKE = "LIKE";
	private static String BIZ_DISLIKE = "DISLIKE";
	private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";	//事件队列
	
	//粉丝
	private static String BIZ_FOLLOWER = "FOLLOWER";
	//关注对象
	private static String BIZ_FOLLOWEE= "FOLLOWEE";
	private static String BIZ_TIMELINE=	"TIMELINE";
	
	
	//点赞和踩
	public static String getLikeKey(int entityType,int entityId,int commentId){
		return BIZ_LIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId)+SPLIT+String.valueOf(commentId);
	}
	
	public static String getDisLikeKey(int entityType,int entityId,int commentId){
		return BIZ_DISLIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId)+SPLIT+String.valueOf(commentId);
	}
	
	//事件队列
	public static String getEventQueueKey(){
		return BIZ_EVENTQUEUE;
	}
	
	// 某个实体的粉丝key
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    // 每个用户对某类实体的关注key,比如用户关注“问题”，这里的问题指所有的问题
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }

    public static String getTimelineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + String.valueOf(userId);
    }
	
	
	
	
}
