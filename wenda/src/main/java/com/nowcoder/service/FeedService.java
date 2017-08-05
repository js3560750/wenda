package com.nowcoder.service;

import com.nowcoder.dao.FeedDAO;
import com.nowcoder.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 新鲜事服务
 * 
 * Created by nowcoder on 2016/8/12.
 */
@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;

    //“拉”模式，获取我关注的人的新鲜事，如果未登录，则userIds=null，获取所有人的新鲜事（在sql语句中体现)
    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count) {
        return feedDAO.selectUserFeeds(maxId, userIds, count);
    }

    //插入新鲜事到数据库
    public boolean addFeed(Feed feed) {
        feedDAO.addFeed(feed);
        return feed.getId() > 0;
    }

    //“推”模式，根据新鲜事ID获取新鲜事
    public Feed getById(int id) {
        return feedDAO.getFeedById(id);
    }
}
