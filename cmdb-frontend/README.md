# CMDB Frontend Legacy

## Inicio rapido

Esta copia es una aplicacion web estatica legacy.
No requiere proceso de build para mostrar la pantalla inicial.

1. Entrar a la carpeta `public_html`:

```bash
cd /home/seba/Projects/CMDB/cmdb-frontend/public_html
```

2. Levantar un servidor HTTP estatico:

```bash
python3 -m http.server 18080
```

3. Abrir en navegador:

- http://127.0.0.1:18080/index.html

Si se inicia el servidor desde la carpeta raiz (`cmdb-frontend`) en lugar de `public_html`,
`/index.html` devuelve 404 y la ruta correcta pasa a ser `/public_html/index.html`.

## Notas importantes

- El `package.json` no define scripts de `build` ni `test`.
- No ejecutar `npm test` o `npm run build` asumiendo que existen.
- El frontend consume backend segun `public_html/conf/propiedades.js`.
  Por defecto apunta a `http://localhost:8081/cmdb-backend/recursos/`.

## Detener servidor

En la terminal donde se ejecuto `python3 -m http.server`, presionar `Ctrl+C`.
