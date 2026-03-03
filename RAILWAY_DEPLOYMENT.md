# 🚂 Guía de Despliegue en Railway

## 📋 Requisitos Previos

- Cuenta en [Railway.app](https://railway.app)
- Repositorio Git (GitHub, GitLab o Bitbucket)
- Código subido al repositorio

---

## 🚀 Pasos para Desplegar

### 1. Crear Proyecto en Railway

1. Ingresa a [Railway.app](https://railway.app)
2. Click en **"New Project"**
3. Selecciona **"Deploy from GitHub repo"**
4. Autoriza Railway para acceder a tu repositorio
5. Selecciona el repositorio `casilleroback`

### 2. Agregar Base de Datos PostgreSQL

1. En tu proyecto de Railway, click en **"+ New"**
2. Selecciona **"Database"**
3. Elige **"Add PostgreSQL"**
4. Railway creará automáticamente la base de datos

### 3. Configurar Variables de Entorno

En el servicio de tu backend, ve a **"Variables"** y agrega:

#### Variables Obligatorias

```bash
# Perfil de Spring
SPRING_PROFILES_ACTIVE=prod

# Base de Datos (Railway las genera automáticamente si vinculas la BD)
DATABASE_URL=jdbc:postgresql://<host>:<port>/<database>
DATABASE_USERNAME=<usuario>
DATABASE_PASSWORD=<contraseña>

# Puerto (Railway lo asigna automáticamente)
PORT=8080

# JWT
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=28800000

# Códigos de Retiro
RETRIEVAL_CODE_LENGTH=8
RETRIEVAL_CODE_EXPIRATION_HOURS=48
```

#### Variables Opcionales (Email)

```bash
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password
```

### 4. Vincular Base de Datos con Backend

1. En el servicio backend, ve a **"Settings"**
2. Busca **"Service Variables"**
3. Click en **"+ Variable Reference"**
4. Selecciona las variables de PostgreSQL:
   - `DATABASE_URL` → `${{Postgres.DATABASE_URL}}`
   - O usa las variables individuales:
     - `PGHOST` → Host
     - `PGPORT` → Puerto
     - `PGDATABASE` → Nombre BD
     - `PGUSER` → Usuario
     - `PGPASSWORD` → Contraseña

### 5. Configurar Build

Railway detectará automáticamente el `Dockerfile`. Si no:

1. Ve a **"Settings"** del servicio
2. En **"Build"** selecciona:
   - **Builder:** Dockerfile
   - **Dockerfile Path:** `Dockerfile`

### 6. Desplegar

1. Railway iniciará el build automáticamente
2. Espera a que termine (puede tomar 3-5 minutos)
3. Una vez completado, Railway te dará una URL pública

---

## 🔧 Configuración Avanzada

### Usar Variables de Railway Directamente

Si Railway genera las variables de PostgreSQL automáticamente, puedes usarlas así:

```bash
# En lugar de DATABASE_URL, usa:
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
```

### Configurar Dominio Personalizado

1. Ve a **"Settings"** del servicio backend
2. Busca **"Domains"**
3. Click en **"Generate Domain"** (Railway te da uno gratis)
4. O agrega tu dominio personalizado

### Health Check

Railway verificará automáticamente que tu app esté corriendo. Puedes configurar:

1. **Settings** → **"Deploy"**
2. **Health Check Path:** `/api/health`
3. **Health Check Timeout:** 300 segundos

---

## 📝 Variables de Entorno Completas

### Opción 1: URL Completa (Recomendado)

```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://containers-us-west-xxx.railway.app:5432/railway
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=xxxxxxxxxxxxx
PORT=8080
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=28800000
RETRIEVAL_CODE_LENGTH=8
RETRIEVAL_CODE_EXPIRATION_HOURS=48
```

### Opción 2: Variables Separadas

```bash
SPRING_PROFILES_ACTIVE=prod
PGHOST=containers-us-west-xxx.railway.app
PGPORT=5432
PGDATABASE=railway
PGUSER=postgres
PGPASSWORD=xxxxxxxxxxxxx
PORT=8080
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=28800000
```

---

## 🔍 Verificar Despliegue

### 1. Ver Logs

```bash
# En Railway, ve a "Deployments" y click en el último deploy
# Verás los logs en tiempo real
```

### 2. Probar Health Check

```bash
curl https://tu-app.railway.app/api/health
```

### 3. Acceder a Swagger

```
https://tu-app.railway.app/swagger-ui.html
```

---

## ⚠️ Problemas Comunes

### Error: "Port already in use"

**Solución:** Railway asigna el puerto automáticamente. Asegúrate de usar `${PORT:8080}` en application-prod.yml

### Error: "Connection refused to database"

**Solución:** 
1. Verifica que las variables de BD estén correctas
2. Asegúrate de vincular el servicio PostgreSQL con el backend
3. Revisa que `SPRING_PROFILES_ACTIVE=prod`

### Error: "Flyway migration failed"

**Solución:**
1. La primera vez puede fallar si la BD está vacía
2. Railway ejecutará las migraciones automáticamente
3. Si persiste, verifica los scripts en `src/main/resources/db/migration`

### Build muy lento

**Solución:**
- Railway usa el Dockerfile multi-stage
- El primer build toma más tiempo (descarga dependencias)
- Los siguientes builds usan caché y son más rápidos

### Error: "Application failed to start"

**Solución:**
1. Revisa los logs en Railway
2. Verifica que todas las variables de entorno estén configuradas
3. Asegúrate que el perfil `prod` esté activo

---

## 💰 Costos

### Plan Gratuito (Hobby)
- **$5 USD de crédito mensual** (gratis)
- Suficiente para:
  - 1 backend Spring Boot
  - 1 base de datos PostgreSQL
  - ~500 horas de ejecución

### Plan Pro
- **$20 USD/mes**
- Recursos ilimitados
- Mejor rendimiento

---

## 🔄 CI/CD Automático

Railway despliega automáticamente cuando:
- Haces `git push` a la rama principal
- Creas un Pull Request (preview deployment)

### Configurar Branch de Despliegue

1. **Settings** → **"Source"**
2. Selecciona la rama (ej: `main`, `production`)
3. Railway desplegará automáticamente cada push

---

## 📊 Monitoreo

### Métricas Disponibles

Railway proporciona:
- **CPU Usage**
- **Memory Usage**
- **Network Traffic**
- **Request Count**
- **Response Time**

Accede desde el dashboard del servicio.

---

## 🔐 Seguridad

### Recomendaciones

1. **Nunca** subas credenciales al repositorio
2. Usa variables de entorno para todo
3. Cambia el `JWT_SECRET` en producción
4. Usa contraseñas fuertes para la BD
5. Habilita HTTPS (Railway lo hace automáticamente)

### Generar JWT Secret Seguro

```bash
openssl rand -base64 64
```

---

## 📚 Recursos Adicionales

- [Documentación Railway](https://docs.railway.app)
- [Railway Discord](https://discord.gg/railway)
- [Railway Status](https://status.railway.app)

---

## ✅ Checklist de Despliegue

- [ ] Código subido a GitHub/GitLab
- [ ] Proyecto creado en Railway
- [ ] PostgreSQL agregado
- [ ] Variables de entorno configuradas
- [ ] Base de datos vinculada con backend
- [ ] Build completado exitosamente
- [ ] Health check funcionando
- [ ] Swagger UI accesible
- [ ] Endpoints probados

---

**¡Listo!** Tu aplicación debería estar corriendo en Railway 🎉

**URL de ejemplo:** `https://casilleroback-production.up.railway.app`
