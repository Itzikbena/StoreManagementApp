
# Store Chain Management System

This repository contains the Store Chain Management System project, developed for the Internet Programming course at HIT - Holon Institute of Technology during the Summer Semester of 2024, under the mentorship of Mr. Roi Zimon.

## Introduction

This project is a robust client-server application developed in Java, utilizing a MySQL database for efficient data storage and management.

To facilitate smooth communication between the client and server, the application employs a custom protocol for encoding and decoding actions and objects. This mechanism ensures secure and efficient data transmission over sockets, enabling the server to process requests and execute corresponding actions seamlessly.

## Main Features

### 1. Real-Time Messaging
- **Branch-to-Branch Communication**: Employees in different branches can communicate through a real-time chat system.
- **Chat Interface Demonstration**: Demonstrates the chat functionality within the system.

### 2. Product Inventory Control
- **Manage Branch Inventories**: Interface for overseeing product inventory for each branch, including buying and selling functionality.

### 3. Customer Database Management
- **Unified Customer Information**: Manage customer data across all branches, with any updates reflected in real-time throughout the system.

### 4. Sales Analytics
- **Comprehensive Sales Reports**: Generate detailed reports for sales of specific products or product categories to analyze branch performance.
- **Export Options**: Ability to download reports as text files.

### 5. Administrative Control
- **Employee Account Setup**: Admin dashboard for creating and managing employee accounts with enforced password policies and additional details.

### 6. System Activity Logs
- **Track System Usage**: Record logs for employee and customer registrations, sales transactions, and chat history (when enabled by Admin).

## Getting Started

To run the application locally, follow these instructions:

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Itzikbena/StoreManagementApp
   ```

2. **Set Up the MySQL Database:**
   - Create a MySQL database named `StoreManagementApp_db`.

3. **Configure the Application:**
   - Update the database connection settings in the configuration file with your MySQL credentials.

4. **Build and Run the Application:**
   - Use a Java IDE like IntelliJ IDEA or Eclipse to build and run the application.

5. **Access the Application:**
   - Open your web browser and go to `http://localhost:8080` to use the Store Chain Management System.

## Video Demonstration

For a demonstration of the system's features, check out our video on YouTube:

[Watch the video](https://youtu.be/J5zn9XHEFGk)
