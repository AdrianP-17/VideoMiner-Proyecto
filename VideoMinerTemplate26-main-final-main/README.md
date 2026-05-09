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
Debes levantar los tres microservicios en terminales separadas. Es **recomendable** levantar primero el `VideoMiner` central para que los mineros no den error de conexión al intentar enviar (`POST`) los datos.

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
```

---

## 📡 Endpoints de Uso Frecuente (APIs)

### 📥 Obtención de Datos (Desde Mineros a Base de Datos)
Utiliza peticiones `POST` a los mineros para que vayan a Internet, extraigan los datos y se los envíen automáticamente al servicio central.

* **Importar desde PeerTube:**
  * *Ruta:* `POST http://localhost:8081/api/channels/{id}?maxVideos=10&maxComments=2`
  * *Ejemplos de IDs para probar:* `stux` (peertube.tv), `framablog.audio` (framatube).
* **Importar desde Dailymotion:**
  * *Ruta:* `POST http://localhost:8082/api/channels/{id}?maxVideos=10&maxPages=2`
  * *Ejemplos de IDs para probar:* `euronews-es`, `BBC`.

*(Nota: Si cambias `POST` por `GET` en las rutas anteriores, los mineros devolverán el JSON extraído por pantalla sin guardarlo en la base de datos).*

### 📊 Consulta de Datos (Desde la Base de Datos Central)
Una vez hechos los `POST` anteriores, puedes consultar los datos consolidados en el puerto `8080`:

* Listar todos los canales: `GET http://localhost:8080/api/channels`
* Listar todos los vídeos: `GET http://localhost:8080/api/videos`
* Ver comentarios de un vídeo: `GET http://localhost:8080/api/videos/{videoId}/comments`

---

## ⚙️ Configuración (application.properties)

Los mineros son flexibles y pueden apuntar a distintas instancias. Puedes modificar la URL base en los archivos `src/main/resources/application.properties` de cada minero:

**PeerTubeMiner:**
```properties
server.port=8081
videominer.base-url=http://localhost:8080/api
# Puedes usar peertube.tv, framatube.org, video.blender.org...
peertube.base-url=[https://peertube.tv/api/v1](https://peertube.tv/api/v1)
```

**DailymotionMiner:**
```properties
server.port=8082
videominer.base-url=http://localhost:8080/api
dailymotion.base-url=[https://api.dailymotion.com](https://api.dailymotion.com)
```

---

## 🏆 Retos Técnicos Resueltos en esta Versión

1. ✅ **Protección Jackson (Type Mismatch):** Uso estratégico de `@JsonIgnore` para evitar excepciones al mapear respuestas JSON inesperadas o incompatibles de APIs en constante evolución.
2. ✅ **Gestión de Cargas Anidadas:** Implementación de bucles de extracción secundaria (`getSubtitlesByVideo`, `getCommentsByVideo`) sin afectar al rendimiento global del `RestTemplate`.
3. ✅ **Federación de PeerTube:** Compatibilidad total con la arquitectura descentralizada de PeerTube mediante algoritmos de *Fallback* que evitan caídas por errores 404.

---
**Autores:** Proyecto Arquitectura e Integración de Sistemas Software (AISS) - Curso 2025/2026.
