# EurekaService (Eureka Server)
Fornece as informações de quais instâncias existem.  
Responsável por registrar e descobrir as instâncias dos microsserviços disponíveis.  

	```	
	Eureka Server
	   │
	   ├── msCliente
	   │      ├── localhost:8081
	   │      └── localhost:8082
	   │
	   ├── mmsCartoes
	   │      ├── localhost:8091
	   │      └── localhost:8092
	   │
	```
	
# msGateway - API Gateway - (Eureka Client / Load Balance)
Serviço responsável por receber as requisições externas e decidir para qual microsserviço elas devem ser encaminhadas.

	```
	Cliente
	   │
	   ▼
	msGateway
	   │
	   ├── /users  ──────► ms-user
	   │
	   ├── /orders ──────► ms-order
	   │
	   └── /payments ────► ms-payment	
	```

Quando existem várias instâncias:

	```
					  ┌──► ms-user :8081
	Cliente → Gateway ┤
					  └──► ms-user :8082   
	```

# msClientes (Eureka Client)
Microserviço responsável por realizar o cadastro e consulta de clientes	
	
# msCartoes (Eureka Client)
Microserviço responsável por realizar o processamento de criação e consulta de cartões
	
---

```
                    ┌─────────────────┐
                    │ Eureka Server   │
                    │Service Discovery│
                    └───────┬─────────┘
                            │
             registra/descobre instâncias
                            │
       ┌────────────────────┼────────────────────┐
       │                    │                    │
   msClientes           msCartoes            x
  :8081 :8082          :8091 :8092             :8101
       ▲                    ▲                    ▲
       │                    │                    │
       └────────────────────┼────────────────────┘
                            │
                       ┌────┴────┐
Cliente ──────────────►│ Gateway │
                       └─────────┘
```