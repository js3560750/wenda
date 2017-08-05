package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 新鲜事
 * 
 * Created by nowcoder on 2016/7/2.
 */
@Mapper
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    //插入新鲜事
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{data},#{createdDate},#{type})"})
    int addFeed(Feed feed);

    //“推”模式
    //根据新鲜事ID获取新鲜事
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed getFeedById(int id);

    /**
     * “拉”模式
     * 获取我关注用户的所有新鲜事，具体定义写在src/main/resources/com/nowcoder/dao/FeedDAO.xml中
     * 如果是登录用户，则userIds有值，则获取我关注用户的新鲜事
     * 如果是未登录用户，则userIds为null，则获取所有用户的新鲜事
     * 
     * @param maxId   maxId是一个增量的概念，比如我翻新鲜事，往下翻了很多条，然后换页，那么换页之后的新鲜事id肯定要小于这个maxId
     * @param userIds	我关注的所有用户的id集合
     * @param count	分页相关，限制每次查询数目
     * @return
     */
    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);
}
