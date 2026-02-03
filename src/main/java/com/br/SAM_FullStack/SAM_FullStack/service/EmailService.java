package com.br.SAM_FullStack.SAM_FullStack.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String remetente;

    public String enviarEmailTexto(String destinatario, String assunto, String mensagem){
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(remetente);
            simpleMailMessage.setTo(destinatario);
            simpleMailMessage.setSubject(assunto);
            simpleMailMessage.setText(mensagem);
            javaMailSender.send(simpleMailMessage);
            return "Email Enviado";
        }catch (Exception ex){
            return "Erro ao tentar enviar";
        }
    }

    public void enviarEmailComTemplate(String destinatario, String assunto, String templatePath, Map<String, Object> variaveis) {
        try {
            Context context = new Context();
            context.setVariables(variaveis);
            String corpoHtml = templateEngine.process(templatePath, context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {

            throw new RuntimeException("Erro ao enviar e-mail com template: " + e.getMessage(), e);
        }
    }
}