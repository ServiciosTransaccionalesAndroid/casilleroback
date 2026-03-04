# 🔧 Fix: Swagger "Failed to load remote configuration"

## Solución

### En Railway, agrega esta variable:

```bash
SERVER_URL=https://tu-dominio.up.railway.app
```

Reemplaza `tu-dominio.up.railway.app` con tu URL real de Railway.

### Ejemplo:
```bash
SERVER_URL=https://casilleroback-production.up.railway.app
```

## Verificar

Después del deploy, accede a:
```
https://tu-dominio.up.railway.app/swagger-ui.html
```

Swagger debería cargar correctamente.

## Alternativa (si sigue fallando)

Accede directamente a:
```
https://tu-dominio.up.railway.app/v3/api-docs
```

Si ves el JSON, Swagger está funcionando pero hay un problema de CORS.
