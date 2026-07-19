# Campus Lost and Found Management System

A full-stack campus lost and found management system built with SpringBoot 2.7 + Vue3 + Element Plus.

## Tech Stack

### Backend
- SpringBoot 2.7.18
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Redis (for token caching)
- Lombok, Hutool

### Frontend
- Vue 3 + Vite
- Element Plus
- Axios
- ECharts
- Vue Router 4

## Setup Instructions

### Prerequisites
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0
- Redis
- Node.js 16+
- npm 8+

### Database Setup

1. Create database and tables:
mysql -u root -p < backend/sql/init.sql

2. Update database credentials in backend/src/main/resources/application.yml

### Backend Setup

1. Navigate to backend directory:
cd backend
mvn clean package
java -jar target/campus-lost-found-1.0.0.jar

Backend will run on http://localhost:8080

### Frontend Setup

1. Navigate to frontend directory:
cd frontend
npm install
npm run dev

Frontend will run on http://localhost:5173

### Default Accounts

- Admin: username: admin, password: admin123
- Student: Register a new account

## Project Structure

campus-lost-found/
- backend/          # SpringBoot backend
- frontend/         # Vue3 frontend
