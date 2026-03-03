# 🔐 Cambios en Autenticación - Endpoints Protegidos

## Resumen de Cambios

Se actualizó la seguridad para proteger endpoints administrativos. Ahora requieren token JWT.

---

## ✅ Endpoints PÚBLICOS (Sin autenticación)

| Endpoint | Descripción | Uso |
|----------|-------------|-----|
| `POST /api/auth/admin/login` | Login administrador | Portal web |
| `POST /api/auth/courier/login` | Login mensajero | App móvil |
| `GET /api/health` | Health check | Monitoreo |
| `GET /api/qr/**` | Códigos QR | Cliente final |
| `GET /api/retrievals/validate?code={code}` | Validar código retiro | Locker físico |
| `POST /api/retrievals` | Registrar retiro | Locker físico |
| `GET /swagger-ui/**` | Documentación API | Desarrollo |

---

## 🔒 Endpoints PROTEGIDOS (Requieren JWT)

### Gestión de Empleados
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/couriers` | GET | Listar empleados |
| `/api/couriers` | POST | Crear empleado |
| `/api/couriers/{id}` | PUT | Actualizar empleado |
| `/api/couriers/{id}` | DELETE | Eliminar empleado |

### Gestión de Clientes
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/recipients` | GET | Listar clientes |
| `/api/recipients` | POST | Crear cliente |
| `/api/recipients/{id}` | PUT | Actualizar cliente |
| `/api/recipients/{id}` | DELETE | Eliminar cliente |

### Gestión de Paquetes
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/packages` | GET | Listar paquetes |
| `/api/packages` | POST | Crear paquete |
| `/api/packages/{id}` | GET | Ver paquete |
| `/api/packages/{id}` | PUT | Actualizar paquete |
| `/api/packages/{id}` | DELETE | Eliminar paquete |
| `/api/packages/validate` | GET | Validar tracking |

### Gestión de Lockers
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/lockers` | GET | Listar lockers |
| `/api/lockers/{id}` | GET | Ver locker |
| `/api/lockers/{id}/compartments` | GET | Ver compartimentos |
| `/api/lockers/{id}/status` | GET | Ver estado |
| `/api/lockers/status-update` | POST | Actualizar estado |

### Depósitos
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
- [ ] Agregar token a todas las llamadas de couriers
- [ ] Agregar token a todas las llamadas de recipients
- [ ] Agregar token a todas las llamadas de packages
- [ ] Agregar token a todas las llamadas de lockers
- [ ] Agregar token a todas las llamadas de deposits

### App Móvil Courier
- [ ] Login funciona
- [ ] Agregar token a depósitos
- [ ] Agregar token a actualización de estado

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
