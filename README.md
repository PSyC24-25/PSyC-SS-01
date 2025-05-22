# Proyecto DoctorClick

Este es un proyecto que utiliza una base de datos y un servidor para gestionar la información. El siguiente README contiene las instrucciones necesarias para configurar la base de datos y ejecutar la aplicación.

## Configuración de la base de datos

A continuación, se detallan los comandos necesarios para configurar la base de datos:

1. **Eliminar el esquema y el usuario si existen:**

    ```sql
    DROP SCHEMA IF EXISTS doctorclick;
    DROP USER IF EXISTS 'spq'@'%';
    ```

2. **Crear un nuevo esquema (base de datos):**

    ```sql
    CREATE SCHEMA doctorclick;
    ```

3. **Crear un usuario con los privilegios necesarios:**

    ```sql
    CREATE USER 'spq'@'%' IDENTIFIED BY 'spq';
    GRANT ALL PRIVILEGES ON doctorclick.* TO 'spq'@'%' WITH GRANT OPTION;
    ```

Con estos pasos, habrás configurado correctamente la base de datos `doctorclick` y el usuario `spq` con los permisos necesarios.


## Ejecución del programa


Para ejecutar mediante Docker:

1. **Ejecución Docker:**
   
    ```bash
   docker-compose up --build
   ```


Para ejecutar los test:

1. **Test con Maven:**
   
   ```bash
   mvn test
   ```


Para ejecutar el proyecto, sigue los siguientes pasos:

1. **Compilar el proyecto con Maven:**

    ```bash
    mvn compile
    ```

2. **Iniciar el servidor Spring Boot (sin test):**

    ```bash
    mvn spring-boot:run -DskipTests
    ```

Esto iniciará el servidor y podrás acceder a la aplicación en el puerto configurado.
