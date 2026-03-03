# 🔐 Credenciales de Prueba - Desarrollo

## Mensajeros (Couriers)

### COUR001 - Juan Pérez
- **Employee ID:** `COUR001`
- **PIN:** `1234`
- **Email:** juan.perez@servientrega.com
- **Teléfono:** +573001234567

### COUR002 - María González
- **Employee ID:** `COUR002`
- **PIN:** `5678`
- **Email:** maria.gonzalez@servientrega.com
- **Teléfono:** +573007654321

### COUR003 - Luis Ramírez
- **Employee ID:** `COUR003`
- **PIN:** `9012`
- **Email:** luis.ramirez@servientrega.com
- **Teléfono:** +573201234567

### COUR004 - Sandra López
- **Employee ID:** `COUR004`
- **PIN:** `123456`
- **Email:** sandra.lopez@servientrega.com
- **Teléfono:** +573301234567

---

## Validación de PIN

- **Formato:** Solo dígitos numéricos
- **Longitud:** 4 a 6 dígitos
- **Ejemplos válidos:** `1234`, `5678`, `123456`
- **Ejemplos inválidos:** `abc`, `12`, `1234567`, `12ab`

---

## Ejemplo de Login (cURL)

```bash
curl -X POST http://localhost:8080/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "COUR001",
    "pin": "1234"
  }'
```

## Ejemplo de Login (Swagger)

1. Ir a: http://localhost:8080/swagger-ui.html
2. Buscar: `POST /api/auth/courier/login`
3. Click en "Try it out"
4. Ingresar:
   ```json
   {
     "employeeId": "COUR001",
     "pin": "1234"
   }
   ```
5. Click en "Execute"

---

## ⚠️ IMPORTANTE

**Estas credenciales son SOLO para desarrollo y pruebas.**

En producción:
- Cada mensajero debe tener su propio PIN único
- Los PINs deben ser generados de forma segura
- Implementar política de cambio de PIN periódico
- Considerar autenticación de dos factores (2FA)

---

## Generar Nuevos Hashes BCrypt

Si necesitas agregar más mensajeros, puedes generar hashes usando:

### Opción 1: Online (desarrollo)
https://bcrypt-generator.com/
- Rounds: 10

### Opción 2: Código Java
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode("tu-pin-aqui");
System.out.println(hash);
```

### Opción 3: Docker
```bash
docker run --rm -it alpine/openssl passwd -1 tu-pin-aqui
```
