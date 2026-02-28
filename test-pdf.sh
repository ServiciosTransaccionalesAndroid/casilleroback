#!/bin/bash
TOKEN=$(curl -s http://localhost:8090/api/auth/courier/login -H "Content-Type: application/json" -d '{"employeeId":"COUR001","pin":"1234"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)
curl -s "http://localhost:8090/api/reports/export/pdf?reportType=OCCUPANCY_RATE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o test_ocupacion.pdf
curl -s "http://localhost:8090/api/reports/export/csv?reportType=OCCUPANCY_RATE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o test_ocupacion.csv
ls -lh test_ocupacion.*
file test_ocupacion.*
echo "---CSV---"
cat test_ocupacion.csv
