package com.nowcoder.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.SensitiveService;
import com.nowcoder.util.WendaUtil;

@Controller
public class CommentController {

	// 日志
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

	@Autowired
	HostHolder hostHolder;

	@Autowired
	QuestionService questionService;

	@Autowired
	CommentService commentService;

	@Autowired
	SensitiveService sensitiveService;

	@Autowired
	EventProducer eventProducer;

	// 添加评论
	@RequestMapping(path = { "/addComment" }, method = { RequestMethod.POST })
	public String addComment(@RequestParam("questionId") int questionId, @RequestParam("content") String content) {
		try {
			// 对评论进行标签过滤和敏感词过滤
			content = HtmlUtils.htmlEscape(content);
			content = sensitiveService.filter(content);

			Comment comment = new Comment();
			if (hostHolder.getUser() != null) {
				// 如果用户登录了，则设置该用户ID为评论的userId
				comment.setUserId(hostHolder.getUser().getId());
			} else {
				// 如果用户没有登录，则设置该评论的userId为我们的默认ID即WendaUtil.ANONYMOUS_USERID
				comment.setUserId(WendaUtil.ANONYMOUS_USERID);
			}
			comment.setContent(content);
			comment.setCreatedDate(new Date());
			comment.setEntityType(EntityType.ENTITY_QUESTION); // 设置评论对象的类型，在Model里的EntityType定义好了的，EntityType.ENTITY_QUESTION=1
			comment.setEntityId(questionId);
			comment.setStatus(0); // status=0为评论正常显示，1则为不显示

			// 通过服务把该comment数据写入数据库
			commentService.addComment(comment);

			// 更新该问题下的评论数量，要用到questionService
			int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
			questionService.updateCommentCount(questionId, count);

			// 如果评论了，则产生一个评论事件，并且我们在事件处理器中添加了FeedHandler（新鲜事处理器）
			// 即评论了就产生一件新鲜事
			eventProducer.fireEvent(
					new EventModel(EventType.COMMENT).setActorId(comment.getUserId()).setEntityId(questionId));

		} catch (Exception e) {
			logger.error("添加评论错误" + e.getMessage());
		}

		return "redirect:/question/" + String.valueOf(questionId);
	}

}
