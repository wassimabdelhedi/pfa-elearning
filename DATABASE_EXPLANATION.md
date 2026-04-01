# Database Configuration & Collaborator Setup

This document details how the `elearning-backend` connects to the PostgreSQL database, and how your team can configure it so that all collaborators share the **exact same database**.

---

## 1. Current Local Database Connection
By default, the backend connects to a database existing only on your personal machine via the `elearning-backend/src/main/resources/application.yml` file:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/elearning_pfa
    username: postgres
    password: root
```

* **The Problem:** Because the URL says `localhost`, if a collaborator pulls the code and runs the Spring Boot app, it will look for a database server installed on *their* laptop. They won't see your data, courses, or users.

---

## 2. How to Share One Database Across Collaborators

To enable all team members to connect to the **same database** (meaning if you create a course, your teammate instantly sees it), you must move the database from `localhost` to the cloud.

### Step 1: Choose a Managed PostgreSQL Host (Free Options)
You need to host your database on a cloud provider. Excellent free-tier options for student/PFA projects include:
1. **Neon** (https://neon.tech): Extremely fast, serverless Postgres. Highly recommended.
2. **Supabase** (https://supabase.com): Provides a full Postgres database for free.
3. **Render** (https://render.com): Simple managed Postgres database.
4. **Aiven** (https://aiven.io/postgresql): Free tier available.

### Step 2: Create the Database
1. Create an account on your chosen provider (e.g., Neon).
2. Create a new project and select PostgreSQL.
3. The provider will generate a **Connection String** that looks like this:
   `postgres://admin_user:super_secret_password@db.cloud-provider.com:5432/elearning_pfa`

### Step 3: Update `application.yml` for the Whole Team
Take the connection string provided by the cloud host and update your `application.yml` (or create an `application-dev.yml` to prevent committing secrets to GitHub). 

Change the `url`, `username`, and `password` properties:

```yaml
spring:
  datasource:
    # Notice we change 'postgres://' to 'jdbc:postgresql://' for Java compatibility
    url: jdbc:postgresql://db.cloud-provider.com:5432/elearning_pfa?sslmode=require
    username: admin_user
    password: super_secret_password
```

### Step 4: Share & Run
Once you commit this updated `application.yml` file to your Git repository (or securely share the `.yml` file over Discord/Slack if you want to keep the password safe from public repos), all collaborators simply need to:
1. `git pull` the latest code.
2. Run the Spring Boot backend. 

*Because the `url` now points to the cloud (`db.cloud-provider.com`), every collaborator's backend will read and write to the exact same cloud database!*

---

## 3. Database Migration (`ddl-auto`)
In your `application.yml`, you have:
```yaml
  jpa:
    hibernate:
      ddl-auto: update
```
**Important note for Collaborators:**
Because `ddl-auto` is set to `update`, whenever any teammate modifies a Java Entity (e.g., adds a new field to `Course.java`) and runs the project, Spring Boot will automatically alter the database schema in the cloud for everybody. This is great for rapid development in a PFA, but be careful not to delete columns accidentally!
