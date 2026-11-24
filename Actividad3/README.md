# Actividad 3 — Automatizando el despliegue de una aplicación Java y MySQL con Docker

Proyecto **GestionServidoresDevOps** adaptado para persistir la colección de servidores en **MySQL 8.0** mediante JDBC (`mysql-connector-j:8.3.0`) y orquestado con **Docker Compose**.

## Estructura de la entrega

- [`GestionServidoresDevOps/`](GestionServidoresDevOps/) — Código fuente Java 21, suite de pruebas JUnit 5, `pom.xml`, `Dockerfile` multi-etapa y `docker-compose.yml`.
- [`GestionServidoresDevOps.zip`](GestionServidoresDevOps.zip) — Proyecto Java exportado en formato `.zip`.
- [`video_docker_compose_build.mp4`](video_docker_compose_build.mp4) — Vídeo del proceso completo de construcción con `docker compose build`.
- [`01_acciones_terminal_docker_compose.png`](01_acciones_terminal_docker_compose.png) — Captura de las acciones en terminal tras la ejecución (`docker compose ps`, imagen generada y consulta a la tabla `servidores` en MySQL).
- [`02_ejecucion_app_menu_parte1.png`](02_ejecucion_app_menu_parte1.png) — Captura de la ejecución con `docker compose run --rm app` (consulta inicial y alta de nuevo servidor).
- [`03_ejecucion_app_menu_parte2.png`](03_ejecucion_app_menu_parte2.png) — Captura de la ejecución (modificación, borrado, listado actualizado y salida).

## Comandos principales

```bash
# Construir la imagen de la aplicación (compila, ejecuta JUnit y empaqueta el JAR)
docker compose build

# Levantar MySQL y ejecutar la aplicación interactiva en consola
docker compose run --rm app
```
