#(Encender servidor bd)

#COMANDOS PARA GESTIONAR LA BASE DE DATOS

DROP SCHEMA IF exists doctorclick;

create schema doctorclick;

DROP USER IF EXISTS 'spq'@'%';

CREATE USER 'spq'@'%' IDENTIFIED BY 'spq';

GRANT ALL PRIVILEGES ON doctorclick.* TO 'spq'@'%' WITH GRANT OPTION;

#(Ejecución del programa)

mvn compile

mvn spring-boot:run
