# CMDB-11 — F06-01: compatibilidad de dependencias

La revisión parte de CMDB-10 completada. No se encontraron brechas que requieran
cambiar `pom.xml`, añadir bibliotecas, exclusiones o versiones directas. Se conserva
Spring Boot 4.1.2 y la arquitectura Jersey/Jakarta REST. La comparación normalizada
con CMDB-10 no muestra cambios de coordenadas, versiones ni scopes seleccionados.
La evidencia reproducible queda en `docs/evidence/cmdb11/`.

## Dependencias principales

Todas las versiones se gestionan mediante `spring-boot-starter-parent:4.1.2` y
su `spring-boot-dependencies:4.1.2` (incluidos los BOM que este importa). El POM
efectivo y los POM publicados consultados registran esa gestión, sin repetirla
en las declaraciones del proyecto. Compile es el scope predeterminado.

| Coordenadas | Versión efectiva | Scope | Propósito y procedencia |
|---|---|---|---|
| org.springframework.boot:spring-boot-starter-jersey | 4.1.2 | compile | Directa; integración REST y servidor embebido |
| org.springframework.boot:spring-boot-starter | 4.1.2 | compile | Directa existente; infraestructura y logging base, también transitiva del starter Jersey; Maven selecciona una sola copia |
| org.glassfish.jersey.core:jersey-server, jersey-client, jersey-common | 4.0.3 | compile | Transitivas del starter; módulos complementarios de una única implementación REST |
| org.glassfish.jersey.containers:jersey-container-servlet | 4.0.3 | compile | Servlet Jersey |
| org.glassfish.jersey.ext:jersey-spring6 | 4.0.3 | compile | Integración Spring gestionada por el starter; el nombre del artefacto no implica otra versión efectiva de Spring |
| org.glassfish.jersey.inject:jersey-hk2 | 4.0.3 | compile | Inyección interna de Jersey |
| jakarta.ws.rs:jakarta.ws.rs-api | 4.0.0 | compile | API Jakarta REST |
| org.apache.tomcat.embed:tomcat-embed-core, tomcat-embed-el, tomcat-embed-websocket | 11.0.27 | compile | HTTP, Servlet 6.1 y módulos complementarios del servidor embebido |
| jakarta.inject:jakarta.inject-api | 2.0.1 | compile | Inyección Jakarta |
| jakarta.validation:jakarta.validation-api | 3.1.1 | compile | Contratos de validación Jakarta |
| org.glassfish.jersey.ext:jersey-bean-validation | 4.0.3 | compile | Integración de validación Jersey |
| org.hibernate.validator:hibernate-validator | 9.1.5.Final | compile | Implementación de validación, complementaria de su API |
| org.springframework.boot:spring-boot-jackson2 | 4.1.2 | compile | Integración JSON que el starter Jersey publicado elige explícitamente |
| org.glassfish.jersey.media:jersey-media-json-jackson | 4.0.3 | compile | Proveedor JSON REST |
| com.fasterxml.jackson.core:jackson-core, jackson-databind | 2.21.7 | compile | Parser y mapeo JSON |
| com.fasterxml.jackson.core:jackson-annotations | 2.21 | compile | Anotaciones; su numeración diferente es la publicada/gestionada, no otra implementación |
| com.fasterxml.jackson.datatype:jackson-datatype-jdk8, jackson-datatype-jsr310 | 2.21.7 | compile | Soporte de tipos JDK |
| com.fasterxml.jackson.module:jackson-module-parameter-names, jackson-module-jakarta-xmlbind-annotations | 2.21.7 | compile | Módulos complementarios de Jackson |
| com.microsoft.sqlserver:mssql-jdbc | 13.4.0.jre11 | runtime | Directa; JDBC productivo y tests. Carga reflectiva en BDSqlServer; no requiere compile |
| org.springframework.boot:spring-boot-starter-logging | 4.1.2 | compile | Transitiva del starter base |
| ch.qos.logback:logback-classic, logback-core | 1.5.38 | compile | Único proveedor SLF4J y su implementación base |
| org.slf4j:slf4j-api, jul-to-slf4j | 2.0.20 | compile | Fachada y puente JUL hacia SLF4J |
| org.apache.logging.log4j:log4j-api, log4j-to-slf4j | 2.25.5 | compile | API y puente a SLF4J; no incorpora log4j-core ni un binding competidor |
| org.springframework.boot:spring-boot-devtools | 4.1.2 | runtime, optional | Herramientas de desarrollo existentes; excluidas del JAR ejecutable por el plugin Boot |
| org.springframework.boot:spring-boot-starter-test | 4.1.2 | test | Directa; contexto y auxiliares de pruebas |
| junit:junit | 4.13.2 | test | Directa; pruebas legacy |
| org.junit.vintage:junit-vintage-engine | 6.0.3 | test | Directa; ejecuta JUnit 4 en la plataforma actual; temporal hasta CMDB-21 |
| org.junit.jupiter:junit-jupiter | 6.0.3 | test | Transitiva; test de contexto existente |
| org.testcontainers:testcontainers-mssqlserver | 2.0.5 | test | Directa; SQL Server descartable de CMDB-9 |

## Coherencia y vigencia

- El árbol verbose muestra gestión de versiones anteriores solicitadas por
  transitivas (por ejemplo SLF4J 1.7 y Jackson 2.22) hacia las seleccionadas por
  Boot. Las líneas `omitted for duplicate` representan caminos repetidos del
  grafo, no jars adicionales. No aparecen `omitted for conflict` ni coordenadas
  directas duplicadas. El listado normalizado está en `resolved-dependencies.json`.
- Se mantiene Jackson 2 para Jersey: así lo declara el POM publicado del starter
  4.1.2. No se fuerza Jackson 3 ni se mezclan proveedores JSON-B. La suite,
  inspección del JAR y comprobación del proveedor documentan la compatibilidad
  observada; no equivalen a probar todos los posibles cuerpos JSON de negocio.
- No hay imports ni APIs REST/Servlet `javax` residuales; `javax.xml` o `javax.crypto`
  del JDK, si se usan, no son APIs REST/Servlet legacy. No hay RESTEasy, CXF o
  Spring MVC en el grafo seleccionado. `spring-web` aporta infraestructura común
  y no constituye por sí solo otra implementación de endpoints.
- El logging usa Logback; los puentes JUL y Log4j son complementarios. Se revisan
  también los archivos ServiceLoader del artefacto para comprobar el proveedor
  efectivo, sin confundir logback-core con un segundo binding.
- La documentación oficial de Boot 4.1.2 admite Java 21 y Maven 3.10.0; Microsoft
  documenta Java 21 para el driver 13.4 y la variante jre11 para Java 11 o superior.
  JUnit 6.0.3 requiere Java 17 o superior y Vintage admite JUnit 4.13.2. No es
  necesario actualizar a la última versión disponible para esta compatibilidad.
  Vintage sigue siendo temporal hasta la tarea CMDB-21.

## Fuentes oficiales consultadas al ejecutar

Consulta: 2026-11-01. Los metadatos de publicación se descargaron de Maven Central
con URL, fecha Last-Modified y SHA-256 en `publication-metadata.json`, conservando
los POM de Boot/BOM, starter Jersey, JSON Jersey, JDBC, Vintage y Testcontainers.

- [Requisitos Spring Boot](https://docs.spring.io/spring-boot/system-requirements.html).
- [Jersey y registro explícito en Boot](https://docs.spring.io/spring-boot/reference/web/servlet.html#web.servlet.jersey).
- [Versiones gestionadas por Boot](https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html).
- [Logging y puentes de Boot](https://docs.spring.io/spring-boot/reference/features/logging.html).
- [Compatibilidad del driver Microsoft 13.4](https://learn.microsoft.com/en-us/sql/connect/jdbc/release-notes-for-the-jdbc-driver?view=sql-server-ver16).
- [JUnit 6.0.3 y Vintage](https://docs.junit.org/6.0.3/overview.html).
- [Módulo SQL Server de Testcontainers](https://java.testcontainers.org/modules/databases/mssqlserver/).

## Validación

Desde `${HOME}/Projects/CMDB/cmdb-backend`:

```sh
./mvnw -version
./mvnw -B dependency:tree -Dverbose -DoutputFile=<evidencia>/dependency-tree.txt
./mvnw -B help:effective-pom -Doutput=<evidencia>/effective-pom.xml
./mvnw -B clean verify
python scripts/verify-test-inventory.py
```

JDK 21.0.13.0.2, Maven Wrapper 3.10.0. Docker accesible, SQL Server Developer
2022 CU20 aislado con fixtures de CMDB-9; se requiere red si faltan artefactos o
imágenes. No se usa una BD de producción. Se conserva íntegra la suite de 131
métodos y sus aserciones, sin filtros ni omisiones. La carpeta no tiene Git;
`pom-before.xml` y `dependency-audit.json` permiten verificar que el POM no cambió.


## Resultado y límites

`clean verify`: **BUILD SUCCESS**, duración Maven 1 min 34 s. El cotejo confirmó
131 métodos esperados y ejecutados, 0 fallos, 0 errores y 0 omitidos; sin faltantes,
duplicados ni inesperados. `build.log`, `inventory.txt` y `surefire-reports/`
conservan los resultados completos.

`jar-inspection.json` registra todas las bibliotecas y el SHA-256 del artefacto:
un driver JDBC (`SQLServerDriver`), un proveedor SLF4J
(`LogbackServiceProvider`), sin bibliotecas exclusivas de test, bindings rivales,
APIs REST/Servlet javax ni DevTools. Se inspeccionaron los archivos ServiceLoader
dentro de los jars, no solo sus nombres.

`JsonProviderProbe.java` es una comprobación puntual conservada como evidencia,
fuera de src/test y de la suite de 131 métodos: usa exclusivamente las clases y
bibliotecas extraídas del JAR productivo en un directorio temporal. El proveedor
Jackson de Jersey serializó y deserializó un DTOService con campos de texto y
Unicode, preservando valores. Salida y comando en `json-provider-probe.log` y
`json-provider-probe.json`. No añade librerías ni cambia el código productivo.

Se arrancó el JAR con Java 21 en 127.0.0.1 y puerto aleatorio. Los **24 recursos**
respondieron OPTIONS 200 y anunciaron todos los métodos del inventario de CMDB-8;
se verificó el contrato de `/recursos/login`. No aparecieron errores de carga de
clases ni proveedores; proceso detenido al finalizar. Evidencia: `startup.log`,
`http-options.json`. Sigue el aviso preexistente de WADL desactivado por ausencia
de implementación JAXBContext: generación WADL/XML no es una capacidad actual
requerida y no justifica añadir un runtime JAXB. JSON y REST funcionan en las
comprobaciones realizadas.

El build conserva las excepciones internas legacy observadas en CMDB-9, incluidas
las de mantenimiento de códigos y un hilo de sesión. El resultado JUnit no prueba
su ausencia. OPTIONS y el round trip JSON no acreditan operaciones reales contra
BD de producción ni todos los contratos de negocio; no se ejecutaron contra ella.

Duración desde preparación: 235 s (2026-11-01T11:45:18.201551+00:00 a 2026-11-01T11:49:12.731325+00:00).
Cuota de sesión antes/después: 50.0 % /
54.0 % usado en ventana de cinco horas;
36.0 % /
36.0 % semanal. Observaciones con
fecha en `quota-before.json` y `quota-after.json`; no miden consumo exclusivo de
esta tarea. Sin dependencias necesarias pendientes ni bloqueos. Solo se añadieron
documentación y evidencias, conforme a la opción explícita de CMDB-11 de validar
sin forzar cambios cuando el conjunto existente cumple.
