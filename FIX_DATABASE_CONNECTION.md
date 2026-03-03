# 🔧 Fix: Error de Conexión a Base de Datos en Railway

## ❌ Error
```
Cannot resolve reference to bean 'jpaSharedEM_entityManagerFactory'
```

## 🎯 Causa
Las variables de entorno de la base de datos no están configuradas correctamente en Railway.

## ✅ Solución

### 1. Verificar Variables en Railway

Ve a **Railway** → **backend** → **Variables** y asegúrate que existan:

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=28800000
RETRIEVAL_CODE_LENGTH=8
RETRIEVAL_CODE_EXPIRATION_HOURS=48
```

### 2. Problema Común: DATABASE_URL

Railway genera `DATABASE_URL` en formato PostgreSQL:
```
postgresql://postgres:password@host:port/railway
```

Pero Spring Boot necesita formato JDBC:
```
jdbc:postgresql://host:port/railway
```

### 3. Solución: Usar Variables Separadas

**OPCIÓN A: Referencias Automáticas (RECOMENDADO)**

En Railway backend → Variables, agrega:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
```

**OPCIÓN B: Valores Manuales**

Si las referencias no funcionan, copia los valores directamente:

1. Ve a **PostgreSQL** → **Variables**
2. Copia los valores de:
   - `PGHOST`
   - `PGPORT`
   - `PGDATABASE`
   - `PGUSER`
   - `PGPASSWORD`

3. En **backend** → **Variables**, crea:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://PGHOST:PGPORT/PGDATABASE
SPRING_DATASOURCE_USERNAME=PGUSER_VALUE
SPRING_DATASOURCE_PASSWORD=PGPASSWORD_VALUE
```

Reemplaza con los valores reales.

### 4. Verificar Conexión PostgreSQL

Asegúrate que el servicio PostgreSQL esté:
- ✅ Activo (verde)
- ✅ En el mismo proyecto
- ✅ Con las variables generadas

### 5. Redeploy

Después de configurar las variables:
1. **backend** → **Deployments**
2. Click **"Redeploy"**
3. Espera 2-3 minutos

---

## 📋 Checklist

- [ ] PostgreSQL está activo
- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `SPRING_DATASOURCE_URL` con prefijo `jdbc:postgresql://`
- [ ] `SPRING_DATASOURCE_USERNAME` configurado
- [ ] `SPRING_DATASOURCE_PASSWORD` configurado
- [ ] `JWT_SECRET` configurado
- [ ] Redeploy ejecutado

---

## 🧪 Verificar Después del Deploy

```bash
curl https://casilleroback-production.up.railway.app/api/health
```

Debería responder:
```json
{
  "service": "Servientrega Locker Backend",
  "status": "UP"
}
```

---

## 🚨 Si Persiste el Error

### Opción 1: Vincular Servicios

1. **backend** → **Settings**
2. Busca **"Service Variables"** o **"Connect"**
3. Selecciona **PostgreSQL**
4. Railway vinculará automáticamente

### Opción 2: Recrear Conexión

1. Elimina las variables de datasource del backend
2. En PostgreSQL, busca **"Connect"** o **"Share"**
3. Selecciona el servicio backend
4. Railway creará las variables automáticamente

---

## 📝 Ejemplo de Variables Correctas

```bash
# Perfil
SPRING_PROFILES_ACTIVE=prod

# Base de Datos (con referencias)
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}

# JWT
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=28800000

# Retrieval Codes
RETRIEVAL_CODE_LENGTH=8
RETRIEVAL_CODE_EXPIRATION_HOURS=48
```

---

## ⚠️ Nota Importante

El prefijo `jdbc:` es OBLIGATORIO en `SPRING_DATASOURCE_URL`. Sin él, Spring Boot no puede conectarse.

❌ Incorrecto: `postgresql://host:port/db`
✅ Correcto: `jdbc:postgresql://host:port/db`
