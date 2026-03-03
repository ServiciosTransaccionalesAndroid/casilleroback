#!/bin/bash
# Script para generar hashes BCrypt de PINs

echo "Generando hashes BCrypt para PINs..."
echo ""

# Necesitamos usar htpasswd o un script Java
# Vamos a crear un pequeño programa Java

cat > PinHashGenerator.java << 'JAVA'
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PinHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("PIN 1234: " + encoder.encode("1234"));
        System.out.println("PIN 5678: " + encoder.encode("5678"));
        System.out.println("PIN 9012: " + encoder.encode("9012"));
        System.out.println("PIN 123456: " + encoder.encode("123456"));
    }
}
JAVA

echo "Archivo PinHashGenerator.java creado"
