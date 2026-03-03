# 🔐 Cambios en Autenticación - Endpoints Protegidos

## Resumen de Cambios

Se actualizó la seguridad para proteger endpoints administrativos. Ahora requieren token JWT.

---

## ✅ Endpoints PÚBLICOS (Sin autenticación)

| Endpoint | Descripción | Usado por |
|----------|-------------|----------|
| `POST /api/auth/admin/login` | Login administrador | Portal web |
| `POST /api/auth/courier/login` | Login mensajero | App móvil |
| `GET /api/health` | Health check | Monitoreo |
| `GET /api/qr/**` | Códigos QR | Cliente final |
| `GET /api/packages/validate?trackingNumber={number}` | Validar paquete | App móvil courier |
| `POST /api/deposits` | Registrar depósito | App móvil courier |
| `GET /api/retrievals/validate?code={code}` | Validar código retiro | Locker físico |
| `POST /api/retrievals` | Registrar retiro | Locker físico |
| `POST /api/lockers/status-update` | Actualizar estado locker | Locker físico (hardware) |
| `GET /swagger-ui/**` | Documentación API | Desarrollo |

---

## 🔒 Endpoints PROTEGIDOS (Requieren JWT)

### Solo ADMIN
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/couriers/**` | ALL | Gestión empleados |
| `/api/recipients/**` | ALL | Gestión clientes |
| `/api/packages/**` | ALL | Gestión paquetes (CRUD) |
| `/api/lockers/**` | ALL | Gestión lockers |
| `/api/util/**` | ALL | Utilidades desarrollo |

### ADMIN + COURIER
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/deposits` | POST | Registrar depósito |

### Utilidades (Solo desarrollo)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/util/hash` | GET | Generar hash BCrypt |

---

## 🔑 Cómo Usar Autenticación

### 1. Obtener Token

**Admin:**
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@servientrega.com","password":"Admin123!"}'
```

**Courier:**
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId":"COUR001","pin":"1234"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@servientrega.com",
  "name": "Administrador Principal",
  "role": "ADMIN",
  "message": "Login successful"
}
```

### 2. Usar Token en Requests

```bash
curl https://casilleroback-production.up.railway.app/api/couriers \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. En JavaScript/Nuxt

```typescript
const { api } = useApi()

// El composable useApi ya incluye el token automáticamente
const couriers = await api('/api/couriers')
```

---

## 📋 Checklist de Migración

### Portal Admin (Nuxt)
- [x] Login funciona
- [ ] Token incluido en todas las llamadas (composable useApi)

### App Móvil Courier
- [ ] Login courier funciona
- [ ] Token incluido en POST /api/deposits

### Locker Físico
- ✅ No requiere cambios (endpoints públicos)

---

## ⚠️ Errores Comunes

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Unauthorized"
}
```
**Solución:** Incluir header `Authorization: Bearer {token}`

### 403 Forbidden
```json
{
  "status": 403,
  "message": "Access Denied"
}
```
**Solución:** Token inválido o expirado. Hacer login nuevamente.

---

## 🔄 Endpoints que NO Cambiaron

Estos endpoints siempre fueron públicos y siguen igual:
- `/api/auth/**` - Login
- `/api/health` - Health check
- `/api/qr/**` - Códigos QR
- `/api/retrievals/validate` - Validar código
- `/api/retrievals` - Registrar retiro

---

## 📅 Fecha de Cambio

**Implementado:** 2026-03-03

**Versión:** 1.1.0
