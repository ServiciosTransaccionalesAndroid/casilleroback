# 👥 API de Gestión de Empleados (Couriers)

## 📋 Endpoints Disponibles

### 1. Crear Empleado
```bash
POST /api/couriers
```

**Body:**
```json
{
  "employeeId": "COUR005",
  "name": "Pedro García",
  "phone": "+573401234567",
  "email": "pedro.garcia@servientrega.com",
  "pin": "7890"
}
```

**Respuesta:**
```json
{
  "id": 5,
  "employeeId": "COUR005",
  "name": "Pedro García",
  "phone": "+573401234567",
  "email": "pedro.garcia@servientrega.com",
  "active": true
}
```

---

### 2. Listar Todos los Empleados
```bash
GET /api/couriers
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "employeeId": "COUR001",
    "name": "Juan Pérez",
    "phone": "+573001234567",
    "email": "juan.perez@servientrega.com",
    "active": true
  },
  ...
]
```

---

### 3. Obtener Empleado por ID
```bash
GET /api/couriers/COUR001
```

**Respuesta:**
```json
{
  "id": 1,
  "employeeId": "COUR001",
  "name": "Juan Pérez",
  "phone": "+573001234567",
  "email": "juan.perez@servientrega.com",
  "active": true
}
```

---

### 4. Actualizar Empleado
```bash
PUT /api/couriers/COUR001
```

**Body (todos los campos son opcionales):**
```json
{
  "name": "Juan Pérez Actualizado",
  "phone": "+573009999999",
  "email": "juan.nuevo@servientrega.com",
  "pin": "4567",
  "active": false
}
```

**Respuesta:**
```json
{
  "id": 1,
  "employeeId": "COUR001",
  "name": "Juan Pérez Actualizado",
  "phone": "+573009999999",
  "email": "juan.nuevo@servientrega.com",
  "active": false
}
```

---

### 5. Eliminar Empleado
```bash
DELETE /api/couriers/COUR001
```

**Respuesta:** `204 No Content`

---

## 🧪 Ejemplos de Uso

### Crear empleado con PIN personalizado
```bash
curl -X POST http://localhost:8080/api/couriers \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "COUR005",
    "name": "Pedro García",
    "phone": "+573401234567",
    "email": "pedro.garcia@servientrega.com",
    "pin": "7890"
  }'
```

### Cambiar PIN de un empleado
```bash
curl -X PUT http://localhost:8080/api/couriers/COUR005 \
  -H "Content-Type: application/json" \
  -d '{
    "pin": "1111"
  }'
```

### Desactivar empleado
```bash
curl -X PUT http://localhost:8080/api/couriers/COUR005 \
  -H "Content-Type: application/json" \
  -d '{
    "active": false
  }'
```

### Probar login con nuevo empleado
```bash
curl -X POST http://localhost:8080/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "COUR005",
    "pin": "7890"
  }'
```

---

## ✅ Validaciones

### PIN
- Solo dígitos (0-9)
- Longitud: 4 a 6 dígitos
- Se hashea automáticamente con BCrypt

### Employee ID
- Requerido
- Único en el sistema

### Email
- Formato válido de email
- Opcional

### Nombre
- Requerido
- Mínimo 1 carácter

---

## 🔒 Seguridad

- El PIN nunca se devuelve en las respuestas
- Se hashea con BCrypt antes de guardar
- Cada vez que actualizas el PIN, se genera un nuevo hash

---

## 📝 Casos de Uso

### 1. Crear empleado con PIN único
```bash
POST /api/couriers
{
  "employeeId": "COUR010",
  "name": "Ana López",
  "pin": "2468"
}
```

### 2. Cambiar PIN olvidado
```bash
PUT /api/couriers/COUR010
{
  "pin": "1357"
}
```

### 3. Desactivar empleado que ya no trabaja
```bash
PUT /api/couriers/COUR010
{
  "active": false
}
```

### 4. Reactivar empleado
```bash
PUT /api/couriers/COUR010
{
  "active": true
}
```

---

## 🚀 Probar en Swagger

1. Ir a: `http://localhost:8080/swagger-ui.html`
2. Buscar sección **"Couriers"**
3. Probar cada endpoint

---

## ⚠️ Notas

- Los PINs se hashean automáticamente, no necesitas hacerlo manualmente
- No puedes crear dos empleados con el mismo `employeeId`
- Al actualizar, solo envía los campos que quieres cambiar
- El PIN debe cumplir el formato (4-6 dígitos) incluso al actualizar
