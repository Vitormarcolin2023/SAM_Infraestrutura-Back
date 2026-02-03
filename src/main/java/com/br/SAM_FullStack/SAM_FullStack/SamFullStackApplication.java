package com.br.SAM_FullStack.SAM_FullStack;

import io.github.cdimascio.dotenv.Dotenv; // Nova Importação
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File; // Nova Importação

@CrossOrigin
@SpringBootApplication
@EnableScheduling
public class SamFullStackApplication {

    public static void main(String[] args) {

        // --- INÍCIO: Carregamento do Arquivo .env ---

        // 1. Verifica se o arquivo .env existe na raiz do projeto.
        File envFile = new File(".env");

        if (envFile.exists()) {
            try {
                // 2. Carrega as variáveis do arquivo .env
                Dotenv dotenv = Dotenv.load();

                // 3. Itera sobre as variáveis e as define como Propriedades do Sistema.
                // O Spring Boot pode ler essas propriedades.
                dotenv.entries().forEach(entry -> {
                    System.setProperty(entry.getKey(), entry.getValue());
                });

                System.out.println("✅ Arquivo .env carregado com sucesso! Variáveis injetadas no ambiente.");

            } catch (Exception e) {
                // Trata exceções caso o arquivo .env exista mas não possa ser lido
                System.err.println("❌ Erro ao carregar o arquivo .env: " + e.getMessage());
            }

        } else {
            System.out.println("⚠️ Aviso: Arquivo .env não encontrado. O aplicativo usará apenas variáveis de ambiente do sistema ou defaults.");
        }

        // --- FIM: Carregamento do Arquivo .env ---

        // A aplicação Spring é executada após o carregamento do .env
        SpringApplication.run(SamFullStackApplication.class, args);
    }

}