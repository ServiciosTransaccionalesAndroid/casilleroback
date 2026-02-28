# Stack Tecnológico - Backend Casilleros Servientrega

## 📋 Resumen

Sistema backend desarrollado con Spring Boot para la gestión de lockers inteligentes, utilizando arquitectura REST API con PostgreSQL como base de datos y Docker para containerización.

---

## ☕ Core Framework

### Spring Boot
- **Versión:** 3.2.0
- **Descripción:** Framework principal para desarrollo de aplicaciones Java empresariales
- **Uso:** Base del proyecto, proporciona autoconfiguración, servidor embebido y gestión de dependencias

### Java
- **Versión:** 17 (LTS)
- **Descripción:** Lenguaje de programación principal
- **JDK:** Eclipse Temurin 17
- **Uso:** Desarrollo de toda la lógica de negocio y APIs

### Maven
- **Versión:** 3.8+
- **Descripción:** Herramienta de gestión y construcción de proyectos
- **Uso:** Gestión de dependencias, compilación y empaquetado

---

## 🗄️ Base de Datos

### PostgreSQL
- **Versión:** 15-alpine
- **Descripción:** Sistema de gestión de base de datos relacional
- **Puerto:** 5432
- **Uso:** Almacenamiento persistente de datos (lockers, paquetes, usuarios, transacciones)

### Flyway
- **Versión:** Incluida en Spring Boot 3.2.0
- **Descripción:** Herramienta de migración de base de datos
- **Uso:** Control de versiones y migraciones del esquema de base de datos

---

## 🔧 Spring Ecosystem

### Spring Data JPA
- **Versión:** Incluida en Spring Boot 3.2.0
- **Descripción:** Abstracción de persistencia de datos
- **Uso:** Repositorios, entidades JPA y operaciones CRUD

### Spring Security
- **Versión:** Incluida en Spring Boot 3.2.0
- **Descripción:** Framework de seguridad y autenticación
- **Uso:** Protección de endpoints, autenticación y autorización

### Spring Validation
- **Versión:** Incluida en Spring Boot 3.2.0
- **Descripción:** Validación de datos
- **Uso:** Validación de DTOs y request bodies

### Spring Boot Mail
- **Versión:** Incluida en Spring Boot 3.2.0
- **Descripción:** Soporte para envío de correos electrónicos
- **Uso:** Sistema de notificaciones por email

---

## 🔐 Seguridad y Autenticación

### JWT (JSON Web Tokens)
- **Librería:** JJWT (Java JWT)
- **Versión:** 0.12.3
- **Componentes:**
  - `jjwt-api` - API principal
  - `jjwt-impl` - Implementación
  - `jjwt-jackson` - Integración con Jackson
- **Uso:** Autenticación stateless, generación y validación de tokens

---

## 📚 Documentación API

### SpringDoc OpenAPI
- **Versión:** 2.3.0
- **Descripción:** Generación automática de documentación OpenAPI 3.0
- **Interfaz:** Swagger UI
- **URL:** http://localhost:8080/swagger-ui.html
- **Uso:** Documentación interactiva de la API REST

---

## 🔨 Utilidades

### Lombok
- **Versión:** Incluida en Spring Boot 3.2.0
- **Descripción:** Reducción de código boilerplate
- **Uso:** Generación automática de getters, setters, constructores, builders, etc.

### ZXing (Zebra Crossing)
- **Versión:** 3.5.3
- **Componentes:**
  - `core` - Librería principal
  - `javase` - Extensión para Java SE
- **Descripción:** Generación y lectura de códigos QR y de barras
- **Uso:** Generación de códigos QR para retiro de paquetes

---

## 🐳 Containerización y Orquestación

### Docker
- **Descripción:** Plataforma de containerización
- **Uso:** Empaquetado y despliegue de la aplicación

### Docker Compose
- **Versión:** 3.8
- **Descripción:** Orquestación de múltiples contenedores
- **Servicios:**
  - Backend (Spring Boot)
  - PostgreSQL
  - Adminer

### Eclipse Temurin
- **Versión Build:** 17-jdk-alpine
- **Versión Runtime:** 17-jre-alpine
- **Descripción:** Distribución OpenJDK
- **Uso:** Imagen base para construcción y ejecución

---

## 🛠️ Herramientas de Administración

### Adminer
- **Versión:** latest
- **Descripción:** Gestor de base de datos web
- **Puerto:** 8081
- **Uso:** Administración visual de PostgreSQL

---

## 🧪 Testing

### Spring Boot Test
- **Versión:** Incluida en Spring Boot 3.2.0
- **Descripción:** Framework de testing
- **Incluye:**
  - JUnit 5
  - Mockito
  - AssertJ
  - Hamcrest
- **Uso:** Tests unitarios e integración

### Spring Security Test
- **Versión:** Incluida en Spring Boot 3.2.0
- **Descripción:** Utilidades para testing de seguridad
- **Uso:** Tests de autenticación y autorización

---

## 📦 Dependencias del Driver

### PostgreSQL JDBC Driver
- **Versión:** Incluida en Spring Boot 3.2.0 (42.6.0)
- **Descripción:** Driver JDBC para PostgreSQL
- **Uso:** Conexión entre Spring Boot y PostgreSQL

---

## 🌐 Arquitectura de Red

### Docker Network
- **Tipo:** Bridge
- **Nombre:** locker-network
- **Uso:** Comunicación entre contenedores

### Puertos Expuestos
- **8080:** Backend Spring Boot (interno)
- **8090:** Backend Spring Boot (externo)
- **5432:** PostgreSQL
- **8081:** Adminer

---

## 📊 Versiones Resumidas

| Tecnología | Versión | Tipo |
|------------|---------|------|
| Java | 17 | Lenguaje |
| Spring Boot | 3.2.0 | Framework |
| Maven | 3.8+ | Build Tool |
| PostgreSQL | 15-alpine | Base de Datos |
| Docker Compose | 3.8 | Orquestación |
| JJWT | 0.12.3 | Seguridad |
| SpringDoc OpenAPI | 2.3.0 | Documentación |
| ZXing | 3.5.3 | QR Codes |
| Eclipse Temurin | 17 | JDK/JRE |

---

## 🏗️ Patrones y Arquitectura

### Patrones de Diseño
- **MVC (Model-View-Controller):** Separación de capas
- **Repository Pattern:** Abstracción de acceso a datos
- **DTO Pattern:** Transferencia de datos entre capas
- **Builder Pattern:** Construcción de objetos complejos (Lombok)
- **Dependency Injection:** Inversión de control (Spring)

### Arquitectura
- **REST API:** Arquitectura de servicios web
- **Layered Architecture:** Controller → Service → Repository → Entity
- **Stateless Authentication:** JWT para autenticación sin estado

---

## 🔄 Gestión de Configuración

### Perfiles de Spring
- **dev:** Desarrollo local
- **prod:** Producción

### Variables de Entorno
Configurables vía Docker Compose o application.yml

---

## 📝 Notas Adicionales

### Compatibilidad
- **Java:** Requiere Java 17 o superior (LTS)
- **Docker:** Compatible con Docker Engine 20.10+
- **PostgreSQL:** Compatible con versiones 12+

### Licencias
- Spring Boot: Apache 2.0
- PostgreSQL: PostgreSQL License
- JJWT: Apache 2.0
- ZXing: Apache 2.0

---

## 🚀 Próximas Tecnologías (Roadmap)

- **Redis:** Cache distribuido
- **RabbitMQ/Kafka:** Mensajería asíncrona
- **Prometheus + Grafana:** Monitoreo y métricas
- **ELK Stack:** Logging centralizado
- **Kubernetes:** Orquestación avanzada

---

**Última actualización:** Diciembre 2024  
**Versión del proyecto:** 1.0.0
