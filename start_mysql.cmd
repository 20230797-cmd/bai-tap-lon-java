@echo off
title MySQL Server 8.4 - QL Canh Bao Hoc Vu
echo ========================================================
echo   KHOI DONG MYSQL SERVER 8.4 (PORT 3306)
echo ========================================================
echo Dang khoi dong MySQL Server tu C:\Program Files\MySQL\MySQL Server 8.4...
cd /d C:\ProgramData\MySQL
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe" --defaults-file=C:\ProgramData\MySQL\my.ini --console
pause
