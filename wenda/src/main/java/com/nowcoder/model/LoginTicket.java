package com.nowcoder.model;

import java.util.Date;

/**
 * 登录注册的T票，如果登录或者注册成功则有这个T票，否则没有。因此验证这个T票能知道是否登录成功。
 * 注册成功后自动登录
 */
public class LoginTicket {
    private int id;
    private int userId;
    private Date expired;	//过期时间
    private int status;		// 0有效，1无效
    private String ticket;

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
