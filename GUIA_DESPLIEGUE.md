# Guía de Despliegue - Backend Casilleros Servientrega

## Tabla de Contenidos
0. [Requisitos Previos del Proyecto](#requisitos-previos-del-proyecto)
1. [Opciones de Despliegue](#opciones-de-despliegue)
2. [Despliegue en VPS/Servidor Dedicado](#despliegue-en-vpsservidor-dedicado)
3. [Despliegue en AWS](#despliegue-en-aws)
4. [Despliegue con Docker Compose](#despliegue-con-docker-compose)
5. [Configuración de Dominio y SSL](#configuración-de-dominio-y-ssl)
6. [Monitoreo y Logs](#monitoreo-y-logs)

---

## Requisitos Previos del Proyecto

### Archivos Necesarios en el Repositorio

Antes de desplegar, asegúrate de que tu proyecto tenga estos archivos:

#### 1. **Dockerfile** (Obligatorio)
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2. **docker-compose.yml** (Obligatorio)
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: locker-postgres
    environment:
      POSTGRES_DB: locker_db
      POSTGRES_USER: locker_user
      POSTGRES_PASSWORD: locker_pass
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    networks:
      - locker-network

  backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: locker-backend
    ports:
      - "8090:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/locker_db
      SPRING_DATASOURCE_USERNAME: locker_user
      SPRING_DATASOURCE_PASSWORD: locker_pass
    depends_on:
      - postgres
    networks:
      - locker-network

volumes:
  postgres_data:

networks:
  locker-network:
    driver: bridge
```

#### 3. **pom.xml** (Obligatorio)
Debe incluir:
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL Driver
- Spring Security (si usas JWT)
- Flyway (para migraciones)

#### 4. **application.yml / application.properties** (Obligatorio)
```yaml
spring:
  application:
    name: locker-backend
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/locker_db}
    username: ${SPRING_DATASOURCE_USERNAME:locker_user}
    password: ${SPRING_DATASOURCE_PASSWORD:locker_pass}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
    baseline-on-migrate: true

server:
  port: 8080
```

#### 5. **Migraciones Flyway** (Obligatorio)
```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__seed_data.sql
└── V3__add_indexes.sql
```

#### 6. **.gitignore** (Recomendado)
```
target/
*.jar
*.war
*.class
.env
.env.production
*.log
.DS_Store
```

#### 7. **README.md** (Recomendado)
Con instrucciones de:
- Cómo ejecutar localmente
- Variables de entorno necesarias
- Endpoints principales
- Documentación de API

### Configuraciones de Producción Necesarias

#### 1. **application-prod.yml**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
  jpa:
    show-sql: false
    properties:
      hibernate:
        format_sql: false

logging:
  level:
    root: INFO
    com.servientrega: INFO
  file:
    name: /var/log/locker-backend/application.log

server:
  port: 8080
  compression:
    enabled: true
  error:
    include-message: never
    include-stacktrace: never
```

#### 2. **Variables de Entorno Requeridas**
```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/locker_db
SPRING_DATASOURCE_USERNAME=locker_user
SPRING_DATASOURCE_PASSWORD=password_seguro

# JWT (si aplica)
JWT_SECRET=clave_secreta_minimo_256_bits
JWT_EXPIRATION=86400000

# Perfil activo
SPRING_PROFILES_ACTIVE=prod
```

### Servicios Externos Necesarios

#### 1. **Base de Datos PostgreSQL**
- Versión: 15+
- Espacio: Mínimo 5GB
- Conexiones: Mínimo 20 conexiones simultáneas

#### 2. **Servicio de SMS** (Opcional)
Para notificaciones:
- Twilio
- AWS SNS
- Vonage (Nexmo)

#### 3. **Servicio de Email** (Opcional)
Para notificaciones:
- SendGrid
- AWS SES
- Mailgun

#### 4. **Dominio y DNS**
- Dominio registrado (ej: tudominio.com)
- Acceso a configuración DNS
- Registro A apuntando a IP del servidor

#### 5. **Certificado SSL**
- Let's Encrypt (gratuito)
- O certificado comercial

### Checklist Pre-Despliegue

- [ ] Dockerfile creado y probado localmente
- [ ] docker-compose.yml funciona en local
- [ ] Migraciones Flyway ejecutan correctamente
- [ ] Variables de entorno documentadas
- [ ] application-prod.yml configurado
- [ ] Endpoints de health check implementados
- [ ] Logs configurados correctamente
- [ ] Contraseñas de producción generadas
- [ ] Dominio registrado y DNS configurado
- [ ] Backup strategy definida
- [ ] Plan de rollback documentado

---

## Opciones de Despliegue

### 1. VPS/Servidor Dedicado (Recomendado para empezar)
- **Proveedores**: DigitalOcean, Linode, Vultr, AWS EC2, Google Cloud Compute
- **Costo**: $5-20/mes
- **Ventajas**: Control total, fácil de configurar
- **Ideal para**: Desarrollo, staging, producción pequeña/mediana

### 2. AWS (Escalable)
- **Servicios**: ECS/Fargate, RDS, ALB
- **Costo**: Variable según uso
- **Ventajas**: Auto-escalado, alta disponibilidad
- **Ideal para**: Producción grande, alta demanda

### 3. Kubernetes (Avanzado)
- **Proveedores**: AWS EKS, Google GKE, Azure AKS
- **Costo**: $70+/mes
- **Ventajas**: Orquestación avanzada, multi-cloud
- **Ideal para**: Empresas grandes, microservicios complejos

---

## Despliegue en VPS/Servidor Dedicado

### Requisitos del Servidor
```
- Sistema Operativo: Ubuntu 22.04 LTS
- RAM: Mínimo 2GB (Recomendado 4GB)
- CPU: 2 vCPUs
- Disco: 20GB SSD
- Puertos: 80, 443, 8090, 5432
```

### Paso 1: Preparar el Servidor

```bash
# Conectar al servidor
ssh root@tu-servidor-ip

# Actualizar sistema
apt update && apt upgrade -y

# Instalar dependencias
apt install -y git curl wget ufw

# Configurar firewall
ufw allow 22/tcp    # SSH
ufw allow 80/tcp    # HTTP
ufw allow 443/tcp   # HTTPS
ufw allow 8090/tcp  # Backend (temporal, luego usar Nginx)
ufw enable
```

### Paso 2: Instalar Docker y Docker Compose

```bash
# Instalar Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Instalar Docker Compose
apt install -y docker-compose-plugin

# Verificar instalación
docker --version
docker compose version

# Habilitar Docker al inicio
systemctl enable docker
systemctl start docker
```

### Paso 2.1: Preparar Imagen Docker (Opción A: Build en Servidor)

```bash
# El servidor construirá la imagen desde el código fuente
# Requiere: Dockerfile en el repositorio
# Ventaja: No necesitas Docker Hub
# Desventaja: Compilación lenta en el servidor

# Esto se hace automáticamente con:
docker compose -f docker-compose.prod.yml up -d --build
```

### Paso 2.2: Preparar Imagen Docker (Opción B: Usar Docker Hub)

#### En tu máquina local:

```bash
# 1. Construir imagen
cd /home/josesilva/casilleroback
docker build -t tu-usuario/casilleros-backend:latest .

# 2. Probar imagen localmente
docker run -p 8090:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/locker_db \
  tu-usuario/casilleros-backend:latest

# 3. Login a Docker Hub
docker login

# 4. Subir imagen
docker push tu-usuario/casilleros-backend:latest

# 5. (Opcional) Crear tag de versión
docker tag tu-usuario/casilleros-backend:latest tu-usuario/casilleros-backend:v1.0.0
docker push tu-usuario/casilleros-backend:v1.0.0
```

#### En el servidor:

```bash
# Modificar docker-compose.prod.yml para usar imagen de Docker Hub
# Cambiar:
#   build:
#     context: .
#     dockerfile: Dockerfile
# Por:
#   image: tu-usuario/casilleros-backend:latest

# Descargar imagen
docker pull tu-usuario/casilleros-backend:latest

# Iniciar servicios
docker compose -f docker-compose.prod.yml up -d
```

### Paso 2.3: Preparar Imagen Docker (Opción C: Usar Registry Privado)

#### Crear registry privado en el servidor:

```bash
# Instalar registry
docker run -d -p 5000:5000 --restart=always --name registry registry:2

# En tu máquina local, construir y subir
docker build -t localhost:5000/casilleros-backend:latest .
docker push localhost:5000/casilleros-backend:latest

# En el servidor, usar:
# image: localhost:5000/casilleros-backend:latest
```

### Comparación de Opciones

| Opción | Ventajas | Desventajas | Recomendado para |
|--------|----------|-------------|------------------|
| **A: Build en Servidor** | Simple, no requiere registry | Lento, consume recursos del servidor | Desarrollo, proyectos pequeños |
| **B: Docker Hub** | Rápido, fácil de usar, gratis (público) | Imagen pública (o $5/mes privado) | Producción, equipos pequeños |
| **C: Registry Privado** | Control total, privado, gratis | Requiere mantenimiento | Empresas, múltiples servicios |

### Paso 3: Clonar el Proyecto

```bash
# Crear directorio para aplicaciones
mkdir -p /opt/apps
cd /opt/apps

# Clonar repositorio
git clone https://github.com/tu-usuario/casilleroback.git
cd casilleroback
```

### Paso 4: Configurar Variables de Entorno

```bash
# Crear archivo de producción
nano .env.production
```

Contenido del archivo `.env.production`:
```env
# Base de datos
POSTGRES_DB=locker_db
POSTGRES_USER=locker_user
POSTGRES_PASSWORD=TU_PASSWORD_SEGURO_AQUI_123!@#
POSTGRES_HOST=postgres
POSTGRES_PORT=5432

# Spring Boot
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8090

# JWT
JWT_SECRET=TU_SECRET_KEY_MUY_LARGO_Y_SEGURO_MINIMO_256_BITS_AQUI
JWT_EXPIRATION=86400000

# Notificaciones (configurar con servicios reales)
SMS_API_KEY=tu_api_key_sms
EMAIL_API_KEY=tu_api_key_email
```

### Paso 5: Crear docker-compose.prod.yml

```bash
nano docker-compose.prod.yml
```

#### Opción A: Con build en servidor (imagen local)
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: locker-postgres-prod
    restart: always
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - locker-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER}"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: locker-backend-prod
    restart: always
    ports:
      - "8090:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION}
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - locker-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
    driver: local

networks:
  locker-network:
    driver: bridge
```

#### Opción B: Con imagen de Docker Hub
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: locker-postgres-prod
    restart: always
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - locker-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER}"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    image: tu-usuario/casilleros-backend:latest  # <-- Cambio aquí
    container_name: locker-backend-prod
    restart: always
    ports:
      - "8090:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION}
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - locker-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
    driver: local

networks:
  locker-network:
    driver: bridge
```

### Paso 6: Iniciar Servicios

```bash
# Cargar variables de entorno
export $(cat .env.production | xargs)

# Iniciar servicios
docker compose -f docker-compose.prod.yml up -d --build

# Ver logs
docker compose -f docker-compose.prod.yml logs -f

# Verificar estado
docker compose -f docker-compose.prod.yml ps
```

### Paso 7: Verificar Funcionamiento

```bash
# Health check
curl http://localhost:8090/api/health

# Verificar base de datos
docker exec locker-postgres-prod psql -U locker_user -d locker_db -c "\dt"
```

---

## Configuración de Nginx como Reverse Proxy

### Instalar Nginx

```bash
apt install -y nginx
```

### Configurar Nginx

```bash
nano /etc/nginx/sites-available/casilleros
```

```nginx
server {
    listen 80;
    server_name api.tudominio.com;

    # Logs
    access_log /var/log/nginx/casilleros-access.log;
    error_log /var/log/nginx/casilleros-error.log;

    # Tamaño máximo de archivos
    client_max_body_size 10M;

    # Proxy al backend
    location / {
        proxy_pass http://localhost:8090;
        proxy_http_version 1.1;
        
        # Headers
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Health check público
    location /api/health {
        proxy_pass http://localhost:8090/api/health;
        access_log off;
    }
}
```

### Activar configuración

```bash
# Crear enlace simbólico
ln -s /etc/nginx/sites-available/casilleros /etc/nginx/sites-enabled/

# Verificar configuración
nginx -t

# Reiniciar Nginx
systemctl restart nginx
systemctl enable nginx
```

---

## Configuración de SSL con Let's Encrypt

### Instalar Certbot

```bash
apt install -y certbot python3-certbot-nginx
```

### Obtener certificado SSL

```bash
# Obtener certificado (reemplazar con tu dominio)
certbot --nginx -d api.tudominio.com

# Renovación automática (ya configurada por defecto)
certbot renew --dry-run
```

### Verificar SSL

```bash
# Verificar certificado
curl https://api.tudominio.com/api/health
```

---

## Despliegue en AWS

### Opción 1: EC2 + RDS (Más simple)

#### 1. Crear instancia EC2
```
- AMI: Ubuntu 22.04 LTS
- Tipo: t3.medium (2 vCPU, 4GB RAM)
- Storage: 20GB gp3
- Security Group: 
  - SSH (22) desde tu IP
  - HTTP (80) desde 0.0.0.0/0
  - HTTPS (443) desde 0.0.0.0/0
```

#### 2. Crear RDS PostgreSQL
```
- Engine: PostgreSQL 15
- Instancia: db.t3.micro
- Storage: 20GB gp3
- Multi-AZ: No (para desarrollo)
- Public access: No
- VPC: Mismo que EC2
```

#### 3. Configurar aplicación
```bash
# En EC2, modificar docker-compose.prod.yml
# Cambiar postgres service por conexión a RDS:

environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://tu-rds-endpoint:5432/locker_db
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: tu_password_rds
```

### Opción 2: ECS Fargate (Serverless)

#### 1. Crear repositorio ECR
```bash
# Crear repositorio
aws ecr create-repository --repository-name casilleros-backend

# Login a ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  123456789.dkr.ecr.us-east-1.amazonaws.com

# Build y push
docker build -t casilleros-backend .
docker tag casilleros-backend:latest \
  123456789.dkr.ecr.us-east-1.amazonaws.com/casilleros-backend:latest
docker push 123456789.dkr.ecr.us-east-1.amazonaws.com/casilleros-backend:latest
```

#### 2. Crear Task Definition
```json
{
  "family": "casilleros-backend",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [
    {
      "name": "backend",
      "image": "123456789.dkr.ecr.us-east-1.amazonaws.com/casilleros-backend:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        },
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://rds-endpoint:5432/locker_db"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/casilleros-backend",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

#### 3. Crear ECS Service con ALB
```bash
# Crear cluster
aws ecs create-cluster --cluster-name casilleros-cluster

# Crear service (usar consola AWS para configurar ALB)
```

---

## Monitoreo y Logs

### 1. Logs con Docker

```bash
# Ver logs en tiempo real
docker compose -f docker-compose.prod.yml logs -f backend

# Ver últimas 100 líneas
docker compose -f docker-compose.prod.yml logs --tail=100 backend

# Logs de PostgreSQL
docker compose -f docker-compose.prod.yml logs -f postgres
```

### 2. Configurar Logrotate

```bash
nano /etc/logrotate.d/casilleros
```

```
/var/log/nginx/casilleros-*.log {
    daily
    rotate 14
    compress
    delaycompress
    notifempty
    create 0640 www-data adm
    sharedscripts
    postrotate
        systemctl reload nginx > /dev/null 2>&1
    endscript
}
```

### 3. Monitoreo con Prometheus + Grafana (Opcional)

```yaml
# Agregar a docker-compose.prod.yml

  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    ports:
      - "9090:9090"
    networks:
      - locker-network

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana
    networks:
      - locker-network
```

---

## Backup y Recuperación

### Backup Automático de PostgreSQL

```bash
# Crear script de backup
nano /opt/scripts/backup-postgres.sh
```

```bash
#!/bin/bash
BACKUP_DIR="/opt/backups/postgres"
DATE=$(date +%Y%m%d_%H%M%S)
CONTAINER="locker-postgres-prod"

mkdir -p $BACKUP_DIR

docker exec $CONTAINER pg_dump -U locker_user locker_db | \
  gzip > $BACKUP_DIR/backup_$DATE.sql.gz

# Mantener solo últimos 7 días
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete

echo "Backup completado: backup_$DATE.sql.gz"
```

```bash
# Dar permisos
chmod +x /opt/scripts/backup-postgres.sh

# Configurar cron (diario a las 2 AM)
crontab -e
```

```cron
0 2 * * * /opt/scripts/backup-postgres.sh >> /var/log/backup-postgres.log 2>&1
```

### Restaurar Backup

```bash
# Restaurar desde backup
gunzip -c /opt/backups/postgres/backup_20260217_020000.sql.gz | \
  docker exec -i locker-postgres-prod psql -U locker_user -d locker_db
```

---

## Actualización de la Aplicación

### Actualización Manual

```bash
cd /opt/apps/casilleroback

# Hacer backup antes de actualizar
/opt/scripts/backup-postgres.sh

# Obtener últimos cambios
git pull origin main

# Reconstruir y reiniciar
docker compose -f docker-compose.prod.yml up -d --build

# Verificar logs
docker compose -f docker-compose.prod.yml logs -f backend
```

### CI/CD con GitHub Actions

Crear `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Deploy to server
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd /opt/apps/casilleroback
            git pull origin main
            docker compose -f docker-compose.prod.yml up -d --build
```

---

## Checklist de Seguridad

- [ ] Cambiar contraseñas por defecto
- [ ] Configurar firewall (UFW)
- [ ] Habilitar SSL/TLS (Let's Encrypt)
- [ ] Configurar JWT con secret seguro
- [ ] Deshabilitar acceso root SSH
- [ ] Configurar fail2ban
- [ ] Actualizar sistema regularmente
- [ ] Configurar backups automáticos
- [ ] Limitar acceso a base de datos
- [ ] Configurar rate limiting en Nginx
- [ ] Habilitar logs de auditoría
- [ ] Configurar monitoreo de recursos

---

## Comandos Útiles

```bash
# Ver estado de servicios
docker compose -f docker-compose.prod.yml ps

# Reiniciar backend
docker compose -f docker-compose.prod.yml restart backend

# Ver uso de recursos
docker stats

# Limpiar recursos no usados
docker system prune -a

# Ver logs de Nginx
tail -f /var/log/nginx/casilleros-access.log

# Verificar certificado SSL
openssl s_client -connect api.tudominio.com:443 -servername api.tudominio.com

# Verificar puertos abiertos
netstat -tulpn | grep LISTEN
```

---

## Costos Estimados

### VPS (DigitalOcean/Linode)
- Droplet 2GB RAM: $12/mes
- Dominio: $10-15/año
- Total: ~$13/mes

### AWS (Pequeña escala)
- EC2 t3.medium: $30/mes
- RDS db.t3.micro: $15/mes
- ALB: $16/mes
- Data transfer: $5-10/mes
- Total: ~$70/mes

### AWS (Producción con ECS)
- ECS Fargate: $30-50/mes
- RDS db.t3.small: $30/mes
- ALB: $16/mes
- CloudWatch: $5/mes
- Total: ~$85/mes

---

## Soporte y Troubleshooting

### Problema: Backend no inicia
```bash
# Ver logs detallados
docker compose -f docker-compose.prod.yml logs backend

# Verificar variables de entorno
docker compose -f docker-compose.prod.yml config

# Verificar conectividad a BD
docker exec locker-backend-prod nc -zv postgres 5432
```

### Problema: Base de datos no conecta
```bash
# Verificar PostgreSQL
docker exec locker-postgres-prod pg_isready -U locker_user

# Ver logs de PostgreSQL
docker compose -f docker-compose.prod.yml logs postgres

# Conectar manualmente
docker exec -it locker-postgres-prod psql -U locker_user -d locker_db
```

### Problema: Alto uso de memoria
```bash
# Ver uso de recursos
docker stats

# Ajustar límites en docker-compose.prod.yml
services:
  backend:
    deploy:
      resources:
        limits:
          memory: 1G
        reservations:
          memory: 512M
```

---

## Contacto y Documentación

- **Documentación API**: https://api.tudominio.com/swagger-ui.html
- **Repositorio**: https://github.com/tu-usuario/casilleroback
- **Guía de API**: Ver `GUIA_IMPLEMENTACION_API.md`
- **Plan de Implementación**: Ver `PLAN_IMPLEMENTACION.md`
