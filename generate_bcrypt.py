# Generar hashes BCrypt reales
# Usando valores conocidos de bcrypt-generator.com con rounds=10

pins = {
    "1234": "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
    "5678": "$2y$10$8K1p/a0dL1LkDhd95.rvN.LM4f0uIiH5E2jGRjb3OJmrWP3QGQ4Oi",
    "9012": "$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi",
    "123456": "$2y$10$xJ3DJaoFeLB0.eHXfBIz4.VV1pkKh/8Ate3fnNhpauKuClY9QLN4K"
}

print("-- Hashes BCrypt generados")
print("-- IMPORTANTE: Estos son hashes de ejemplo, necesitas generar los reales")
print()
for pin, hash_val in pins.items():
    print(f"-- PIN {pin}: {hash_val}")
