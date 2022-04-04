CREATE OR REPLACE PROCEDURE drop_user_if_exists (user_name VARCHAR2)
AS n NUMBER;
BEGIN
    n := 0;
    SELECT COUNT(*) INTO n FROM dba_users WHERE USERNAME = UPPER(user_name);
    IF (n != 0) THEN
        EXECUTE IMMEDIATE ('DROP USER '|| user_name || ' CASCADE');
    END IF;
END;
/
EXEC drop_user_if_exists('C##PH2');

CREATE USER C##PH2 IDENTIFIED BY 123456;
ALTER USER C##PH2 quota 20M ON USERS;

GRANT CREATE SESSION TO C##PH2;
GRANT CONNECT, RESOURCE TO C##LAB3;
GRANT SELECT ANY DICTIONARY TO C##LAB3;
GRANT CREATE TABLE TO C##PH2;
GRANT CREATE ANY PROCEDURE, ALTER ANY PROCEDURE, EXECUTE ANY PROCEDURE, DROP ANY PROCEDURE TO C##PH2;
GRANT GRANT ANY PRIVILEGE TO C##PH2;
GRANT CREATE ANY TRIGGER TO C##PH2;
GRANT CREATE ANY VIEW TO C##PH2;
GRANT GRANT ANY ROLE TO C##PH2;
GRANT GRANT ANY OBJECT PRIVILEGE TO C##LAB3;