# Recipe API

The Recipe API is a RESTful API that allows users to manage recipes. It provides endpoints for creating, updating, deleting, and retrieving recipes.

## Features

- Add a new recipe
- Update an existing recipe
- Delete a recipe
- Get a recipe by ID
- Get all recipes
- Search recipes

## Technologies Used

- Java
- Spring Boot
- Spring Data JPA
- Spring Validation
- Spring Web
- Swagger UI
- MySQL
- Jackson JSON
- WebClient
- Gradle
- Spock

## Prerequisites

Before running the application, make sure you have the following installed:

- Java Development Kit (JDK) 17 or higher
- Docker
- Gradle build tool

## Getting Started

1. Clone the repository:
2. Build the project locally with running test cases 
```shell
cd recipe-api
gradle clean build
```
3. Bring up the database by running the following script:
```shell
cd infra/dev
. db-up.sh
```
4. Now Run the application
```shell
gradle bootRun
```
5. API is accessiable at: 

 http://localhost:8080/

### API Documentation

The API is documented using Swagger. You can access the Swagger UI to explore the API endpoints and interact with them.

- Swagger UI:  http://localhost:8080/swagger-ui/index.html

### API Endpoints
The following are the available API endpoints:

* `POST /recipes`: Add a new recipe
* `PATCH /recipes/{id}` : Update an existing recipe
* `DELETE /recipes/{id}`: Delete a recipe
* `GET /recipes/{id}`: Get a recipe by ID
* `GET /recipes`: Get all recipes

### Unit testing
run the following command from root folder of the project: 
```shell
./gradlew test
```

### Integration testing
Implementing using spock and spring and webclient help
* Must have the application running at localhost:8080 or else adjust the `application.properties` files accordingly
```shell
cd integrtion-tests
./gradlew test
```

### Approach
Please refer `docs` folder at the root level
* approach.md
* limitation.md files for more information 

