package com.br.SAM_FullStack.SAM_FullStack;

import io.github.cdimascio.dotenv.Dotenv; // Nova Importação
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File; // Nova Importação

@CrossOrigin
@SpringBootApplication
@EnableScheduling
public class SamFullStackApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SamFullStackApplication.class);
    }

    public static void main(String[] args) {

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


        SpringApplication.run(SamFullStackApplication.class, args);
    }

}