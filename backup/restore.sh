#!/bin/bash

# Script para restaurar backup de base de datos
# Uso: ./restore.sh <host> <port> <database> <username>
# Ejemplo: ./restore.sh localhost 5432 locker_db locker_user

if [ "$#" -ne 4 ]; then
    echo "Uso: $0 <host> <port> <database> <username>"
    echo "Ejemplo: $0 localhost 5432 locker_db locker_user"
    exit 1
fi

HOST=$1
PORT=$2
DATABASE=$3
USERNAME=$4

echo "Restaurando backup en:"
echo "  Host: $HOST"
echo "  Port: $PORT"
echo "  Database: $DATABASE"
echo "  Username: $USERNAME"
echo ""

read -sp "Password: " PASSWORD
echo ""

export PGPASSWORD=$PASSWORD

# Orden de restauración respetando dependencias
TABLES=(
    "admins"
    "lockers"
    "compartments"
    "recipients"
    "packages"
    "couriers"
    "deposits"
    "retrieval_codes"
    "retrievals"
    "alerts"
    "operation_logs"
    "status_history"
)

for table in "${TABLES[@]}"; do
    if [ -f "${table}.csv" ]; then
        echo "Restaurando tabla: $table"
        psql -h $HOST -p $PORT -U $USERNAME -d $DATABASE -c "\copy $table FROM '${table}.csv' CSV HEADER" 2>&1
        if [ $? -eq 0 ]; then
            echo "✓ $table restaurada"
        else
            echo "✗ Error restaurando $table"
        fi
    else
        echo "⚠ Archivo ${table}.csv no encontrado"
    fi
done

# Actualizar secuencias
echo ""
echo "Actualizando secuencias..."
psql -h $HOST -p $PORT -U $USERNAME -d $DATABASE << 'EOF'
SELECT setval('admins_id_seq', COALESCE((SELECT MAX(id) FROM admins), 1));
SELECT setval('alerts_id_seq', COALESCE((SELECT MAX(id) FROM alerts), 1));
SELECT setval('compartments_id_seq', COALESCE((SELECT MAX(id) FROM compartments), 1));
SELECT setval('couriers_id_seq', COALESCE((SELECT MAX(id) FROM couriers), 1));
SELECT setval('deposits_id_seq', COALESCE((SELECT MAX(id) FROM deposits), 1));
SELECT setval('lockers_id_seq', COALESCE((SELECT MAX(id) FROM lockers), 1));
SELECT setval('operation_logs_id_seq', COALESCE((SELECT MAX(id) FROM operation_logs), 1));
SELECT setval('packages_id_seq', COALESCE((SELECT MAX(id) FROM packages), 1));
SELECT setval('recipients_id_seq', COALESCE((SELECT MAX(id) FROM recipients), 1));
SELECT setval('retrieval_codes_id_seq', COALESCE((SELECT MAX(id) FROM retrieval_codes), 1));
SELECT setval('retrievals_id_seq', COALESCE((SELECT MAX(id) FROM retrievals), 1));
SELECT setval('status_history_id_seq', COALESCE((SELECT MAX(id) FROM status_history), 1));
EOF

echo ""
echo "✓ Restauración completada"
