# FinTrack — Online Personal Finance Management System

A Java 17 / Jakarta Servlet / JSP / JDBC / MySQL college project with a responsive Bootstrap-style finance dashboard.

## Team
- **Team:** FinTrack Trio
- **Team leader:** Aditya Dhanraj
- **Members:** Alok Saini, Sumit
- **College:** Galgotias University
- **Branch:** B.Tech Computer Science and Engineering

## Stack
Java 17, Maven, Apache Tomcat 10.1, Jakarta Servlet 6.0, JSP 3.1, JDBC, MySQL 8+, jBCrypt, HTML/CSS/JavaScript, Chart.js.

## Requirements
- JDK 17+
- Maven 3.9+
- MySQL 8+
- Apache Tomcat 10.1
- VS Code (or another Java IDE)

## Setup (Mac)
1. Create the database by running `database/schema.sql` in MySQL Workbench.
2. Set environment variables before starting Tomcat:
   - `FINTRACK_DB_URL=jdbc:mysql://localhost:3306/fintrack?useSSL=false&serverTimezone=UTC`
   - `FINTRACK_DB_USER=your_mysql_user`
   - `FINTRACK_DB_PASSWORD=your_mysql_password`
3. From this folder run `mvn clean package`.
4. Copy `target/FinTrack.war` into Tomcat 10.1's `webapps/` directory and start Tomcat.
5. Open `http://localhost:8080/FinTrack/`.

Do not commit database passwords. The app defaults to a local development database configuration only when environment variables are absent; change credentials to match your local MySQL setup.

## Current features
- Register/login/logout with BCrypt password hashing and session handling.
- Per-user income and expense transaction creation/listing.
- Dashboard totals and recent transaction history.
- MySQL schema with user ownership foreign keys and basic validation.

## Notes
This repository is a working project baseline, not a claim of production readiness. Verify the build and run the manual test checklist in your environment before submission. Add edit/delete, budget and savings-goal CRUD, automated tests, CSRF protection, and deployment hardening before production use.