CREATE OR REPLACE PROCEDURE drop_user(
    user_name NVARCHAR2
) 
IS
    li_count NUMBER;
BEGIN
    SELECT COUNT (1)
    INTO li_count
    FROM dba_users
   	WHERE username = UPPER ( user_name );

   	IF li_count != 0
   	THEN
		EXECUTE IMMEDIATE ( 'DROP USER '|| user_name || ' CASCADE' );
    END IF;
END;
/

EXEC drop_user('C##PH1');

CREATE OR REPLACE PROCEDURE create_user(
	user_name NVARCHAR2,
	pwd NVARCHAR2
) IS
    li_count NUMBER;
BEGIN
    SELECT COUNT (1)
    INTO li_count
    FROM dba_users
   	WHERE username = UPPER ( user_name );

   	IF li_count = 0
   	THEN
        EXECUTE IMMEDIATE ( 'CREATE USER ' || user_name || ' IDENTIFIED BY ' || pwd || ' DEFAULT TABLESPACE SYSTEM' );    
    END IF;
END;
/

EXEC create_user('C##PH1','lamhoangphuc');

CREATE OR REPLACE PROCEDURE create_role(
    r_name NVARCHAR2
) 
IS
    li_count NUMBER;
BEGIN
    SELECT COUNT (1)
    INTO li_count
    FROM dba_roles
   	WHERE dba_roles.role = UPPER ( r_name );
    
    IF li_count = 0
   	THEN
        EXECUTE IMMEDIATE ( 'CREATE ROLE ' || r_name);
    END IF;
END;
/

EXEC create_role('C##test_role');

CREATE OR REPLACE PROCEDURE drop_role(
    r_name NVARCHAR2
) 
IS
    li_count NUMBER;
BEGIN
    SELECT COUNT (1)
    INTO li_count
    FROM dba_roles
   	WHERE dba_roles.role = UPPER ( r_name );
    
    IF li_count != 0
   	THEN
        EXECUTE IMMEDIATE ( 'DROP ROLE ' || r_name);
    END IF;
END;
/

EXEC drop_role('C##test_role');

CREATE OR REPLACE PROCEDURE add_user_to_role(u_name NVARCHAR2,r_name NVARCHAR2)
IS
    u_count NUMBER;
    r_count NUMBER;
BEGIN
    SELECT COUNT (1)
    INTO u_count
    FROM dba_users
   	WHERE username = UPPER ( u_name );
    
    SELECT COUNT (1)
    INTO r_count
    FROM dba_roles
   	WHERE dba_roles.role = UPPER ( r_name );
    
    if u_count != 0 AND r_count != 0
    THEN
        EXECUTE IMMEDIATE ( 'GRANT ' || r_name || ' TO ' || u_name);
    END IF;
END;
/

EXEC create_user('C##PH1','lamhoangphuc');
EXEC create_role('C##test_role');
EXEC add_user_to_role('C##PH1', 'C##test_role');


CREATE OR REPLACE PROCEDURE remove_user_from_role(u_name NVARCHAR2,r_name NVARCHAR2)
IS
    u_count NUMBER;
    r_count NUMBER;
BEGIN
    SELECT COUNT (1)
    INTO u_count
    FROM dba_users
   	WHERE username = UPPER ( u_name );
    
    SELECT COUNT (1)
    INTO r_count
    FROM dba_roles
   	WHERE dba_roles.role = UPPER ( r_name );
    
    if u_count != 0 AND r_count != 0
    THEN
        EXECUTE IMMEDIATE ( 'REVOKE ' || r_name || ' FROM ' || u_name);
    END IF;
END;
/

EXEC remove_user_from_role('C##PH1', 'C##test_role');

-- xem tất cả role
SELECT *
FROM dba_roles;

-- xem tất cả user
SELECT *
FROM dba_users;
