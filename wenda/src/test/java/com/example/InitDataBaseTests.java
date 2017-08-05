package com.example;

import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.nowcoder.WendaApplication;
import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")	//这句话的意思是运行程序前首先引用这个相对路径的sql语句并运行，用于创建初始表的，便于测试下面的程序，每运行一次，表都会被删除并重建
public class InitDataBaseTests {
	
	@Autowired
	UserDAO userDAO;
	
	@Autowired
	QuestionDAO questionDAO;

	@Test
	public void contextLoads() {
		Random random=new Random();
		//循环创建了10个user对象并进行了操作
		for(int i=1;i<11;i++){
			User user=new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setPassword("");
			user.setSalt("");
			//插入上面创建的对象进user表里
			userDAO.addUser(user);
			
			//更新密码
			user.setPassword("newpassword3");
			userDAO.updatePassword(user);
			
            Question question = new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            question.setCreatedDate(date);
            question.setUserId(i);
            question.setTitle(String.format("TITLE{%d}", i));
            question.setContent(String.format("Balaababalalalal Content %d", i));
            questionDAO.addQuestion(question);
			
		}
		
		
		
		//Assert.assertEquals是判断两个元素是否equals，如果都为null，也返回true，已经被弃用！！
		//Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
		//判断是否为空,Assert已经被弃用！！！
		//Assert.assertNull(userDAO.selectById(1));
		
		//userDAO.deleteById(7);	//删除了id=7的那一行
		
		System.out.println(questionDAO.selectLatestQuestions(0, 0, 10));
	}

}
