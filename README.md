# Integração HubSpot - MeeTime

## Visão Geral

Este projeto é uma aplicação Spring Boot projetada para integrar com o CRM HubSpot usando a API HubSpot. Ele implementa a autenticação OAuth 2.0 e fornece endpoints para criar contatos e webhook.

## Arquitetura

A aplicação segue uma arquitetura em camadas:

- **Camada de Config**: Contém classes de configuração para Spring, OAuth e configurações do HubSpot.
- **Camada de Controller**: Expõe endpoints REST para interagir com a API HubSpot e manipular webhooks.
- **Camada de Service**: Contém a lógica de negócios para autenticação OAuth, criação de contatos e processamento de webhooks.
- **Camada de Model**: Define os objetos de transferência de dados (DTOs), as classes de entidade e o mapper para converter entidade para DTO, e DTO para entidade.
- **Camada de Repository**: Fornece acesso a dados usando Spring Data JPA.
- **Camada de Shared**: Contém classes de constantes.
- **Camada de Util**: Contém classes de utilitários para geração e validação de assinatura.

## Tecnologias Utilizadas

- **Java**: 21
- **Spring Boot 3.2.3**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security (OAuth2 Client)**
- **Lombok**
- **H2 Database**
- **Mapstruct**
- **Jackson Databind**

## Pré-requisitos

Antes de executar esta aplicação, certifique-se de ter o seguinte instalado:

- Java Development Kit (JDK) versão 21
- Maven
- Uma conta de desenvolvedor HubSpot
- Um aplicativo OAuth 2.0 configurado no HubSpot

## Instruções de Configuração

### 1. Clone o Repositório

```bash
git clone https://github.com/RafaelGabriel325/integra-o-hubspot
cd integra-o-hubspot
```

### 2. Configure o Aplicativo HubSpot

- Crie uma conta de desenvolvedor no HubSpot em [developers.hubspot.com](https://developers.hubspot.com/).
- Crie um aplicativo e configure as configurações do OAuth 2.0.
- Anote o `client_id`, `client_secret` e `redirect_uri`.

### 3. Configuração da Aplicação

Modifique o arquivo `application.properties` com as seguintes propriedades:

```properties
spring.security.oauth2.client.registration.hubspot.client-id=SEU_CLIENT_ID
spring.security.oauth2.client.registration.hubspot.client-secret=SEU_CLIENT_SECRET
spring.security.oauth2.client.registration.hubspot.redirect-uri=http://localhost:8080/api/oauth/callback
spring.security.oauth2.client.registration.hubspot.scope=crm.objects.contacts.write%20oauth%20crm.objects.contacts.read
```

Substitua `SEU_CLIENT_ID` e `SEU_CLIENT_SECRET` pelas suas credenciais reais do aplicativo HubSpot.

### 4. Execute a Aplicação

```bash
mvn clean install
mvn spring-boot:run
```

## Endpoints

### 1. Obter URL de Autorização

**GET** `/api/oauth/authorize`

- Retorna a URL para iniciar o fluxo OAuth com o HubSpot.
- Clique na URL e autorize a aplicação.
- Após entrar no usuário escolhido ele chamará automaticamente o callback.

### 2. Processar Callback OAuth

**GET** `/api/oauth/callback`

- Após chamada do callback a partir do authorize.
- Recebe o código de autorização do HubSpot e troca por um token de acesso.

### 3. Criar um Contato

**POST** `/api/contacts`

**Exemplo de Request Body:**

```json
{
  "properties": {
    "email": "teste123@hubspot.com",
    "firstname": "teste123",
    "lastname": "teste123",
    "phone": "(555) 9999-9999",
    "company": "HubSpot",
    "website": "hubspot.com",
    "lifecyclestage": "marketingqualifiedlead"
  }
}
```

- Com o token pego a partir do callback.
- Cria um novo contato no HubSpot.

### 4. Webhook de Criação de Contato

**POST** `/api/webhooks/contact-creation`

**Exemplo de Request Body:**

```json
{
  "subscriptionType": "contact.creation",
  "objectId": "12345",
  "properties": {
    "email": { "value": "test@example.com" },
    "firstname": { "value": "Teste" },
    "lastname": { "value": "User" }
  }
}
```

**Headers:**

```text
X-HubSpot-Signature: 4xTDBvIaBCARmPdJI6/wofeymaLSjCurIUq+cikHlTA=
```

- Salva a requisição no banco de dados H2.

## Notas Importantes

- **Limitação de Taxa**: Implementação de limite de taxa conforme as diretrizes da API HubSpot.
- **Segurança**: Usa Spring Security. O CSRF está desativado por simplicidade.
- **Banco de Dados**: Utiliza um banco de dados H2 em memória para desenvolvimento. Para produção, considere um banco persistente.

## Possíveis Melhorias

- Melhor tratamento de erros e logging.
- Implementação de um sistema de rate limiting mais sofisticado.
- Adição de testes unitários e de integração.
- Proteção da aplicação com mecanismos avançados de autenticação e autorização.
- Permitir configurações dinâmicas para as credenciais do HubSpot a partir do @RequestParam
