package com.br.SAM_FullStack.SAM_FullStack.service;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    JavaMailSender javaMailSender;
    @Mock
    SpringTemplateEngine templateEngine;

    @InjectMocks
    EmailService emailService;

    final String remetente = "sam@email.com";
    final String destinatario = "mentor@email.com";
    final String assunto = "Teste";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "remetente", remetente);
    }

    @Test
    @DisplayName("Deve enviar email de texto com sucesso")
    void enviarEmailTexto_quandoSucesso_deveEnviarEmailERetornarMensagem() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        String resultado = emailService.enviarEmailTexto(destinatario, assunto, "Olá");

        assertEquals("Email Enviado", resultado);
        verify(javaMailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals(remetente, message.getFrom());
        assertArrayEquals(new String[]{destinatario}, message.getTo());
        assertEquals(assunto, message.getSubject());
        assertEquals("Olá", message.getText());
    }

    @Test
    @DisplayName("Deve retornar mensagem de erro ao falhar envio de email texto")
    void enviarEmailTexto_quandoFalha_deveRetornarMensagemDeErro() {
        doThrow(new MailSendException("Erro de envio simulado")).when(javaMailSender).send(any(SimpleMailMessage.class));

        String resultado = emailService.enviarEmailTexto(destinatario, assunto, "Olá");

        assertEquals("Erro ao tentar enviar", resultado);
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve enviar email com template com sucesso")
    void enviarEmailComTemplate_quandoSucesso_deveProcessarTemplateEEnviar() throws MessagingException {
        String templatePath = "email/template";
        Map<String, Object> variaveis = Collections.singletonMap("nome", "Aluno");
        String corpoHtml = "<html><body>Olá Aluno</body></html>";
        MimeMessage mimeMessage = new MimeMessage((Session) null);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(corpoHtml);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);

        emailService.enviarEmailComTemplate(destinatario, assunto, templatePath, variaveis);

        verify(templateEngine).process(eq(templatePath), any(Context.class));
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(captor.capture());

        MimeMessage messageEnviada = captor.getValue();
        assertEquals(remetente, messageEnviada.getFrom()[0].toString());
        assertEquals(destinatario, messageEnviada.getAllRecipients()[0].toString());
        assertEquals(assunto, messageEnviada.getSubject());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException original se falhar ao processar template")
    void enviarEmailComTemplate_quandoFalhaNoTemplate_deveLancarRuntimeExceptionOriginal() {
        // (Teste OK)
        String templatePath = "path/invalido";
        Map<String, Object> variaveis = Collections.emptyMap();
        String errorMsg = "Erro de Template Simulado";

        when(templateEngine.process(eq(templatePath), any(Context.class)))
                .thenThrow(new RuntimeException(errorMsg));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.enviarEmailComTemplate(destinatario, assunto, templatePath, variaveis);
        });

        assertEquals(errorMsg, exception.getMessage());

        verify(javaMailSender, never()).createMimeMessage();
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException ao falhar ao configurar o helper")
    void enviarEmailComTemplate_quandoFalhaNaConfiguracaoDoHelper_deveLancarRuntimeException() throws MessagingException {
        String templatePath = "email/template";
        Map<String, Object> variaveis = Collections.singletonMap("nome", "Mentor");
        String corpoHtml = "<html><body>Olá Mentor</body></html>";
        String errorMsg = "Erro simulado no helper.setFrom()";

        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(corpoHtml);

        doThrow(new MessagingException(errorMsg))
                .when(mimeMessage)
                .setFrom(any(jakarta.mail.Address.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.enviarEmailComTemplate(destinatario, assunto, templatePath, variaveis);
        });

        assertEquals("Erro ao enviar e-mail com template: " + errorMsg, exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MessagingException);

        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }
}