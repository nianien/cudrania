package com.cudrania.core.utils;

import com.cudrania.core.exception.ExceptionChecker;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 发送邮件的工具类
 *
 * @author skyfalling
 */
public class EmailSender {
    /**
     * 用户名
     */
    private String user;
    /**
     * 密码
     */
    private String password;
    /**
     * 服务器地址
     */
    private String host;
    /**
     * 端口号
     */
    private int port = 25;
    /**
     * 发件人
     */
    private String from;
    /**
     * 收件人列表,以逗号分割
     */
    private String to;
    /**
     * 抄送列表,以逗号分割
     */
    private String cc;
    /**
     * 暗送列表,以逗号分割
     */
    private String bcc;
    /**
     * 标题
     */
    private String subject;
    /**
     * 正文内容
     */
    private String content;
    /**
     * 正文类型
     */
    private String contentType = "text/plain;charset=utf-8;";
    /**
     * 附件列表
     */
    private List<File> attachments = new ArrayList<File>();

    /**
     * 默认构造方法
     */
    public EmailSender() {
    }

    /**
     * 构造方法
     *
     * @param user     用户名,含@后缀
     * @param password 密码,如不需密码可置为null
     */
    public EmailSender(String user, String password) {
        this(user, password, null);
    }

    /**
     * 构造方法
     *
     * @param user     用户名,含@后缀
     * @param password 密码,如不需密码可置为null
     * @param host     邮件服务器地址,如 smtp.xxx.com
     */
    public EmailSender(String user, String password, String host) {
        this(user, password, host, 25);
    }

    /**
     * 构造方法
     *
     * @param user     用户名,含@后缀
     * @param password 密码,如不需密码可置为null
     * @param host     邮件服务器地址,如 smtp.xxx.com
     * @param port     服务器端口号,默认25
     */
    public EmailSender(String user, String password, String host, int port) {
        this.user = user;
        this.from = user.substring(0, user.indexOf('@')) + "<" + user + ">";
        this.password = password;
        this.host = host != null ? host : "smtp." + user.substring(user.indexOf('@') + 1);
        this.port = port > 0 ? port : 25;
    }


    /**
     * 发件人
     *
     * @param from
     * @return
     */
    public EmailSender from(String from) {
        this.from = from;
        return this;
    }


    /**
     * 发送列表,逗号","分隔
     *
     * @param to
     * @return
     */
    public EmailSender to(String to) {
        this.to = to;
        return this;
    }

    /**
     * 抄送列表,逗号","分隔
     *
     * @param cc
     * @return
     */
    public EmailSender cc(String cc) {
        this.cc = cc;
        return this;
    }

    /**
     * 暗送列表,逗号","分隔
     *
     * @param bcc
     * @return
     */
    public EmailSender bcc(String bcc) {
        this.bcc = bcc;
        return this;
    }


    /**
     * 主题
     *
     * @param subject
     * @return
     */
    public EmailSender subject(String subject) {
        this.subject = subject;
        return this;
    }


    /**
     * 正文内容
     *
     * @param content
     * @return
     */
    public EmailSender content(String content) {
        this.content = content;
        return this;
    }

    /**
     * 正文内容
     *
     * @param content
     * @return
     */
    public EmailSender content(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
        return this;
    }

    /**
     * 添加附件
     *
     * @param attachment
     * @return
     */
    public EmailSender addAttachment(File attachment) {
        this.attachments.add(attachment);
        return this;
    }

    /**
     * 清空附件
     *
     * @return
     */
    public EmailSender clearAttachments() {
        this.attachments.clear();
        return this;
    }

    /**
     * 发送邮件
     */
    public void send() {
        Session session = createSession(user, password, host, port);
        try {
            // 创建邮件
            MimeMessage message = new MimeMessage(session);
            // 设置发件人地址
            message.setFrom(new InternetAddress(from));
            // 设置收件人地址（多个邮件地址）
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            if (StringUtils.isNotBlank(cc)) {
                message.addRecipients(RecipientType.CC, InternetAddress.parse(cc));
            }
            if (StringUtils.isNotBlank(bcc)) {
                message.addRecipients(RecipientType.BCC, InternetAddress.parse(bcc));
            }
            // 设置邮件主题
            message.setSubject(subject);
            // 设置发送时间
            message.setSentDate(new Date());
            // 设置发送内容
            Multipart multipart = new MimeMultipart();
            MimeBodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(content, contentType);
            multipart.addBodyPart(contentPart);
            //设置附件
            for (File attachment : attachments) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                FileDataSource source = new FileDataSource(attachment);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
//                attachmentPart.setFileName(MimeUtility.encodeWord(attachment.getName(), "GBK", null));
                multipart.addBodyPart(attachmentPart);
            }
            message.setContent(multipart);
            Transport.send(message);
        } catch (Exception e) {
            ExceptionChecker.throwException(e);
        }
    }


    /**
     * 创建邮件会话
     *
     * @param user
     * @param password
     * @param host
     * @param port
     * @return
     */
    protected Session createSession(final String user, final String password, String host, int port) {
        boolean needAuth = StringUtils.isNotBlank(password);
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.sendpartial", true);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", needAuth);
        props.put("mail.transport.protocol", "smtp");
        Authenticator authenticator = needAuth ? new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        } : null;
        return Session.getDefaultInstance(props, authenticator);
    }


}