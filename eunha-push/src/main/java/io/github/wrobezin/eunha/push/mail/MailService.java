package io.github.wrobezin.eunha.push.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/19 20:45
 */
@Service
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;

    private static final String FROM = "eunha_push@126.com";

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(FROM);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            mailSender.send(message);
            log.info("发送邮件到{}", to);
        } catch (MessagingException e) {
            log.error("发送邮件出错", e);
        }
    }
}
