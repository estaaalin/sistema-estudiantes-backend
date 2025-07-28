# Script para instalar Maven y compilar el proyecto

Write-Host "🔧 Instalando Maven..." -ForegroundColor Yellow

# Verificar si chocolatey está instalado
$chocoInstalled = Get-Command choco -ErrorAction SilentlyContinue

if (-not $chocoInstalled) {
    Write-Host "📦 Instalando Chocolatey..." -ForegroundColor Cyan
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
    refreshenv
}

# Instalar Maven
Write-Host "📦 Instalando Maven..." -ForegroundColor Cyan
choco install maven -y
refreshenv

# Compilar el proyecto
Write-Host "🏗️ Compilando el proyecto..." -ForegroundColor Green
cd "C:\Users\fabri\Documents\Visual Studio 2022\Proyectos Visual Studio\6A\EXAMEN\Backend"
mvn clean compile

Write-Host "✅ Proceso completado!" -ForegroundColor Green
