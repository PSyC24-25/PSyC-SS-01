DROP SCHEMA IF EXISTS doctorclick;
DROP USER IF EXISTS 'spq'@'localhost';

CREATE SCHEMA doctorclick;
CREATE USER 'spq'@'localhost' IDENTIFIED BY 'spq';
GRANT ALL PRIVILEGES ON doctorclick.* TO 'spq'@'localhost';
