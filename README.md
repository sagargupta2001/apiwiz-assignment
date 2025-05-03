# 🔁 APIWIZ Sync/Async Invoker

A lightweight Quarkus-based service to synchronously and asynchronously invoke external APIs using configurable HTTP methods, request bodies, headers, and timeouts.

## 📦 Project Structure

```
sagargupta2001-apiwiz-assignment/
├── README.md
├── gradle.properties
├── gradlew
├── gradlew.bat
├── .dockerignore
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── docker/
│   │   │   ├── Dockerfile.jvm
│   │   │   ├── Dockerfile.legacy-jar
│   │   │   ├── Dockerfile.native
│   │   │   └── Dockerfile.native-micro
│   │   ├── java/
│   │   │   └── org/
│   │   │       └── apiwiz/
│   │   │           ├── ApiwizApplication.java
│   │   │           ├── annotations/
│   │   │           │   ├── AsyncClient.java
│   │   │           │   └── SyncClient.java
│   │   │           ├── api/ -> Contains actual JAX-RS resources
│   │   │           │   ├── AsyncApiResource.java
│   │   │           │   ├── SyncApiResource.java
│   │   │           ├── client/ -> Factories or service clients
│   │   │           │   ├── AsyncRestFactory.java
│   │   │           │   ├── SyncRestFactory.java
│   │   │           │   ├── ApiFactory.java
│   │   │           │   └── WebClientProducer.java
│   │   │           ├── http/ -> Low-level HTTP helpers
│   │   │           │   └── HttpDeleteWithBody.java
│   │   │           ├── model/ -> DTOs and Enums
│   │   │           │   ├── ApiMethod.java
│   │   │           │   ├── RequestDTO.java
│   │   │           │   ├── RequestDTOWrapper.java
│   │   │           │   └── RequestWrapper.java
│   │   │           └── util/ -> Utility Classes
│   │   │               └── RequestUtils.java
│   │   └── resources/
│   │       └── application.properties
```

---

## 🚀 Features

- 🔁 **Sync and Async HTTP invocation**
- 📡 Supports all HTTP methods (GET, POST, PUT, PATCH, DELETE, OPTIONS)
- 🧠 Pluggable request factory (`ApiFactory`) interface
- ⏱️ Timeout control per request
- 🪪 Ready for testing and native builds with Quarkus
- 🧑‍💻 SSL Support
---

## 🛠️ Technologies

- [Quarkus](https://quarkus.io/) (JAX-RS, CDI)
- Jakarta REST & Dependency Injection
- Vert.x WebClient (for async calls)
- Java 17+
- Gradle

---

## 🧑‍💻 API Usage

### 🔗 `POST /api/sync/invoke`

Invokes an HTTP request **synchronously**.

**Request Body:**

```json
{
  "apiMethod": "GET",
  "requestDTO": {
    "url": "http://localhost:8080/delay",
    "headerVariables": {
      "Content-Type": "application/json"
    },
    "bodyType": "application/json",
    "requestBody": null,
    "params": []
  },
  "timeout": 3500
}

```

**Response:**
- Returns raw response from the external API with status code and body.

---

### 🔗 `POST /api/async/invoke`

Invokes an HTTP request **asynchronously**.

**Request Body:**

```json
{
  "apiMethod": "GET",
  "requestDTO": {
    "url": "http://localhost:8080/delay",
    "headerVariables": {
      "Content-Type": "application/json"
    },
    "bodyType": "application/json",
    "requestBody": null,
    "params": []
  },
  "timeout": 3500
}

```

**Response:**
- Returns raw response from the external API with status code and body.

---

## 🚪 Running the App

### 🏗️ Build

```bash
./gradlew clean build
```

### 🏃 Run (Dev Mode)

```bash
./gradlew quarkusDev
```

### 🐳 Docker Build

```bash
./gradlew build -Dquarkus.package.type=uber-jar
docker build -f src/main/docker/Dockerfile.jvm -t apiwiz-invoker .
docker run -p 8080:8080 apiwiz-invoker
```

---

## ✅ Tests

```bash
./gradlew test
```

Run integration tests:

```bash
./gradlew integrationTest
```

Run native image tests:

```bash
./gradlew nativeTest
```

---

## 📁 Extending the Project

- Add custom interceptors, retry logic, or circuit breakers.
- Support multipart/form-data or OAuth2 flows.
- Swap out `WebClient` with `HttpClient` for full synchronous blocking behavior.

---

## 🤛️ Author
Made with ❤️ by Sagar Gupta

(probably upcoming SDE at APIWIZ) 😄




