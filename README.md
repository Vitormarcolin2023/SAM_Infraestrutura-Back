# SAM - Sistema de Acompanhamento de Mentorias (Back-end)

## 📌 Proposta do Projeto

O **SAM - Sistema de Acompanhamento de Mentorias** é uma solução tecnológica desenvolvida como parte de um Projeto Integrador de Extensão do **Centro Universitário União das Américas Descomplica - UniAmérica**.

Este repositório contém a aplicação **Back-end** (API RESTful), responsável por toda a regra de negócios, persistência de dados e segurança do sistema. O objetivo principal é fornecer serviços estáveis e seguros para a gestão, validação e certificação de mentores voluntários, servindo como núcleo central para as aplicações clientes (Web/Mobile).

## 👥 Quem Desenvolveu o Projeto

Este projeto foi realizado por acadêmicos dos cursos de Engenharia de Software e Análise e Desenvolvimento de Sistemas:

* Cecília de Moura Cezar Quaresma
* Josiane Cristina Marins Steiernagel
* Samara Achterberg da Silva
* Vitor Hugo Marcolin

## ❗ Problema Apresentado

A falta de centralização e controle na gestão das mentorias de extensão gera dificuldades na rastreabilidade das atividades e na validação das horas complementares. Para resolver isso, era necessário um servidor robusto capaz de processar as regras de negócio complexas, garantir a integridade dos dados e prover segurança no acesso às informações sensíveis de alunos e mentores.

## 💡 Solução e Arquitetura (Back-end)

A solução foi construída utilizando **Java** com o framework **Spring Boot**, adotando uma arquitetura em camadas (Controller, Service, Repository) para garantir desacoplamento e manutenibilidade. A aplicação expõe uma API RESTful documentada e segura.

Principais características da implementação:

* **API RESTful:** Uso correto dos verbos HTTP (GET, POST, PUT, DELETE) e status codes para comunicação padronizada.
* **Segurança (Spring Security):** Implementação de autenticação e autorização via **JWT (JSON Web Token)**. O acesso aos endpoints é controlado com base em **Roles** (perfis de usuário), garantindo que apenas usuários autorizados acessem rotas sensíveis.
* **Persistência (Spring Data JPA):** Mapeamento Objeto-Relacional avançado, com relacionamentos entre entidades e consultas customizadas utilizando JPQL e métodos automáticos.
* **Qualidade de Código:** Cobertura de testes unitários e de integração para garantir a confiabilidade das regras de negócio.

## 🛠️ Tecnologias Utilizadas

O ecossistema tecnológico do servidor inclui:

* **Linguagem:** Java 17
* **Framework Principal:** Spring Boot
* **Segurança:** Spring Security & JWT
* **Banco de Dados:** PostgreSQL
* **ORM:** Hibernate / Spring Data JPA
* **Testes:** JUnit, Mockito e Jacoco (para relatórios de cobertura)
* **Infraestrutura/Deploy:** Amazon Web Services (AWS)

## 🚀 Como Executar o Projeto

Pré-requisitos: Java 17 e Maven instalados.

1. **Clone o repositório:**
```bash
git clone https://github.com/Vitormarcolin2023/SAM_FullStack2.git

```


2. **Configure o Banco de Dados:**
* Certifique-se de ter o PostgreSQL rodando.
* Ajuste as credenciais no arquivo `.env`.


3. **Execute a aplicação:**
```bash
mvn spring-boot:run

```


4. A API estará disponível em `http://localhost:8080`.

## 👨‍🏫 Orientadores do Projeto

**Willian Bogler da Silva**
- Mestre em Tecnologia Ambiental e docente do curso de Engenharia de Software.

**Edrian Silva**
- Especialista em Gestão em Modelos Educacionais Inovadores e docente do curso de Engenharia de Software.

## 👨‍🏫 Mentor do Projeto

**Wellington de Oliveira**
- Mestre em Tecnologias Computacionais e docente do curso de Engenharia de Software.

## ✅ Conclusões

O desenvolvimento do Back-end do SAM consolidou conhecimentos avançados em desenvolvimento web com Java. A integração do **Spring Boot** com **Spring Security** proporcionou um ambiente seguro e escalável, enquanto o uso de testes automatizados garantiu a entrega de um software de alta qualidade. A API está pronta para ser consumida pelo Front-end e preparada para futuras expansões e integrações institucionais.
