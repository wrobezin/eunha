package io.github.wrobezin.eunha.push.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/19 20:45
 */
@Service
public class MailService {
    private final JavaMailSender mailSender;

    private static final String FROM = "eunha_push@126.com";

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
