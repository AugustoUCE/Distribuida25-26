@echo off
echo ==============================================
echo    FINALIZANDO INSTANCIAS DE QUARKUS/JAVA
echo ==============================================

taskkill /F /IM java.exe

echo.
echo Todas las instancias Java fueron finalizadas.
echo ==============================================
pause