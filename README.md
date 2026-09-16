# Microsserviços

Projeto de estudo com uma arquitetura de microsserviços em Spring Boot, utilizando **Eureka** para service discovery e um **API Gateway** para roteamento e balanceamento de carga das requisições.

## Arquitetura geral

```
                    ┌─────────────────┐
                    │  Eureka Server  │
                    │ Service Discovery│
                    └───────┬─────────┘
                            │
             registra/descobre instâncias
                            │
       ┌────────────────────┼────────────────────┐
       │                    │                    │
   msClientes           msCartoes            msAvaliadorCredito
  :8081 :8082          :8091 :8092             :8101 :8102
       ▲                    ▲                    ▲
       │                    │                    │
       └────────────────────┼────────────────────┘
                            │
                       ┌────┴────┐
Cliente ──────────────►│ Gateway │
                       └─────────┘
```

## Serviços

### EurekaService (Eureka Server)

Fornece as informações de quais instâncias existem. Responsável por registrar e descobrir as instâncias dos microsserviços disponíveis.

```
Eureka Server
   │
   ├── msClientes
   │      ├── localhost:8081
   │      └── localhost:8082
   │
   ├── msCartoes
   │      ├── localhost:8091
   │      └── localhost:8092
   │
   └── msAvaliadorCredito
          ├── localhost:8101
          └── localhost:8102
```

### msGateway — API Gateway (Eureka Client / Load Balance)

Serviço responsável por receber as requisições externas e decidir para qual microsserviço elas devem ser encaminhadas.

```
Cliente
   │
   ▼
msGateway
   │
   ├── /clientes  ──────► msClientes
   │
   ├── /cartoes   ──────► msCartoes
   │
   └── /avaliacoes-credito ────► msAvaliadorCredito
```

Quando existem várias instâncias do mesmo serviço, o Gateway faz o balanceamento de carga entre elas:

```
                  ┌──► msClientes :8081
Cliente → Gateway ┤
                  └──► msClientes :8082
```

### msClientes (Eureka Client)

Microsserviço responsável por realizar o cadastro e a consulta de clientes.

### msCartoes (Eureka Client)

Microsserviço responsável por realizar o processamento de criação e consulta de cartões.

### msAvaliadorCredito (Eureka Client)

Microsserviço responsável por realizar o processamento de avaliação de crédito e solicitação de cartão. Consome os serviços `msClientes` e `msCartoes` para compor a análise.

## Autenticação (Keycloak)

A autenticação é feita via **Keycloak**, com validação centralizada no `msGateway`.

- **Realm:** `msCourseRealm`
- **Client:** `msCredito` — client confidencial (possui client secret), com os fluxos:
  - `Standard Flow` (Authorization Code)
  - `Direct Access Grants` (Resource Owner Password Credentials)
  - `Service Accounts` (Client Credentials)
- **Redirect URI configurada:** `http://localhost:8080`

Fluxo de autenticação:

```
Cliente
   │
   ▼
Keycloak (realm msCourseRealm)
   │  autentica e emite o token JWT
   ▼
msGateway  ───► valida o token junto ao Keycloak
   │
   ▼
msClientes / msCartoes / msAvaliadorCredito
```

O `msGateway` é o ponto único que valida o token JWT emitido pelo Keycloak antes de encaminhar a requisição para o microsserviço de destino. O arquivo `realm-export.json` com a configuração do realm está disponível na raiz do projeto para importação no Keycloak.

## Tecnologias

- Java + Spring Boot
- Spring Cloud Netflix Eureka (Service Discovery)
- Spring Cloud Gateway (roteamento e load balance)
- Keycloak (autenticação/autorização via OAuth2/JWT)
- RabbitMQ (mensageria entre `msCartoes` e `msAvaliadorCredito`)
- Docker (build multi-stage)
- Maven

## Estrutura do projeto

```
Udemy_Microservicos/
├── EurekaService/
├── msGateway/
├── msClientes/
├── msCartoes/
└── msAvaliadorCredito/
```

## Como executar

1. Suba o **EurekaService** primeiro, para que os demais serviços consigam se registrar.
2. Suba os microsserviços clientes (`msClientes`, `msCartoes`, `msAvaliadorCredito`), na ordem que preferir.
3. Suba o **msGateway** por último, para que ele já encontre as instâncias registradas no Eureka.
4. Acesse os endpoints através do Gateway, que fará o roteamento para o microsserviço correto.

## Docker

Cada microsserviço (`msClientes`, `msCartoes`, `msAvaliadorCredito`, `msGateway`) possui um `Dockerfile` próprio, seguindo o mesmo padrão de **build multi-stage**:

1. **Stage `BUILD`** — usa a imagem `maven:3.9.16-amazoncorretto-11-al2023` para compilar o projeto com `mvn clean package -DskipTests`.
2. **Stage final** — usa a imagem `amazoncorretto:11.0.32-al2-native-jdk`, copiando apenas o `.jar` gerado no stage anterior, reduzindo o tamanho da imagem final.

```dockerfile
FROM maven:3.9.16-amazoncorretto-11-al2023 as BUILD
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM amazoncorretto:11.0.32-al2-native-jdk
WORKDIR /app
COPY --from=BUILD ./build/target/*.jar ./app.jar
ARG EUREKA_SERVER=localhost
ENTRYPOINT java -jar app.jar
```

### Build args por serviço

| Serviço | Build args disponíveis |
| --- | --- |
| `EurekaService` | — (expõe a porta `8761`) |
| `msClientes` | `EUREKA_SERVER` |
| `msCartoes` | `EUREKA_SERVER`, `RABBITMQ_SERVER` |
| `msAvaliadorCredito` | `EUREKA_SERVER`, `RABBITMQ_SERVER` |
| `msGateway` | `EUREKA_SERVER`, `KEYCLOAK_SERVER`, `KEYCLOAK_PORT` |

### Build e execução de um serviço

```bash
cd msGateway
docker build -t ms-gateway --build-arg EUREKA_SERVER=eureka-host --build-arg KEYCLOAK_SERVER=keycloak-host --build-arg KEYCLOAK_PORT=8081 .
docker run -p 8080:8080 ms-gateway
```
