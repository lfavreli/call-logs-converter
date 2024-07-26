# 📄 Phone Statement Converter

Welcome to the Phone Statement Converter web server project! This application converts telephone statements in PDF format into CSV format. It showcases advanced Java and Spring ecosystem skills, providing a robust and scalable solution with an intuitive API.

## 🚀 Project Overview

The objective of this web server is to convert telephone statement PDFs into CSV files. It provides three main endpoints for users to upload PDFs, check conversion status, and download the converted CSVs.

## 📑 Endpoints

### 1. Upload PDF Document
- **Endpoint:** `/api/documents/`
- **Method:** `POST`
- **Description:** Upload a PDF telephone statement to be converted into CSV format.
- **Request Body:** `form-data` with a key named `file` and the PDF as the value.
- **Response:** the `documentId` to track the conversion status.

### 2. Check Conversion Status
- **Endpoint:** `/api/documents/{documentId}/status`
- **Method:** `GET`
- **Description:** Check the conversion status of the uploaded document. Status can be `COMPLETED` or `FAILED`.
- **Response:** indicating the conversion status.

### 3. Download Converted CSV
- **Endpoint:** `/api/documents/{documentId}/download`
- **Method:** `GET`
- **Description:** Retrieve the converted document in CSV format.
- **Response:** CSV file

## 🛠️ How It Works

1. **Upload PDF**: User uploads a PDF via `/api/documents/`. The server processes the file asynchronously.
2. **Conversion Status**: The user can check the conversion status by querying `/api/documents/{documentId}/status`.
3. **Download CSV**: Once conversion is complete, the CSV can be downloaded from `/api/documents/{documentId}/download`.

## 🧑‍💻 Skills and Technologies

### Core Technologies

- **Java 21**: Utilizes new features such as Pattern Matching for `instanceof`, Record Classes or Sequenced Collections.
- **Spring Boot**: Provides a robust framework for building production-ready applications.
- **Spring WebFlux & Reactor**: Enables asynchronous and non-blocking communication using reactive programming paradigms.
- **Maven Wrapper**: Ensures consistent Maven versions across different environments for building and managing the project.

### Architecture

- **Hexagonal Architecture**: Organizes the code using the ports and adapters pattern to separate concerns and improve testability and maintainability.
- **Asynchronous Processing with SSE**: Utilizes Server-Sent Events (SSE) to notify users about the conversion status in real-time, leveraging the power of asynchronous communication.

### Reactive Components

- **Mono and Flux**: Represents asynchronous sequences in the application.
- **Sinks, Schedulers, and Operators**: Utilized for handling and transforming streams of data.
- **Spring Functional Endpoints**: Implements routes using `RouterFunction` for a more functional style.

### Testing and Quality Assurance

Comprehensive integration and unit tests ensure the reliability and correctness of the application. The project uses the following tools to achieve test coverage and maintain code quality:

- **JUnit 5**: Framework for writing and running tests.
- **Mockito**: Mocks dependencies for unit testing.
- **Hamcrest**: Provides matchers for writing readable assertions.
- **spring-test & reactor-test**: Enhances testing capabilities for Spring and Reactor applications.

### Logging

- **SLF4J**: Simple Logging Facade for Java to ensure consistent logging throughout the application.

### Dependencies

- **Apache PDFBox**: Used for parsing and extracting content from PDF files.
- **Apache Commons CSV**: Facilitates CSV file creation and manipulation.

## 📦 Usage

To build and run the project using the Maven Wrapper, follow these steps:

1. **Build the project:**
   ```sh
   ./mvnw clean install
   ```

2. **Run the application:**
   ```sh
   ./mvnw spring-boot:run
   ```

## 🌟 Conclusion

This project demonstrates advanced knowledge and practical skills in modern Java development, reactive programming with Spring WebFlux, and a strong emphasis on testing and architecture. It's designed to be scalable, maintainable and efficient.

Feel free to contact me with any questions or contributions!

---

**Author:** Loïc FAVRELIERE  
**LinkedIn:** [linkedin.com/in/loic-favreliere](https://www.linkedin.com/in/loic-favreliere)