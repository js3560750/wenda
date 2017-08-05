package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.SensitiveService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;

@Controller
public class QuestionController {
	
	private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
	
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	LikeService likeService;
	
	@Autowired
	FollowService followService;
	

	/**
	 * 在首页点进具体的问题，注意参数@PathVariable("qid")，不是@RequestParam
	 */
	@RequestMapping(value="/question/{qid}",method={RequestMethod.GET})
	public String questionDetail(Model model,@PathVariable("qid") int qid){
		//问题、提问的相关数据
		Question question=questionService.getById(qid);
		model.addAttribute("question", question);
		//评论的相关数据,EntityType.ENTITY_QUESTION表示这些评论是针对问题的评论，不是针对评论的评论
		List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
		//comments是在写“关注”功能时添加的，用来替换掉vos,就是换了个名字。。。。。
		List<ViewObject> comments = new ArrayList<ViewObject>();		
		//List<ViewObject> vos = new ArrayList<>();	////ViewObject不是视图，是传递对象跟velocity中间的一个对象
		for(Comment comment:commentList){
			ViewObject vo = new ViewObject();
			vo.set("comment", comment);
			
			//增加该用户是否对该评论点过赞
			//如果该用户给该评论点了赞，则liked=1
			//如果点了“踩”，则liked=-1
			//如果没点赞也没点踩或者用户未登录，则liked=0
			if(hostHolder.getUser()==null){
				vo.set("liked", 0);
			}else{
				vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), comment.getEntityType(), comment.getEntityId(),comment.getId()));
			}
			
			//增加该评论的点赞数
			vo.set("likeCount", likeService.getLikeCount(comment.getEntityType(), comment.getEntityId(),comment.getId()));
			
			vo.set("user", userService.getUser(comment.getUserId()));	//把做出该评论的用户信息也传入ViewObject
			comments.add(vo);
		}
		model.addAttribute("comments", comments);	//这样前端通过foreach遍历comments对象从而获得评论相关内容
		
		//下面这一部分全部是“关注”功能
		List<ViewObject> followUsers = new ArrayList<ViewObject>();
        // 获取关注的用户信息
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }
		
		return "detail";
	}
	
	
	/**
	 * 首页头部导航栏，用户点击“提问”并“发布问题”所进行的操作
	 */
	@RequestMapping(value="/question/add",method={RequestMethod.POST})
	@ResponseBody
	public String addQuestion(@RequestParam("title") String title,
							  @RequestParam("content") String content){
		
		try {
			//获取用户输入并存入question对象
			Question question= new Question();
			question.setContent(content);
			question.setCreatedDate(new Date());
			question.setTitle(title);
			
			if(hostHolder.getUser()==null){	
				//如果用户未登录发表问题，则给一个默认的USERID
				question.setUserId(WendaUtil.ANONYMOUS_USERID);	//WendaUtil.ANONYMOUS_USERID=3
			}else{
				//如果用户登录了，则从hostHolder中提取出用户登录的ID赋给question
				question.setUserId(hostHolder.getUser().getId());
			}
			
			//通过questionService把question存入数据库
			if(questionService.addQuestion(question)>0){
				//返回JSON格式字符串给前端，这里返回code=0,0为成功
				return WendaUtil.getJSONString(0);
			}
		} catch (Exception e) {
			logger.error("增加问题失败"+e.getMessage());
		}
		
		//如果上面try里没有返回，则说明失败了，返回JSON字符串给前端，code=1,非0表示失败
		return WendaUtil.getJSONString(1,"失败");
	}

}
