# TaskTrackr

## Project Description
TaskTrackr is a project management application that simplifies the entire project lifecycle. From task creation and assignment to deadline and resource control, it provides a comprehensive solution for managing projects efficiently.

## Architecture Diagrams

### System Architecture
```
┌─────────────────────────────────────────────────────────┐
│                     Frontend                            │
│  ┌─────────────────┐    ┌─────────────────────────┐     │
│  │  Android App    │    │  Local Database (Room)  │     │
│  │  Jetpack Compose│    │  Offline/Online Sync    │     │
│  └─────────────────┘    └─────────────────────────┘     │
└───────────────────────────┬─────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────┐
│                     Backend                             │
│  ┌─────────────────┐    ┌─────────────────────────┐     │
│  │  Spring Boot    │    │  PostgreSQL Database    │     │
│  │  REST API       │    │                         │     │
│  └─────────────────┘    └─────────────────────────┘     │
└─────────────────────────────────────────────────────────┘
```

## Dependencies

### Frontend Dependencies
- Android SDK
- Kotlin
- Jetpack Compose
- AndroidX Libraries
- Material Design Components
- Retrofit for API communication
- Coroutines for asynchronous operations
- Room for local database
- WorkManager for background tasks
- Security Crypto for secure storage

### Backend Dependencies
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT for authentication
- Lombok
- Maven/Gradle

## Installation Instructions

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or newer
- Android SDK
- Git
- PostgreSQL
- PgAdmin

### Frontend Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/GOMES31/tasktrackr-app.git
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Build and run the application

### Backend Setup
1. Clone the backend repository:
   ```bash
   git clone https://github.com/GOMES31/cm-spring-api.git
   ```

2. Database Setup (PgAdmin):
   - Open PgAdmin and connect to your PostgreSQL server
   - Create a new database:
     - Right-click on Databases → Create → Database…
     - Name: `tasktrackr_api`
     - Owner: your superuser (e.g. postgres)
   - Create a new role:
     - Expand Login/Group Roles → Create → Login/Group Role…
     - Role Name: `tasktrackr_admin`
     - Definition → Password: `1234`
   - Grant privileges:
     - Right-click your new role → Properties → Privileges
     - Grant ALL privileges on the `tasktrackr_api` database

3. JWT Secret Configuration:
   - Generate a JWT Secret:
     - Visit https://jwtsecret.com/generate
     - Click Generate
     - Copy the generated secret value
   - Configure JWT Secret in IntelliJ:
     - Click the dropdown next to the Run ▶️ button and choose Edit…
     - In Run/Debug Configurations, click Modify options → Environment variables
     - Add the following environment variable:
       ```
       JWT_SECRET=<your-generated-jwt-secret>
       ```

4. Configure the database in `application.yaml`:
   ```yaml
   spring:
     application:
       name: tasktrackr-api

     datasource:
       driver-class-name: org.postgresql.Driver
       url: jdbc:postgresql://localhost:5432/tasktrackr-api
       username: tasktrackr_admin
       password: 1234

     jpa:
       hibernate:
         # Options: None, Validate, Update, Create, Create-Drop
         # Use 'create' for first run to generate database schema, then change to 'update'
         ddl-auto: create
       show-sql: false
       properties:
         hibernate:
           format_sql: true
       database: POSTGRESQL
       database-platform: org.hibernate.dialect.PostgreSQLDialect

   server:
     servlet:
       context-path: /api

   security:
     jwt:
       secret-key: ${JWT_SECRET}
       expiration: 86400000       # 1 day
       refresh-token:
         expiration: 604800000    # 7 days
   ```

5. Run the Spring Boot application

Note: Hibernate will auto-generate tables on first run (ddl-auto=create)

## Usage Instructions

### Getting Started
1. Launch the application
2. Create an account or log in
3. Start creating and managing your projects

### Key Features
- Project Creation and Management
- Task Assignment and Tracking
- Team Creation and Management
- Edit User Profile
- Project and Tasks Statistics
- Token interceptor to refresh session automatically
- Dark and Light Mode Support
- Portrait and Landscape Orientation
- Multi-language Support (PT/EN) 