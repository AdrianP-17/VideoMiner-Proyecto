# 🎥 VideoMiner - Sistema de Minería de Datos Multimedia

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)
![Maven](https://img.shields.io/badge/Maven-Build-blue.svg)
![AISS](https://img.shields.io/badge/Asignatura-AISS-purple.svg)

## 📖 Descripción General
**VideoMiner** es un ecosistema basado en una arquitectura de **microservicios** diseñado para extraer, integrar y analizar contenido multimedia de diferentes plataformas de vídeo descentralizadas y comerciales. 

El sistema actúa como un agregador (ETL) que extrae datos complejos (vídeos, canales, comentarios, y subtítulos) de APIs externas, los normaliza y los consolida en una base de datos centralizada.

---

## 🏗️ Arquitectura y Microservicios

El proyecto está compuesto por tres microservicios independientes que se comunican a través de peticiones HTTP REST:

### 1. 🗄️ VideoMiner (Core) - `Puerto 8080`
Es el servicio central y el motor de persistencia. Recibe los datos normalizados de los mineros y los almacena en una base de datos relacional (H2).
* **Tecnología:** Spring Data JPA, H2 Database.
* **Interfaz de BD:** Disponible en `http://localhost:8080/h2-ui` (User: `sa`, sin contraseña).

### 2. 🌐 PeerTubeMiner (Adapter) - `Puerto 8081`
Minero especializado en la red federada de PeerTube. 
* **Características Avanzadas:**
  * **Sistema de Fallback (Doble Intento):** Debido a la naturaleza de PeerTube, el minero distingue automáticamente entre Cuentas (`/accounts`) y Canales (`/video-channels`), garantizando la extracción sin errores 404.
  * **Evasión Anti-Bots:** Configurado con cabeceras `User-Agent` personalizadas para evitar bloqueos (Error 403) en instancias protegidas.
  * **Extracción Profunda:** Realiza peticiones secundarias por cada vídeo encontrado para obtener los hilos de comentarios y los subtítulos ignorando el mapeo predeterminado de Jackson para evitar crasheos.

### 3. 📹 DailymotionMiner (Adapter) - `Puerto 8082`
Minero especializado en la API REST de Dailymotion.
* **Características Avanzadas:**
  * **Carga Diferida de Subtítulos:** Implementa llamadas iterativas al endpoint `/video/{id}/subtitles` para anexar los datos de subtítulos que la API principal omite por defecto.
  * **Paginación Controlada:** Permite especificar el límite de vídeos y de páginas a extraer para no saturar los límites de la API (Rate Limiting).

---

## 🚀 Guía de Ejecución

### Requisitos Previos
- Java 17+
- Maven instalado
- Conexión a Internet (para descargar dependencias y consultar las APIs)

### Arranque del Ecosistema
Debes levantar los tres microservicios en terminales separadas. Es **crucial** levantar primero el `VideoMiner` central para que los mineros no den error de conexión al intentar enviar (`POST`) los datos.

```bash
# 1. Arrancar el Servicio Central (Base de Datos)
cd VideoMinerTemplate26-main
mvnw spring-boot:run

# 2. Arrancar PeerTubeMiner
cd PeerTubeMiner
mvnw spring-boot:run

# 3. Arrancar DailymotionMiner
cd DailymotionMiner
mvnw spring-boot:run
