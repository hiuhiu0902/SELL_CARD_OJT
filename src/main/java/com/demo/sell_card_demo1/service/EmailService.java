package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.dto.EmailDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@Transactional
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(EmailDetail details) {
        try {
            Context context = new Context();
            context.setVariable("name", details.getRecipient());
            context.setVariable("button", "Welcome to our system");
            context.setVariable("link", details.getLink());

            String html = templateEngine.process("emailtemplate", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

            mimeMessageHelper.setFrom("admin2601@gmail.com");
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject(details.getSubject());

            javaMailSender.send(message);
            logger.info("Email sent successfully to {}", details.getRecipient());

        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", details.getRecipient(), e.getMessage());
            e.printStackTrace();
        }
    }
}