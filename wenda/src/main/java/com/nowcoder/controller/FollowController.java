package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;

/**
 * 关注和被关注控制器，用的Redis作为数据库
 * @author 18894
 *
 */
@Controller
public class FollowController {
    @Autowired
    FollowService followService;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;
    
    //关注用户
    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
    	//判断用户登录没有，否则返回999，我们自定义999代码为未登录
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        //进行关注用户的操作，即成为粉丝
        //followService用到了Redis
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        //把关注事件发出去，利用异步的事件处理，比如我关注了你，则你会收到提醒
        //异步的关注事件处理器名叫FollowHandler
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

        // 成功返回0，失败返回1，并返回关注的人数
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }
    
    //取消关注用户
    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId) {
    	//判断用户登录没有，否则返回999，我们自定义999代码为未登录
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        //进行取消关注用户的操作
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

      //把取消关注事件发出去，利用异步的事件处理，比如我取消关注了你，则你会收到提醒
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(userId)
                .setEntityType(EntityType.ENTITY_USER).setEntityOwnerId(userId));

        // 成功返回0，失败返回1，并返回关注的人数
        return WendaUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }
    
    //关注问题
    @RequestMapping(path = {"/followQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
    	//判断用户登录没有，否则返回999，我们自定义999代码为未登录
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        //判断问题是否存在
        Question q = questionService.getById(questionId);
        if (q == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        //进行关注问题的操作
        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        //把关注问题事件发出去，利用异步的事件处理，比如我取消关注了你，则你会收到提醒
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

        //关注问题之后，页面会显示一些信息，比如关注人的头像、id之类的，因此要把这些信息返回到前端
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        //获取最新的关注总数
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    //取消关注问题
    @RequestMapping(path = {"/unfollowQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
    	//判断用户登录没有，否则返回999，我们自定义999代码为未登录
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        //判断问题是否存在
        Question q = questionService.getById(questionId);
        if (q == null) {
            return WendaUtil.getJSONString(1, "问题不存在");
        }

        //进行取消关注问题的操作
        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        //把取消关注问题事件发出去，利用异步的事件处理，比如我取消关注了你，则你会收到提醒
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityId(questionId)
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(q.getUserId()));

        //关注问题之后，页面会显示一些信息，比如关注人的头像、id之类的，因此要把这些信息返回到前端
        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        //获取最新的关注总数
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }
    
    //获取我（当前用户）所有的粉丝，即所有关注我的人
    //和下面的followees函数逻辑一样的
    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    //获取我（当前用户）关注的所有人
    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);

        //判断用户是否登录
        if (hostHolder.getUser() != null) {
        	//如果用户登录了
        	//则通过自定义的pirvate getUsersInfo函数获取用户信息装进model里然后传给前端
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
        	//如果用户未登录
        	//则getUsersInfo第一个参数传0，用户信息还是照常获取，但是第一个参数为0，则followed这个属性即是否关注这个属性为false
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        //获取我关注的总数
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        //curUser是当前用户
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }
    
    //公共函数，用来根据List集合（该集合里装所有用户的ID）获取该集合里所有用户的详细信息
    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<ViewObject>();
        for (Integer uid : userIds) {
        	//首先判断这个user是否存在
            User user = userService.getUser(uid);
            if (user == null) {
            	//如果这个user不存在，则忽略掉
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            //获取该用户总的评论数
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            //获取该用户粉丝的数量
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            //获取该用户关注的数量
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            //如果localUserId==0，说明我没有登录
            if (localUserId != 0) {
            	//如果登录了，则判断我是否是这个问题的关注者
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
            	//如果没登录，则followed属性值为false，既不是该问题的关注者
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }
}
