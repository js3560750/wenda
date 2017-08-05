package com.nowcoder.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nowcoder.model.LoginTicket;

@Mapper	//说明这是一个与MyBaits关联的一个DAO,DAO是专门用来与数据库进行交互的
public interface LoginTicketDAO {
	
    String TABLE_NAME = "login_ticket";
    //字段的顺序和数据库里的顺序无关，只要字段都存在就好，下面语句的顺序要和这个顺序一样
    String INSERT_FIELDS = " user_id, expired, status, ticket ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    //插入一个新的数据
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket ticket);

    //根据ticket获得数据
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

    //更新数据，根据ticket选择数据并更新status
    @Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);
}
