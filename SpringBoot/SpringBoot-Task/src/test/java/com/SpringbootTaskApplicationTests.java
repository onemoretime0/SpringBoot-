package com;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@SpringBootTest
class SpringbootTaskApplicationTests {

    @Autowired
    JavaMailSenderImpl mailSender;

    @Test
    void simpleMail() {

        //一个简单的邮件
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setSubject("Hello");//主题
        mailMessage.setText("这是在SpringBoot中使用JavaMailSenderImpl对象发送的邮件");//内容
        mailMessage.setTo("709779916@qq.com");
        mailMessage.setFrom("709779916@qq.com");

        mailSender.send(mailMessage);
    }

    @Test
    void complexMail() throws MessagingException {

        //一个复杂邮件,MimeMessage复杂邮件
        MimeMessage message = mailSender.createMimeMessage();
        //组装邮件
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,true,"utf-8");
        //利用MimeMessageHelper对象进行组装
        mimeMessageHelper.setSubject("Hello,complexMail");
        mimeMessageHelper.setText("<h1>复杂邮件可以写HTML</h1>+" +
                "<hr>+" +
                "<p style='color:red'>这是在SpringBoot中使用JavaMailSenderImpl对象发送的复杂邮件</p>",true);
        //附件
        mimeMessageHelper.addAttachment("加藤惠.png",new File("F:\\OneDrive\\壁纸\\路人女主\\加藤惠.png"));
        mimeMessageHelper.addAttachment("雪乃.png",new File("F:\\OneDrive\\壁纸\\春物\\雪乃.png"));

        mimeMessageHelper.setTo("709779916@qq.com");
        mimeMessageHelper.setFrom("709779916@qq.com");

        mailSender.send(message);
    }


}
