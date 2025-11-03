#!/bin/bash

# Script para verificar que las pruebas de rendimiento están configuradas correctamente

echo "=========================================="
echo "Verificación de Pruebas de Rendimiento"
echo "=========================================="

# Verificar que Python está instalado
if command -v python3 &> /dev/null; then
    echo "✅ Python 3 está instalado"
    python3 --version
else
    echo "❌ Python 3 no está instalado"
    exit 1
fi

# Verificar que Locust está disponible
if command -v locust &> /dev/null; then
    echo "✅ Locust está instalado"
    locust --version
else
    echo "⚠️  Locust no está instalado, instalando..."
    if [ -d "venv" ]; then
        source venv/bin/activate
    else
        python3 -m venv venv
        source venv/bin/activate
    fi
    pip install -r requirements.txt
fi

# Verificar que locustfile.py existe
if [ -f "locustfile.py" ]; then
    echo "✅ locustfile.py existe"
    echo "   Archivo: $(pwd)/locustfile.py"
else
    echo "❌ locustfile.py no encontrado"
    exit 1
fi

# Verificar que requirements.txt existe
if [ -f "requirements.txt" ]; then
    echo "✅ requirements.txt existe"
    echo "   Contenido:"
    cat requirements.txt
else
    echo "❌ requirements.txt no encontrado"
    exit 1
fi

# Verificar sintaxis de locustfile.py
echo ""
echo "Verificando sintaxis de locustfile.py..."
python3 -m py_compile locustfile.py
if [ $? -eq 0 ]; then
    echo "✅ Sintaxis de locustfile.py es válida"
else
    echo "❌ Error en sintaxis de locustfile.py"
    exit 1
fi

echo ""
echo "=========================================="
echo "✅ Todas las verificaciones pasaron"
echo "=========================================="
echo ""
echo "Para ejecutar las pruebas:"
echo "  ./run-locust.sh ECommerceUser 10 2 60"
echo ""

