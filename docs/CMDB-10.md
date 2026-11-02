# CMDB-10 — F05-01: dependencias Maven

## Cambio y trazabilidad

`pom.xml` cambia únicamente `com.microsoft.sqlserver:mssql-jdbc` de `test` a `runtime`. `BDSqlServer` carga el driver por `Class.forName` y abre conexiones con `DriverManager`; no requiere el driver para compilar, sí en ejecución y pruebas. No se añaden dependencias duplicadas ni versiones que sobrescriban el parent.

Se conservan el parent Spring Boot **4.1.2**, Jersey de CMDB-8 y JUnit 4/Vintage/Testcontainers de CMDB-9. El árbol completo con versiones y scopes está en `evidence/cmdb10/dependency-tree.txt`; `effective-pom.xml` registra la gestión efectiva y plugins. Las versiones de las filas resueltas son gestionadas por ese parent/BOM; no se usa `provided`, `system`, instalación manual ni repositorios adicionales. Maven Central (`https://repo.maven.apache.org/maven2`) es el repositorio efectivo y la distribución del Wrapper Maven 3.10.0 procede de Central.

Fuente histórica, solo lectura: `${HOME}/Desktop/CMDB_ENTREGA/Fuentes/cmdb-backend`. Se revisaron `nbproject/project.properties`, `nbproject/private`, `build.xml`, `nbproject/build-impl.xml`, imports Java, jars de `build/web/WEB-INF/lib` y `dist/cmdb-backend.war`. `legacy-inventory.json` registra cada jar y ubicación; las copias de project.properties/build.xml sirven como evidencia histórica, no participan en el build activo.

| Biblioteca legacy | Coordenadas resueltas y scope / decisión | Motivo |
|---|---|---|
| aopalliance-repackaged-2.5.0-b42.jar | org.glassfish.hk2.external:aopalliance-repackaged:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| cdi-api-1.1.jar | No requerida | No se usa CDI; integración Spring/Jersey. |
| el-api-2.2.jar | org.apache.tomcat.embed:tomcat-embed-el:11.0.27:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| hamcrest-core-1.3.jar | org.hamcrest:hamcrest-core:3.0:test | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| hk2-api-2.5.0-b42.jar | org.glassfish.hk2:hk2-api:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| hk2-locator-2.5.0-b42.jar | org.glassfish.hk2:hk2-locator:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| hk2-utils-2.5.0-b42.jar | org.glassfish.hk2:hk2-utils:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| javassist-3.22.0-CR2.jar | org.javassist:javassist:3.33.0-GA:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| javax.annotation-api-1.2.jar | jakarta.annotation:jakarta.annotation-api:3.0.0:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| javax.inject-1.jar | jakarta.inject:jakarta.inject-api:2.0.1:compile | Una única Jakarta Inject API reemplaza las dos copias javax.inject; gestionada por Boot 4.1.2. |
| javax.inject-2.5.0-b42.jar | jakarta.inject:jakarta.inject-api:2.0.1:compile | Una única Jakarta Inject API reemplaza las dos copias javax.inject; gestionada por Boot 4.1.2. |
| javax.json-1.1.jar | No requerida | No hay llamadas JSON-P en fuentes; JSON por Jackson/Jersey. |
| javax.json-api-1.1.jar | No requerida | No hay llamadas JSON-P en fuentes; JSON por Jackson/Jersey. |
| javax.json.bind-api-1.0.jar | No requerida | JSON-B sustituido por Jackson/Jersey. |
| javax.servlet-api-3.0.1.jar | org.apache.tomcat.embed:tomcat-embed-core:11.0.27:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| javax.ws.rs-api-2.1.jar | jakarta.ws.rs:jakarta.ws.rs-api:4.0.0:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| jaxb-api-2.2.7.jar | jakarta.xml.bind:jakarta.xml.bind-api:4.0.5:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| jboss-interceptors-api_1.1_spec-1.0.0.Beta1.jar | No requerida | Sin CDI/interceptores; Spring/Jersey. |
| jersey-client.jar | org.glassfish.jersey.core:jersey-client:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| jersey-common.jar | org.glassfish.jersey.core:jersey-common:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| jersey-container-servlet-core.jar | org.glassfish.jersey.containers:jersey-container-servlet:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| jersey-container-servlet.jar | org.glassfish.jersey.containers:jersey-container-servlet:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| jersey-hk2.jar | org.glassfish.jersey.inject:jersey-hk2:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| jersey-media-jaxb.jar | No requerida | Sin endpoints XML ni uso directo de JAXB; Jackson atiende los contratos JSON. |
| jersey-media-json-binding.jar | No requerida | JSON-B sustituido por jersey-media-json-jackson. |
| jersey-media-sse.jar | No requerida | Sin endpoints SSE ni imports SSE. |
| jersey-server.jar | org.glassfish.jersey.core:jersey-server:4.0.3:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| jsr250-api-1.0.jar | jakarta.annotation:jakarta.annotation-api:3.0.0:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| junit-4.12.jar | junit:junit:4.13.2:test | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| mssql-jdbc-6.4.0.jre8.jar | com.microsoft.sqlserver:mssql-jdbc:13.4.0.jre11:runtime | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| org.osgi.core-4.2.0.jar | No requerida | Aplicación Spring Boot sin runtime OSGi. |
| osgi-resource-locator-1.0.1.jar | org.glassfish.hk2:osgi-resource-locator:3.0.0:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| persistence-api-1.0.jar | No requerida | Persistencia JDBC propia; no hay JPA. |
| validation-api-1.1.0.Final.jar | jakarta.validation:jakarta.validation-api:3.1.1:compile | Resuelto por parent/BOM Spring Boot 4.1.2; dependencia directa o transitiva del árbol adjunto. |
| yasson-1.0.jar | No requerida | Proveedor JSON-B sustituido por Jackson/Jersey. |

Las propiedades privadas `deploy.ant.properties.file`, `j2ee.server.home`, `j2ee.server.instance`, `user.properties.file` y opciones de IDE no tienen equivalente necesario: el Wrapper y Tomcat embebido sustituyen el despliegue Ant/servidor externo. `build.xml` importa `nbproject/build-impl.xml`, que resuelve esos classpaths; ninguno existe ni es importado en el backend activo. El classpath de Tomcat legacy enumera jars internos del contenedor (Catalina, Jasper/JSP, ECJ, Ant, JNI, DBCP, JDBC, utilidades y traducciones): no son dependencias de aplicación. La API Servlet y servidor HTTP proceden de Tomcat embebido; no hay JSP. El uso aislado del conversor de stacktrace de Tomcat JDBC fue eliminado en CMDB-8; no se requiere el pool. Las APIs `java.sql`, XML/JAXP y demás `java.*` proceden del JDK 21.

No se modifica la entrega original, lógica productiva, contratos, pruebas, aserciones ni pipelines. `src/main/java`, `src/main/resources`, `src/test/java`, `src/test/resources` conservan sus raíces estándar. La carpeta no tiene repositorio Git; `evidence/cmdb10/pom-before.xml` permite revertir el cambio.

## Entorno y procedimiento de validación

JDK 21.0.13.0.2 (Arch Linux), Maven Wrapper 3.10.0, Linux amd64.
Docker accesible; cada ejecución crea un SQL Server 2022 CU20 aislado mediante
Testcontainers, con los fixtures y licencia de desarrollo de CMDB-9. Se requiere
red para la descarga inicial de Maven/dependencias/imágenes. No se configura BD
externa ni de producción. El Wrapper ya estaba disponible en su caché propia;
el repositorio de **artefactos** temporal sí comienza vacío. No se modifica ~/.m2.

Comandos desde la raíz del backend:

```sh
./mvnw -version
# Directorio nuevo creado con tempfile.mkdtemp; ruta exacta en repository.txt
./mvnw -B -Dmaven.repo.local=<ruta-de-repository.txt> clean verify
python scripts/verify-test-inventory.py
# Conservar reportes antes del segundo clean; repetir exactamente ambos comandos.
./mvnw -B -Dmaven.repo.local=<misma-ruta> clean verify
python scripts/verify-test-inventory.py
./mvnw -B -Dmaven.repo.local=<misma-ruta> help:effective-pom -Doutput=<archivo>
./mvnw -B -Dmaven.repo.local=<misma-ruta> dependency:tree -DoutputFile=<archivo>
```

`evidence/cmdb10/run1/` y `run2/` preservan logs, reportes Surefire completos,
cotejo del inventario y códigos de salida. No se usan filtros, perfiles,
exclusiones ni opciones para omitir tests. El escaneo `active-build-scan.json`
no encontró rutas privadas, classpaths Ant, dependencias system ni repositorios
personalizados en pom.xml, configuración del Wrapper y VS Code.

Los logs conservan excepciones internas legacy (por ejemplo en
`FormatearCodigosDeEquiposEnBDTest` y un hilo de sesión), ya observadas en
CMDB-9. Las aserciones y el inventario de Surefire determinan el resultado de
la suite; su éxito no demuestra ausencia de errores internos en lógica legacy.
No se amplía esta tarea para modificar esa lógica ni sus tests.

## Resultado

Ambos `clean verify`: **BUILD SUCCESS**, 131 métodos esperados/ejecutados,
0 fallos, 0 errores, 0 omitidos; sin faltantes, duplicados ni inesperados.
Los resultados y comandos exactos están en `run1/result.json`, `run2/result.json`
e `inventory.txt` de cada ejecución. Se mantuvo el mismo repositorio temporal.

`jar-inspection.json` registra el SHA-256 y todas las bibliotecas de BOOT-INF/lib:
`mssql-jdbc-13.4.0.jre11.jar` presente, ninguna biblioteca resuelta con scope test
empaquetada y DevTools ausente. El JAR arranca con `java -jar`, enlazado a
127.0.0.1 y puerto aleatorio, sin NetBeans/Ant. `OPTIONS /recursos/login`
respondió HTTP 200; proceso detenido al finalizar. Evidencia: `startup.log`,
`smoke.json`. Esta comprobación no acredita operación contra BD de producción.

Ejecución registrada: 2026-11-01T11:39:29.163259+00:00 a 2026-11-01T11:43:33.272113+00:00,
244 segundos desde la preparación del cambio.
Cuota de sesión observada antes: 44 % usado en cinco horas, 35 % semanal;
observaciones posteriores en `quota-observations.json`. No es una medición del
consumo exclusivo de CMDB-10. No se identificaron bibliotecas necesarias sin
resolver ni infraestructura faltante.
