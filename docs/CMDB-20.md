# CMDB-20 — F15-01 Reusar mapeadores con adaptacion minima

## Objetivo
Validar que los mapeadores legacy se reutilizan sobre la infraestructura de conexion introducida en CMDB-19, sin cambiar SQL/SP ni mover/renombrar clases.

## Alcance aplicado
- No se modificaron archivos de mapeadores en `src/main/java/com/atissosa/cmdb_backend/persistency`.
- Se agrego una suite de integracion para validar reutilizacion por familia y contratos relevantes.
- Se genero matriz de mapeadores con operaciones soportadas y dependencia de conexion.

## Inventario y matriz
Inventario estructurado generado en:
- `docs/evidence/cmdb20/mapper-matrix.json`

Incluye por mapeador SQL Server:
- operaciones `buscar` / `insertarSP` / `actualizar` / `eliminar` (soportada o no)
- dependencia de conexion hacia `DataSource -> BD -> BDSqlServer`
- estado de cambio (`unchanged`) y justificacion
- referencia a evidencia de validacion

## Validacion por familia
Detalle de cobertura por familia en:
- `docs/evidence/cmdb20/family-validation.json`

Resumen:
- catalogos: validacion de contrato SQL para `MapeadorMarcasSqlServer` (la vista `VMarcas` no existe en la imagen SQL descartable usada por tests)
- lugares: lectura, insercion SP con clave generada, eliminacion y rollback compartido
- equipos/subtipos: lecturas representativas con `DTOFiltroEquipo`
- plantillas: lecturas generales y casos sin resultados/filtro por tipo
- usuarios/accesos: lecturas, vacios y contrato de excepcion ante operacion no soportada
- historicos: lectura representativa

## Pruebas nuevas
Clase agregada:
- `src/test/java/com/atissosa/cmdb_backend/persistency/MapeadoresAdaptacionIntegrationTests.java`

Casos:
- `cubreLecturasRepresentativasPorFamiliaDeMapeadores`
- `conservaBusquedasVaciasYTratamientoDeNull`
- `conservaClaveGeneradaYRollbackEnOperacionCompuesta`
- `conservaContratoDeExcepcionAnteErrorDePersistencia`

Inventario de pruebas actualizado en:
- `docs/test-inventory-additions.json`

## Verificacion ejecutada
Comandos:
- `./mvnw -B -Dtest=MapeadoresAdaptacionIntegrationTests test`
- `./mvnw -B clean verify`
- `python scripts/verify-test-inventory.py`

Resultados:
- Suite F15: 4 tests, 0 fallos, 0 errores
- Verify completo: 156 tests, 0 fallos, 0 errores, 0 omitidos
- Inventario: esperados 156, base 131, agregados 25, sin faltantes/duplicados/inesperados

Evidencia:
- `docs/evidence/cmdb20/mvn-mapper-tests.txt`
- `docs/evidence/cmdb20/mvn-clean-verify.txt`
- `docs/evidence/cmdb20/verify-test-inventory.txt`
- `docs/evidence/cmdb20/mapper-matrix.json`
- `docs/evidence/cmdb20/family-validation.json`

## Notas
- Durante el arranque de Testcontainers SQL Server se observan advertencias de prelogin esperables al inicio del servicio.
- No hubo cambios en SQL/SP de negocio ni en contratos HTTP.
