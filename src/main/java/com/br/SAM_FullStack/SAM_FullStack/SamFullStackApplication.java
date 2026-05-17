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

    // O TOMCAT CHAMA ISSO:
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        carregarDotenv();
        return application.sources(SamFullStackApplication.class);
    }

    // O SEU PC (JAR) CHAMA ISSO:
    public static void main(String[] args) {
        carregarDotenv();
        SpringApplication.run(SamFullStackApplication.class, args);
    }

    private static void carregarDotenv() {
        // Procure o arquivo no diretório de trabalho do Tomcat (/opt/tomcat)
        File envFile = new File("/opt/tomcat/webapps/sam_dir/.env");

        if (envFile.exists()) {
            try {
                Dotenv dotenv = Dotenv.configure()
                        .directory("/opt/tomcat/webapps/sam_dir")
                        .load();
                dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
                System.out.println("✅ Variáveis injetadas com sucesso!");
            } catch (Exception e) {
                System.err.println("❌ Erro ao carregar .env: " + e.getMessage());
            }
        }
    }
}