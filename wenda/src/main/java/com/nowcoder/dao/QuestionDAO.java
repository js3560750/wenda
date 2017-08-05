package com.nowcoder.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nowcoder.model.Question;
import com.nowcoder.model.User;

//接口
@Mapper	//说明这是一个与MyBaits关联的一个DAO,DAO是专门用来与数据库进行交互的
public interface QuestionDAO {
	
	//注意空格,防止下面写SQL语句时少加了空格
	//为什么用这些常量字段，因为以后修改表的时候就只用修改这些常量字段，而不用一个一个去修改下面的每条sql语句了
	String TABLE_NAME = " question ";
	//字段的顺序和数据库里的顺序无关，只要字段都存在就好，下面语句的顺序要和这个顺序一样
	String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";//在mybatis-config.xml配置文件中已经开启了user_id自动转换为userId
	String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);
    
    //只有一个形参的时候，不需要用@Param
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME,"where id=#{id}"})
    Question getById(int id);
	
    //当sql语句特别复杂的时候，就用xml写sql语句，xml文件名字与本类的名字要一模一样
	//下面的这个是自动匹配src/main/resources/com.nowcoder.dao.QuestionDAO.xml中的selectLatestQuestions方法
    //设置了3个参数 ，这3个参数是QuestionDAO.xml中用到的
    //参数之所以要写@Param是让Java虚拟机运行的时候记住形参的名称，以方便在SQL语句中找对应的参数
  	//因为Java运行时，不记录形参的名字，而是arg0,arg1,arg2这种形式记录的
    List<Question> selectLatestQuestions(@Param("userId") int userId, 
    									 @Param("offset") int offset,
    									 @Param("limit") int limit);	

    //更新该问题下的评论数目
    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

}
