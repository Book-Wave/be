package com.test.demo.service.member;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${spring.mail.username}") private String sender;

    private final JavaMailSender javaMailSender;
    private final Map<String, Integer> verification_code = new HashMap<>();

    public static int create_code() {
        return (int) (Math.random() * (90000)) + 100000;
    }

    public MimeMessage create_email(String email) {
        int code = create_code();
        verification_code.put(email, code);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            mimeMessage.setFrom(sender);
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, email);
            mimeMessage.setSubject("이메일 인증");
            String body = String.format(
                    "<h3>요청하신 인증 번호입니다.</h3><h1>%d</h1><h3>감사합니다.</h3>",
                    code
            );
            mimeMessage.setText(body, "UTF-8", "html");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mimeMessage;
    }

    public void send_email(String email) {
        MimeMessage mimeMessage = create_email(email);
        javaMailSender.send(mimeMessage);
    }

    public boolean verify_code(String email, int code) {
        Integer stored = verification_code.get(email);
        return stored != null && stored == code;
    }
}
