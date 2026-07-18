# 📚 Biblioteca PDF
Projeto com a ideia de organizar arquivos PDFs em um único site.

## Instalação e uso
### Dependências
Para executar o projeto é necessário ter instalado:
```
node (20.19.5 ou superior)
npm (10.8.2 ou superior)
Java (21.0.11 ou superior)
maven (3.9.16 ou superior)
```
### Uso
Antes de iniciar o frontend, é necessário instalar executar o comando `npm intall` dentro da pasta `client/` para instalar todas as dependências encontradas no arquivo `package.json`.

#### Frontend 
Para iniciar o frontend, é necessário usar o comando `npm run dev` dentro da pasta `client/`. Após isso, a url `http://localhost:5173/` poderá ser acessada.

#### Backend
Para iniciar o backend, é necessário usar o comando `mvn spring-boot:run` dentro da pasta `server/`. Após isso, a url `http://localhost:8080/` poderá ser acessada e será possível fazer a comunicação com o frontend.

## Ideia inicial do projeto
Adicionar, remover e vizualizar um arquivo PDF localmente, sem utilizar um banco de dados. (quase um CRUD)

## Ferramentas usadas
- **Frontend:** React (JavaScript)
- **Backend:** Spring Boot (Java)
