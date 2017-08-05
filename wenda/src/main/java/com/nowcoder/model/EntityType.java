package com.nowcoder.model;

//评论中心要用的对象类型，如果要增加对象类型，则在这个类里增加
public class EntityType {

	public static int ENTITY_QUESTION = 1;	//1表示评论的对象是问题
	public static int ENTITY_COMMENT = 2;	//2表示评论的对象是其他人的评论
	public static int ENTITY_USER = 3;	//3表示用户
}
