# API de Marcação de Reuniões

![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)
![Version](https://img.shields.io/badge/Version-1.0-blue.svg)
![License](https://img.shields.io/badge/License-MIT-orange.svg)

Este projeto é uma API RESTful para gerenciamento e marcação de salas de reuniões.

## Funcionalidades

- Cadastro e listagem de salas de reuniões.
- Agendamento e consulta de reuniões em salas específicas.
- Busca de horários disponíveis para agendamento.

## Tecnologias Utilizadas

- Java
- Spring Boot
- Spring Data JPA
- Springfox Swagger para documentação da API
- PostgreSQL (ou outro banco de dados relacional)
- Lombok para redução de código boilerplate
- Maven para gestão de dependências
- Docker (opcional para contêineres)
- Git para controle de versionamento

## Estrutura do Projeto

- **`com.api.agendamentodesalas.config`**: Configuração do Swagger para documentação da API.
- **`com.api.agendamentodesalas.converter`**: Classes de conversão de entidades em objetos de resposta (DTOs).
- **`com.api.agendamentodesalas.entity`**: Entidades do banco de dados.
- **`com.api.agendamentodesalas.repository`**: Interfaces de repositório para interação com o banco de dados.
- **`com.api.agendamentodesalas.rest`**: Classes relacionadas ao controle das requisições REST.

## Como Executar

1. Clone o repositório: `git clone https://github.com/seu-usuario/api-marcacao-reunioes.git`
2. Importe o projeto em sua IDE.
3. Configure as dependências e o banco de dados (se necessário).
4. Execute a aplicação.

### Documentação da API

A documentação da API está disponível no servidor padrão de máquina em [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

## Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para abrir um pull request ou uma issue.

## Licença

Este projeto está licenciado sob a [Licença MIT](https://opensource.org/licenses/MIT).
