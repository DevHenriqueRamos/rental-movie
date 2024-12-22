# Movie Rental

**Descrição do Projeto**  
O **Movie Rental** é uma aplicação completa para aluguel de filmes, projetada para demonstrar minhas habilidades como desenvolvedor back-end. O sistema oferece funcionalidades como cadastro de filmes, usuários, pedidos de aluguel, pagamentos, notificações e muito mais. O projeto integra microserviços, utiliza bases de dados distribuídas e adota boas práticas de arquitetura e desenvolvimento.

---

## Índice

1. [Sobre o Projeto](#sobre-o-projeto)
2. [Tecnologias Utilizadas](#tecnologias-utilizadas)
3. [Funcionalidades](#funcionalidades)
4. [Experiência](#experiência)

---

## Sobre o Projeto

O **Movie Rental** foi desenvolvido para gerenciar o processo de aluguel de filmes, desde o cadastro de filmes até o processo de pagamento. O sistema é dividido em microserviços que se comunicam entre si, utilizando uma arquitetura de **event-driven** para garantir a escalabilidade e a independência entre os serviços.

Os principais recursos incluem:
- Cadastro e gerenciamento de filmes, categorias, estúdio de produção e usuários.
- Registro de pedidos de aluguel, incluindo data de início e fim.
- Integração com um serviço de pagamento e envio de notificações.
- Funcionalidades de segurança, como autenticação e autorização com JWT.
- Uso de um banco de dados SQL(PostgreSQL) e NOSQL(MongoDB).

Este projeto foi criado para demonstrar minha capacidade de trabalhar com microserviços, API RESTful, integração de sistemas e testes automatizados.

---

## Tecnologias Utilizadas

- **Java**: A linguagem principal para o desenvolvimento da aplicação.
- **Spring Boot**: Framework para desenvolvimento de microserviços e APIs.
- **Spring Data MongoDB**: Para persistência de dados dos pedidos de aluguel.
- **Spring Data JPA**: Para persistência de dados dos usuários, filmes, aluguel, notificações e afins.
- **Spring Security & JWT**: Para autenticação e autorização de usuários.
- **Stripe**: Serviço de pagamento para realizar transações de aluguel de filmes.
- **JUnit & Mockito**: Framework de testes unitários e de integração.
- **Docker**: Para dockerizar a aplicação e facilitar a execução em diferentes ambientes.
- **RabbitMQ**: Sistema de mensageria para comunicação entre os microserviços.
- **Resilience4j**: Circuit breaker para trazer resiliência na comunicação entre serviços de filmes e aluguel.

---

## Funcionalidades

- **Cadastro de Filmes**: Permite a inserção de filmes com informações como título, sinopse, gênero, etc.
- **Cadastro de Usuários**: Gerencia o registro de novos usuários e autenticação com JWT.
- **Pedidos de Aluguel**: Os usuários podem alugar filmes e gerenciar seu período de locação.
- **Pagamentos**: Integração com a API do Stripe para realizar pagamentos seguros.
- **Notificações**: Envio de notificações sobre status do pedido, aproximação e expiração dos filmes alugados.
- **Busca de Filmes**: Funcionalidade para buscar filmes por nome, gênero ou outros critérios.
- **Administração**: Interface para administradores gerenciarem filmes, usuários e pedidos.

---

## Experiência

O **Movie Rental** foi um projeto que teve seus altos e baixos, mas, sem dúvidas, me trouxe muito conhecimento. Ter que lidar com várias aplicações rodando ao mesmo tempo e se comunicando entre si me ensinou lições que seriam difíceis de aprender com algo mais simples. Criar esse projeto do zero e mantê-lo até o fim me mostrou a importância de um código limpo e bem estruturado, da segurança da aplicação, da comunicação por eventos, do tratamento de exceções, entre outros aspectos. Mas, principalmente, o que mais levo desse projeto é a certeza de que estou pronto para assumir um desafio ainda maior.

---
