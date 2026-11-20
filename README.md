# Ligas deportivas — API REST

![CI](https://github.com/gaelg22/Ejercicio-10/actions/workflows/ci.yml/badge.svg)

Backend del proyecto final de la materia (UPIIZ-IPN): una API para administrar ligas deportivas con equipos, jugadores, entrenadores y partidos. El frontend en Angular está en [Proyecto-Final](https://github.com/gaelg22/Proyecto-Final).

## Stack

Java 21 · Spring Boot 3 · Spring Security + JWT · Spring Data JPA · PostgreSQL / MySQL · Swagger (springdoc) · JUnit 5 + Mockito · Docker · Render · GitHub Actions

## Qué tiene

- CRUD de **ligas, equipos, jugadores, entrenadores y partidos** con DTOs para las peticiones.
- **Login y registro con JWT**: los `GET` son públicos y crear, editar o borrar requiere token.
- Arquitectura por capas: `controllers → services → repositories → entities`.
- Documentación con **Swagger UI** en `/swagger-ui.html`.
- `DataLoader` que crea una liga de ejemplo y el usuario admin al arrancar.
- Dockerfile multi-stage (build con Maven y runtime ligero) para desplegar en Render.
- **Tests** con JUnit 5 y Mockito que corren solos en cada push con GitHub Actions.

## Endpoints principales

| Método | Ruta | Auth |
|---|---|---|
| POST | `/api/auth/register`, `/api/auth/login` | no |
| GET | `/api/ligas`, `/api/equipos`, `/api/jugadores`, `/api/entrenadores`, `/api/partidos` | no |
| POST / PUT / DELETE | mismas rutas (`/{id}` para editar y borrar) | JWT |

## Variables de entorno

Ninguna contraseña va en el código. Todo se configura con variables de entorno (en local con un `.env`, en Render en la pestaña *Environment*). Hay un ejemplo en [`.env.example`](.env.example).

| Variable | Para qué |
|---|---|
| `SPRING_DATASOURCE_URL` | URL de la base, p. ej. `jdbc:postgresql://localhost:5432/ligas` |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Usuario y contraseña de la base |
| `APP_JWT_SECRET` | Secreto para firmar los tokens (mínimo 32 caracteres) |
| `APP_JWT_EXPIRATION_MS` | Duración del token en ms (opcional, por defecto 24 h) |
| `APP_ADMIN_PASSWORD` | Contraseña del usuario admin inicial. Si no la pones, no se crea admin |

## Correrlo

```bash
./mvnw spring-boot:run
# o con Docker
docker build -t ligas-api . && docker run --env-file .env -p 8080:8080 ligas-api
```

## Tests

```bash
./mvnw test
```

Los tests usan una base H2 en memoria, así que no necesitas PostgreSQL para correrlos. Cubren:

- `LigaServiceTest` y `EquipoServiceTest`: lógica de los servicios con los repositorios mockeados (crear, actualizar, qué pasa si la liga o el entrenador no existen).
- `AuthServiceTest`: registro con contraseña encriptada, usuario repetido y login con credenciales buenas y malas.
- `JwtUtilTest`: token válido, token firmado con otro secreto, token expirado y texto que no es JWT.
- `LigasDeportivasApplicationTests`: que la app levante completa con H2.
