# 🔧 Fix: Error de Conexión PostgreSQL en Railway

## ❌ Error Actual
```
Connection to localhost:5432 refused
```

## 🎯 Causa
La aplicación está usando la configuración por defecto (`localhost:5432`) en lugar de la base de datos de Railway.

---

## ✅ Solución Rápida

### Paso 1: Agregar PostgreSQL en Railway

1. En tu proyecto Railway, click **"+ New"**
2. Selecciona **"Database"** → **"Add PostgreSQL"**
3. Espera a que se cree (toma ~30 segundos)

### Paso 2: Configurar Variables de Entorno

Ve al servicio **backend** → pestaña **"Variables"** y agrega:

#### Opción A: Variables Automáticas (RECOMENDADO)

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
```

#### Opción B: Variables Manuales

Si Railway ya creó las variables de PostgreSQL, cópialas:

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://containers-us-west-xxx.railway.app:6543/railway
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=tu-password-generado
```

### Paso 3: Variables Adicionales Obligatorias

```bash
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=28800000
RETRIEVAL_CODE_LENGTH=8
RETRIEVAL_CODE_EXPIRATION_HOURS=48
```

### Paso 4: Redesplegar

1. Ve a **"Deployments"**
2. Click en **"Deploy"** o haz un nuevo commit
3. Espera a que termine el build

---

## 🔍 Verificar Variables en Railway

### Ver Variables de PostgreSQL

1. Click en el servicio **PostgreSQL**
2. Ve a la pestaña **"Variables"**
3. Deberías ver:
   - `DATABASE_URL`
   - `PGHOST`
   - `PGPORT`
   - `PGDATABASE`
   - `PGUSER`
   - `PGPASSWORD`

### Copiar al Backend

1. Ve al servicio **backend**
2. Pestaña **"Variables"**
3. Click **"+ Variable Reference"**
4. Selecciona las variables del PostgreSQL

---

## 📋 Checklist de Variables

En el servicio **backend** debes tener:

- [ ] `SPRING_PROFILES_ACTIVE=prod`
- [ ] `SPRING_DATASOURCE_URL` (referencia a Postgres)
- [ ] `SPRING_DATASOURCE_USERNAME` (referencia a Postgres)
- [ ] `SPRING_DATASOURCE_PASSWORD` (referencia a Postgres)
- [ ] `JWT_SECRET`
- [ ] `JWT_EXPIRATION`
- [ ] `RETRIEVAL_CODE_LENGTH`
- [ ] `RETRIEVAL_CODE_EXPIRATION_HOURS`

---

## 🎯 Formato Correcto de DATABASE_URL

Railway genera algo como:
```
postgresql://postgres:password@containers-us-west-xxx.railway.app:6543/railway
```

Pero Spring Boot necesita:
```
jdbc:postgresql://containers-us-west-xxx.railway.app:6543/railway
```

### Solución Automática

Usa las variables separadas en lugar de DATABASE_URL:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
```

---

## 🔄 Si Ya Tienes PostgreSQL Creado

### Vincular Servicios

1. Ve al servicio **backend**
2. Pestaña **"Settings"**
3. Busca **"Service Variables"** o **"Connect"**
4. Selecciona el servicio PostgreSQL
5. Railway vinculará automáticamente las variables

---

## 🚨 Errores Comunes

### Error: "Variable not found"
**Causa:** No has vinculado PostgreSQL con el backend  
**Solución:** Usa referencias `${{Postgres.VARIABLE}}`

### Error: "Invalid database URL"
**Causa:** Falta el prefijo `jdbc:`  
**Solución:** Asegúrate que la URL empiece con `jdbc:postgresql://`

### Error: "Authentication failed"
**Causa:** Usuario/contraseña incorrectos  
**Solución:** Verifica que las variables coincidan con las de PostgreSQL

---

## ✅ Verificar que Funciona

Después del deploy, revisa los logs:

### Logs Correctos
```
Flyway migration completed successfully
Started LockerApplication in X seconds
```

### Logs con Error
```
Connection to localhost:5432 refused
```
→ Las variables NO están configuradas correctamente

---

## 📸 Ejemplo de Variables en Railway

```
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=28800000
RETRIEVAL_CODE_LENGTH=8
RETRIEVAL_CODE_EXPIRATION_HOURS=48
```

---

## 🆘 Si Sigue Sin Funcionar

1. **Elimina el servicio backend** en Railway
2. **Crea uno nuevo** desde GitHub
3. **Antes de que se despliegue**, configura todas las variables
4. **Vincula PostgreSQL** desde Settings
5. **Despliega**

---

## 📞 Soporte

Si el problema persiste:
1. Revisa los logs completos en Railway
2. Verifica que `SPRING_PROFILES_ACTIVE=prod`
3. Confirma que PostgreSQL está corriendo (verde en Railway)
4. Asegúrate que las variables tienen el formato correcto
