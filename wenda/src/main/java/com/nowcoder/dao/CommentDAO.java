package com.nowcoder.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nowcoder.model.Comment;

@Mapper	//DAO层是接口！！！！
public interface CommentDAO {

	String TABLE_NAME = " comment ";
	//字段的顺序和数据库里的顺序无关，只要字段都存在就好，下面语句的顺序要和这个顺序一样
	String INSERT_FIELDS = " content, user_id, entity_id, entity_type, created_date, status ";
	//id字段后面还有个逗号！！！
	String SELECT_FIELDS = " id, "+INSERT_FIELDS;
	
	//添加评论
	//注意插入是 insert into 不是insert to!!!!!!
	@Insert({ "insert into",TABLE_NAME,"(",INSERT_FIELDS,
		") values (#{content},#{userId},#{entityId},#{entityType},#{createdDate},#{status})"})
	int addComment(Comment comment);
   
	
	//更新评论状态，如果status为0则评论正常显示，status为1则不显示该评论
	//注意这里的where语句中的entity_id=#{entityId} 不能写成entityId=#{entityId} !!!!!!!!!!!!!!!!
	//参数之所以要写@Param是让Java虚拟机运行的时候记住形参的名称，以方便在SQL语句中找对应的参数
	//因为Java运行时，不记录形参的名字，而是arg0,arg1,arg2这种形式记录的
	@Update({"update ",TABLE_NAME,"set status=#{status} where entity_id=#{entityId} and entity_type=#{entityType}"})
	void updateStatus(@Param("entityId") int entityId,
					  @Param("entityType") int entityType,
					  @Param("status") int status);
	
	//获得某一问题或者某个其他人评论下的所有评论具体内容
	//注意这里的where语句中的entity_id=#{entityId} 不能写成entityId=#{entityId} !!!!!!!!!!!!!!!!
	@Select({"select ",SELECT_FIELDS,"from",TABLE_NAME," where entity_id=#{entityId} and entity_type=#{entityType} order by id desc "})
	List<Comment> selectByEntity(@Param("entityId") int entityId,
								 @Param("entityType") int entityType);
	
	//获得某一问题或者某个其他人评论下的所有评论具体内容
	@Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id=#{id}"})
	Comment getCommentById(int id);
	
	//获取某一问题或者某个其他人评论下所有评论的数量
	//注意这里的where语句中的entity_id=#{entityId} 不能写成entityId=#{entityId} !!!!!!!!!!!!!!!!
	@Select({"select count(id) from",TABLE_NAME,"where entity_id=#{entityId} and entity_type=#{entityType}"})
	int getCommentCount(@Param("entityId") int entityId,
						@Param("entityType") int entityType);
	
	//获取该用户总的评论数
    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);
}