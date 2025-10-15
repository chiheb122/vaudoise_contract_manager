# vaudoise_contract_manager
Technical exercise – Spring Boot API managing clients and contracts

A RESTful **Spring Boot** API that manages **clients** (Person or Company) and their **insurance contracts**.

# User Stories
As a customer advisor, I want to create,

                      **view and update a client (Person or Company) so that I can manage their profile.**
                      **- delete a client so that all their contracts are closed on today’s date.**
                      **- create a contract and update its amount so that the billing is accurate**
                      **- list active contracts and get their total so that I can track the client’s current portfolio.**

---

### Technologies
- Java 25
- Maven 3.9.11
- Spring Boot 
- Spring Data JPA (Hibernate)
- MySQL(XAMPP) and H2 (for tests)

---
### Setup
1. Clone the repository
    ```bash
    git clone https://github.com/chiheb122/vaudoise_contract_manager.git
    ```
2. Navigate to the project directory
    ```bash
    cd vaudoise_contract_manager
    ```
3. Build the project using Maven
    ```bash
    mvn clean install
    ```
4. Run the application
    ```bash
    mvn spring-boot:run
    ```
5. The application will be accessible at `http://localhost:8081`
---

### API Endpoints
**All documentation is available in Postman collection.**

Link to Postman collection:
https://www.postman.com/maintenance-explorer-22011916/vaudoise/collection/euf28ot/rest-api-vaudoise?action=share&source=copy-link&creator=30599847
---

### Architecture Explanation
The project is structured into several packages, each serving a specific purpose:

Entities: Client (abstract because it has two subtypes and cannot be instantiated directly), Person, and Company follow SINGLE_TABLE inheritance.
Each Client has many Contract entities (@OneToMany).

The main packages are:
- `controller`: Contains REST controllers that handle HTTP requests and responses.
- `service`: Contains service classes that encapsulate business logic to manage clients and contracts.
- `repository`: Contains repository interfaces for client entities to manage more efficiently the subtypes of Client (Person and Company) 
and personalize some necessary queries.
- `entities`: Contains entity classes representing the data model and their relationships.
- `dto`: Contains Data Transfer Objects (DTOs) for transferring data between without exposing the entity classes directly for security reasons
and to have more control over the data being sent and received.
- `exception`: Contains custom exception classes and a global exception handler to manage errors gracefully.
- `factory`: Contains factory classes to create instances of client entities based on specific types (Person or Company) , 
this can be useful when you want to create clients dynamically based on user input or other conditions and for the future extension of the project.