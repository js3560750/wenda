package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;

@Controller
public class HomeController {
	
	//日志
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	UserService userService;
	
	@Autowired
	QuestionService questionService;
	
    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;
	
	
	
	//根据用户ID获取用户的问题信息
	@RequestMapping(path = {"/user/{userId}"},method={RequestMethod.GET})	//这里返回一个模板，所以去掉了@ResponseBody
	public String userIndex(Model model,@PathVariable("userId") int userId){
		
		model.addAttribute("vos", getQuestions(userId,0,10));
		
		//添加了该ID的用户的一些信息到前端
		//比如用户名字，用户评论总数，粉丝数，关注数
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUser() != null) {
        	//当前用户是否关注了该ID用户
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
	}
	
	//获取用户的问题信息，限制了只取10条
	@RequestMapping(path = {"/","/index"},method={RequestMethod.GET})	//这里返回一个模板，所以去掉了@ResponseBody
	public String index(Model model,
			@RequestParam(value = "pop", defaultValue = "0") int pop){
		
		//model.addAttribute("questions", questionList);
		
		
		model.addAttribute("vos", getQuestions(0,0,10));
		return "index";	//指定返回的模板也就是html文件
	}
	
	//自定义的获得问题信息
	private List<ViewObject> getQuestions(int userId,int offset,int limit){
		
		List<Question> questionList=questionService.getLatestQuestions(userId, offset, limit);
		
		//或者使用ViewObject来存放东西，ViewObject是一个Map，可以存放任何对象
		List<ViewObject> vos = new ArrayList<ViewObject>();
		for(Question question:questionList){
			ViewObject vo = new ViewObject();
			vo.set("question", question);
			vo.set("user", userService.getUser(question.getUserId()));
			//“关注”功能相关，增加该问题的关注数量
			vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
			vos.add(vo);
		}
		return vos;
	}
}
