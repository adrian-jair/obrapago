-- Ejecutar como SYS/SYSTEM o un usuario con privilegios.
-- Si tu Oracle usa otro PDB, conéctate primero al PDB correcto.

CREATE USER OBRAPAGO IDENTIFIED BY 123456;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW TO OBRAPAGO;
ALTER USER OBRAPAGO QUOTA UNLIMITED ON USERS;

-- Si el usuario ya existe, no vuelvas a ejecutar CREATE USER.
-- Spring/JPA creará las tablas automáticamente al iniciar porque
-- spring.jpa.hibernate.ddl-auto=update
