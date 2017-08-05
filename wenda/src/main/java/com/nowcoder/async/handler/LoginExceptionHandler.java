package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录异常则发送邮件事件处理器
 * 
 * 发邮件是一个耗时挺长的操作，要连邮件服务器嘛，所以肯定不能放在同步里来做，一定要放在异步来做
 * 
 * 事件处理器一定要继承EventHandler，这样EventConsumer才能发现这个事件处理器
 * 这里一定要添加为组件！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！不添加组件，EventConsumer初始化时就搜索不到该LikeHandle
 * Created by nowcoder on 2016/7/30.
 */
@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    MailSender mailSender;

    //设置事件处理器的处理逻辑
    @Override
    public void doHandle(EventModel model) {
        // xxxx判断发现这个用户登陆异常，这里没有写登录异常的逻辑判断，只是为了测试功能，假设已经登录异常了，直接进行发邮件的操作
        Map<String, Object> map = new HashMap<String, Object>();
        //login_exception.html静态模板文件里有$username，所以这里要把$username变量传进模板文件里
        map.put("username", model.getExts("username"));
        //利用com.nowcoder.util里的MailSender发送邮件
        //mails/login_exception.html是邮件正文内容模板，放在静态文件里的templates/mails里
        mailSender.sendWithHTMLTemplate(model.getExts("email"), "用户登录提示", "mails/login_exception.html", map);
    }

    /**
     * 设置该事件处理器关注的事件类型，这里设置的是LOGIN事件
     */
    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
