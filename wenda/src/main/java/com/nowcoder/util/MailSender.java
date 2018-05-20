package com.nowcoder.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

/**
 * 邮件发送服务
 * 
 * Created by nowcoder on 2016/7/15. // course@nowcoder.com NKnk66
 */
@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    
    /**
     * 这里面实现邮件的具体内容
     * @param to	收件人
     * @param subject	邮件标题
     * @param template	邮件内容用的静态模板
     * @param model	静态模板里的要用的参数
     * @return
     */
    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model) {
        try {
        	//nick为自己的昵称
            String nick = MimeUtility.encodeText("金漂亮");
            //from为发件人地址
            InternetAddress from = new InternetAddress(nick + "<188949420@qq.com>");
            //创建邮件的正文对象
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            //这个Helper是用来帮助设置正文内容的
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            //用Velocity的渲染引擎，渲染template模板作为result（邮件文本）
            String result = VelocityEngineUtils
                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
            //设置收件人
            mimeMessageHelper.setTo(to);
            //设置发件人
            mimeMessageHelper.setFrom(from);
            //设置邮件主题
            mimeMessageHelper.setSubject(subject);
            //设置文本
            mimeMessageHelper.setText(result, true);
            //发送邮件
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

    /**
     * 这里写邮件的一些初始化设置，比如账号、密码之类的
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("");//QQ账号
        mailSender.setPassword("");//这里写QQ给的IMAP/SMTP服务授权码，不要写自己的QQ密码！！！！！
        mailSender.setHost("smtp.qq.com");	//发送邮件服务器
        //mailSender.setHost("smtp.qq.com");
        //下面的都不用改
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        //javaMailProperties.put("mail.smtp.auth", true);
        //javaMailProperties.put("mail.smtp.starttls.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
