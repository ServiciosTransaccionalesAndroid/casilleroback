# 🚨 Fix: Out of Memory en Railway

## Problema
Railway tiene límite de **512MB RAM** en plan gratuito. Spring Boot consume ~800MB por defecto.

## ✅ Soluciones Aplicadas

### 1. Dockerfile Optimizado
```dockerfile
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=128m -XX:+UseG1GC"
```

**Explicación:**
- `-Xmx512m` → Máximo 512MB heap
- `-Xms256m` → Inicial 256MB heap
- `-XX:MaxMetaspaceSize=128m` → Límite metaspace
- `-XX:+UseG1GC` → Garbage collector eficiente

### 2. application-prod.yml Optimizado
```yaml
hikari:
  maximum-pool-size: 5  # Reduce conexiones BD
  minimum-idle: 2

tomcat:
  threads:
    max: 50  # Reduce threads
    min-spare: 10
```

## 🚀 Desplegar Cambios

```bash
git add .
git commit -m "Optimize memory for Railway"
git push
```

Railway redesplegará automáticamente.

## 📊 Monitoreo

En Railway → Metrics, verifica:
- **Memory Usage** < 512MB
- **CPU Usage** < 80%

## 🆙 Si Sigue Fallando

### Opción 1: Upgrade a Railway Pro
- **$5/mes** → 1GB RAM
- **$20/mes** → 8GB RAM

### Opción 2: Reducir Más
Agrega en Railway → Variables:
```bash
JAVA_OPTS=-Xmx400m -Xms200m -XX:MaxMetaspaceSize=100m
```

### Opción 3: Deshabilitar Features
```yaml
# application-prod.yml
spring:
  jpa:
    show-sql: false
  devtools:
    restart:
      enabled: false
```

## ✅ Verificar
```bash
curl https://tu-app.railway.app/api/health
```

Si responde, está funcionando correctamente.
