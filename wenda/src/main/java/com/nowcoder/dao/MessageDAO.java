package com.nowcoder.dao;

import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by nowcoder on 2016/7/9.
 */
@Mapper
public interface MessageDAO {
	
	String TABLE_NAME = " message ";
	//字段的顺序和数据库里的顺序无关，只要字段都存在就好，下面语句的顺序要和这个顺序一样
	String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id ";
	//id字段后面还有个逗号！！！
	String SELECT_FIELDS = " id, "+INSERT_FIELDS;

	//添加消息
	//注意插入是 insert into 不是insert to!!!!!!
	@Insert({ "insert into",TABLE_NAME,"(",INSERT_FIELDS,
		") values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
	int addMessage(Message message);

	//获得一个会话的所有详细信息
	//注意这里用到了分页，limit #{offset},#{limit}，offset限制了搜索起始点，一般为0，limit限制了搜索的数目
	@Select({"select ",SELECT_FIELDS,"from",TABLE_NAME,"where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit}"})
	List<Message> getConversationDetail(@Param("conversationId") String conversationId,
										@Param("offset") int offset,
										@Param("limit") int limit);

	//获得发给某人的一个会话中未读消息的总数目，这里是多个发件人发给同一个收件人，这个收件人的未读消息总数
	//has_read=0 表示未读
	//就是未读邮件
	//注意to_id=#{userId}  而不是to_id=#{toId}
	@Select({"select count(id) from ",TABLE_NAME,"where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
	int getConvesationUnreadCount(@Param("userId") int userId,
								  @Param("conversationId") String conversationId);


	//获得某个用户所有的会话列表
	//这里有个bug没解决，就是mysql5.7不识别order by 模式下的子查询排序，因此显示的消息不是最新的，而是最旧的
    @Select({"select ", INSERT_FIELDS, " ,count(id) as id from ( select * from ", TABLE_NAME, " where from_id=#{userId} or to_id=#{userId} order by id desc) tt group by conversation_id  order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset, @Param("limit") int limit);
    
    //点进了具体的会话中，将该会话的未读消息全置为已读消息，也就是has_read从0置为1
    //两个判断条件，一个是会话Id，一个是收件人是当前登录的用户
    @Update({" update ",TABLE_NAME,"set has_read=1 where conversation_id=#{conversationId} and to_id=#{userId}"})
    void setUnreadToRead(@Param("userId") int userId,@Param("conversationId") String conversationId);
}

