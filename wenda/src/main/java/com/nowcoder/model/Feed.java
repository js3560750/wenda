package com.nowcoder.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * 新鲜事Model
 * 
 * 新鲜事的数据存在数据库中，没用Redis
 * 
 * Created by nowcoder on 2016/8/12.
 */
public class Feed {
    private int id;	//新鲜事自身id
    private int type;	//类型
    private int userId;	//由谁产生的新鲜事
    private Date createdDate;	//时间
    private String data;	//新鲜事数据，不同类型的新鲜事数据不一样！ 这个data是一个JSON格式用来存储不同的信息
    
    //下面这个JSON对象很重要！！！因为上面的data数据是一个JSON字符串，因此我们同时建立了这个dataJSON即JSON对象
    //这个JSON对象里的内容就是data，在最下面的函数定义了
    //这样我们在前端页面就能通过velocity的$vo.id这种写法直接获得id
    private JSONObject dataJSON = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        //newJSON对象，使其内容就是data
        dataJSON = JSONObject.parseObject(data);
    }
    //额外加的get方法而不是getKey方法，因为我们无法知道Key的内容，所以用的get方法，在前端velocity语言中会帮我们自动判断get的对象是什么	
    public String get(String key) {
        return dataJSON == null ? null : dataJSON.getString(key);
    }
}
