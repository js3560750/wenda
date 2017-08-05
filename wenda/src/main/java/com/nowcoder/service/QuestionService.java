package com.nowcoder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;

@Service
public class QuestionService {

	@Autowired
	QuestionDAO questionDAO;
	
	@Autowired
	SensitiveService sensitiveService;
	
	//增加问题
	public int addQuestion(Question question){
		//这里用HtmlUtils.htmlEscape()函数对用户输入进行转译，比如<script>alert("hi");</script>中的<>这类符号就会被转译，避免用户恶意输入
		question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
		question.setContent(HtmlUtils.htmlEscape(question.getContent()));
		//敏感词过滤
		question.setTitle(sensitiveService.filter(question.getTitle()));
		question.setContent(sensitiveService.filter(question.getContent()));
		return questionDAO.addQuestion(question)>0?question.getId():0;
	}
	
	//点进问题的详细内容
	public Question getById(int id){
		return questionDAO.getById(id);
	}
	
	
	//获得最近的问题，limit限制条数
	public List<Question> getLatestQuestions(int userId,int offset,int limit){
		return questionDAO.selectLatestQuestions(userId, offset, limit);
	}
	
	//更新该问题下的评论数目
    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }
}
