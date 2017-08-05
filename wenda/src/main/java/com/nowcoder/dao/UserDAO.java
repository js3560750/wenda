package com.nowcoder.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nowcoder.model.User;

//接口
@Mapper	//说明这是一个与MyBaits关联的一个DAO,DAO是专门用来与数据库进行交互的
public interface UserDAO {
	
	//注意空格,防止下面写SQL语句时少加了空格
	//为什么用这些常量字段，因为以后修改表的时候就只用修改这些常量字段，而不用一个一个去修改下面的每条sql语句了
	String TABLE_NAME=" user ";
	//字段的顺序和数据库里的顺序无关，只要字段都存在就好，下面语句的顺序要和这个顺序一样
	String INSERT_FIELDS=" name,password,salt,head_url ";
	String SELECT_FIELDS=" id, "+INSERT_FIELDS;
	
	//只有一个形参的时候，不需要用@Param
	@Insert({"insert ",TABLE_NAME,"(",INSERT_FIELDS,") values(#{name},#{password},#{salt},#{headUrl})"})
	int addUser(User user);
	
	@Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id=#{id}"})
	User selectById(int id);
	
	@Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where name=#{name}"})
	User selectByName(String name);
	
	@Update({"update ",TABLE_NAME," set password=#{password} where id=#{id}"})
	void updatePassword(User user);
	
	@Delete({"delete from",TABLE_NAME," where id=#{id}"})
	void deleteById(int id);
	

}
