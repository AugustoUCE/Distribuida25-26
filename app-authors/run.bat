@echo off
setlocal enabledelayedexpansion

echo ==============================================
echo      INICIADOR DE INSTANCIAS QUARKUS
echo ==============================================
echo.

set /p INSTANCIAS=¿Cuantas instancias quieres levantar?:
set /p PUERTO_INICIAL=¿Puerto inicial (ej: 8070)?:

echo.
echo Levantando !INSTANCIAS! instancias comenzando desde el puerto !PUERTO_INICIAL!...
echo.

for /L %%i in (1,1,%INSTANCIAS%) do (
    set /a PORT=!PUERTO_INICIAL! + %%i - 1

    echo Iniciando instancia %%i en puerto !PORT!...
    start cmd /k "java -Dquarkus.http.port=!PORT! -jar build\quarkus-app\quarkus-run.jar"
)

echo.
echo ==============================================
echo      Todas las instancias fueron iniciadas.
echo ==============================================
pause
