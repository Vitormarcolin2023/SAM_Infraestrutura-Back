package com.br.SAM_FullStack.SAM_FullStack;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File;

@CrossOrigin
@SpringBootApplication
@EnableScheduling
public class SamFullStackApplication extends SpringBootServletInitializer {

    // 1. Extraímos a lógica do .env para um método que pode ser chamado por ambos
    private static void carregarVariaveisDeAmbiente() {
        File envFile = new File(".env");

        if (envFile.exists()) {
            try {
                Dotenv dotenv = Dotenv.load();
                dotenv.entries().forEach(entry -> {
                    System.setProperty(entry.getKey(), entry.getValue());
                });
                System.out.println("Arquivo .env carregado com sucesso! Variáveis injetadas no ambiente.");
            } catch (Exception e) {
                System.err.println("Erro ao carregar o arquivo .env: " + e.getMessage());
            }
        } else {
            System.out.println("Aviso: Arquivo .env não encontrado. O aplicativo usará apenas variáveis de ambiente do sistema ou defaults.");
        }
    }

    // 2. ESTE É O MÉTODO QUE O TOMCAT DA VM VAI CHAMAR
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        carregarVariaveisDeAmbiente(); // Carrega o .env quando rodar na VM
        return application.sources(SamFullStackApplication.class);
    }

    // 3. Este é o método que o seu computador (localhost) vai continuar chamando
    public static void main(String[] args) {
        carregarVariaveisDeAmbiente(); // Carrega o .env no seu ambiente local
        SpringApplication.run(SamFullStackApplication.class, args);
    }
}