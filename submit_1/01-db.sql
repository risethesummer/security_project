CREATE OR REPLACE TYPE array_table IS TABLE OF VARCHAR2 (100);
/
CREATE OR REPLACE PROCEDURE GET_TABLE_DETAILS(
    schema  IN VARCHAR2,
    tab_name IN VARCHAR2,
    result OUT SYS_REFCURSOR)
AS
BEGIN
    OPEN result FOR SELECT 
                        DISTINCT
                        cols.COLUMN_NAME, 
                        cols.DATA_TYPE, 
                        cols.DATA_LENGTH,
                        cols.DATA_PRECISION,
                        cons.CONSTRAINT_TYPE,
                        cols.NULLABLE,
                        conCol_pk.TABLE_NAME,
                        conCol_pk.COLUMN_NAME
                    FROM 
                        DBA_TAB_COLUMNS cols
                        LEFT JOIN DBA_CONS_COLUMNS conCol ON cols.OWNER = conCol.OWNER AND cols.TABLE_NAME = conCol.TABLE_NAME AND cols.COLUMN_NAME = conCol.COLUMN_NAME
                        LEFT JOIN DBA_CONSTRAINTS cons ON cons.OWNER = conCol.OWNER AND conCol.CONSTRAINT_NAME = cons.CONSTRAINT_NAME AND conCol.TABLE_NAME = cons.TABLE_NAME
                        LEFT JOIN DBA_CONSTRAINTS cons_pk ON cons_pk.OWNER = cons.OWNER AND cons_pk.CONSTRAINT_NAME = cons.R_CONSTRAINT_NAME
                        LEFT JOIN DBA_CONS_COLUMNS conCol_pk ON conCol_pk.OWNER = cons_pk.OWNER AND conCol_pk.CONSTRAINT_NAME = cons_pk.CONSTRAINT_NAME AND conCol_pk.TABLE_NAME = cons_pk.TABLE_NAME
                    WHERE
                        cols.OWNER = schema
                        AND cols.TABLE_NAME = tab_name
                    ORDER BY cols.COLUMN_NAME ASC;
END;
/
CREATE OR REPLACE PROCEDURE GET_SUITABLE_TABLE_REFFERRENCES (
    schema          IN VARCHAR2,
    data_type_in   IN VARCHAR2,
    data_length_in    IN NUMBER,
    result OUT SYS_REFCURSOR)
AS
BEGIN
    OPEN result FOR SELECT
                        cols.OWNER, 
                        cols.TABLE_NAME,
                        cols.COLUMN_NAME
                    FROM 
                        DBA_TAB_COLUMNS cols
                        JOIN DBA_CONS_COLUMNS conCol ON cols.OWNER = conCol.OWNER AND cols.TABLE_NAME = conCol.TABLE_NAME AND cols.COLUMN_NAME = conCol.COLUMN_NAME
                        JOIN DBA_CONSTRAINTS cons ON cons.OWNER = conCol.OWNER AND conCol.CONSTRAINT_NAME = cons.CONSTRAINT_NAME AND conCol.TABLE_NAME = cons.TABLE_NAME
                    WHERE
                        cols.OWNER = schema
                        AND cons.CONSTRAINT_TYPE IN ('P', 'U')
                        AND cols.data_type = data_type_in
                        AND cols.data_length = data_length_in
                    MINUS
                    SELECT
                        conCol.OWNER, 
                        conCol.TABLE_NAME,
                        conCol.COLUMN_NAME
                    FROM 
                        DBA_CONS_COLUMNS conCol
                    WHERE
                        conCol.OWNER = schema
                        AND (SELECT COUNT(COLUMN_NAME)
                            FROM DBA_CONS_COLUMNS subCon
                            WHERE subCon.OWNER = conCol.OWNER 
                            AND subCon.TABLE_NAME = conCol.TABLE_NAME 
                            AND subCon.CONSTRAINT_NAME = conCol.CONSTRAINT_NAME 
                            AND subCon.COLUMN_NAME != conCol.COLUMN_NAME) > 0;
END;
/
CREATE OR REPLACE PROCEDURE create_user(
    state       OUT NUMBER,
	user_name   IN VARCHAR2,
	pwd         IN VARCHAR2,
    roles       IN array_table,
    local_user  IN NUMBER
) 
AS
BEGIN
    IF local_user = 1
    THEN
        EXECUTE IMMEDIATE ('ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE');
    END IF;
    EXECUTE IMMEDIATE ('CREATE USER ' || user_name || ' IDENTIFIED BY ' || pwd);
    EXECUTE IMMEDIATE ('ALTER SESSION SET "_ORACLE_SCRIPT" = FALSE');
    EXECUTE IMMEDIATE ('GRANT CREATE SESSION TO ' || user_name);  
    FOR i IN 1 .. roles.COUNT
    LOOP
        BEGIN
            EXECUTE IMMEDIATE ('GRANT ' || roles(i) || ' TO ' || user_name);
        EXCEPTION
            WHEN OTHERS THEN
                CONTINUE;
        END;
    END LOOP;
    state := 1;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        state := 0;
        ROLLBACK;
END;
/
CREATE OR REPLACE PROCEDURE create_role(
    state       OUT NUMBER,
	role_name   IN VARCHAR2,
    users       IN array_table,
    local_role  IN NUMBER
) 
AS
BEGIN
    IF local_role = 1
    THEN
        EXECUTE IMMEDIATE ('ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE');
    END IF;
    EXECUTE IMMEDIATE ('CREATE ROLE ' || role_name || ' NOT IDENTIFIED');
    EXECUTE IMMEDIATE ('ALTER SESSION SET "_ORACLE_SCRIPT" = FALSE');
    EXECUTE IMMEDIATE ('GRANT CREATE SESSION TO ' || role_name);  
    FOR i IN 1 .. users.COUNT
    LOOP
        BEGIN
            EXECUTE IMMEDIATE ('GRANT ' || role_name || ' TO ' || users(i));
        EXCEPTION
            WHEN OTHERS THEN
                CONTINUE;
        END;
    END LOOP;
    state := 1;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        state := 0;
        ROLLBACK;
END;
/
CREATE OR REPLACE PROCEDURE DROP_USER
(
	user_name   IN VARCHAR2,
    local_user IN NUMBER
) 
AUTHID  CURRENT_USER
AS
BEGIN
    IF local_user = 1
    THEN
        EXECUTE IMMEDIATE ('ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE');
    END IF;
    EXECUTE IMMEDIATE ('DROP USER ' || user_name || ' CASCADE');
    EXECUTE IMMEDIATE ('ALTER SESSION SET "_ORACLE_SCRIPT" = FALSE');
    COMMIT;
END;
/
CREATE OR REPLACE PROCEDURE DROP_ROLE (
	role_name   IN VARCHAR2,
    local_role IN NUMBER
) 
AS
BEGIN
    IF local_role = 1
    THEN
        EXECUTE IMMEDIATE ('ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE');
    END IF;
    EXECUTE IMMEDIATE ('DROP ROLE ' || role_name);
    EXECUTE IMMEDIATE ('ALTER SESSION SET "_ORACLE_SCRIPT" = FALSE');
END;
/
CREATE OR REPLACE PROCEDURE REVOKE_PRIVILEGE_WITH_CHECK (
    action      IN VARCHAR2,
    obj_name    IN VARCHAR2,
    user_name   IN VARCHAR2)
AS
BEGIN
    EXECUTE IMMEDIATE ('REVOKE ' || action || ' ON ' || obj_name || ' FROM ' || user_name);
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN DBMS_OUTPUT.PUT_LINE('ingore revoking!');
END;
/
CREATE OR REPLACE PROCEDURE DROP_VIEW_IF_EXISTS (
    schema_in       IN VARCHAR2,
    view_name_in    IN VARCHAR2)
AS
    count_view      NUMBER := 0;
    view_name_var VARCHAR2(1000) := schema_in || '.' || view_name_in;
BEGIN
    SELECT COUNT(dv.VIEW_NAME)
    INTO count_view
    FROM DBA_VIEWS dv
    WHERE dv.OWNER = schema_in AND dv.VIEW_NAME = view_name_in;
    IF count_view > 0
    THEN
        EXECUTE IMMEDIATE ('DROP VIEW ' || view_name_var);
    END IF;
    COMMIT;
END;
/
CREATE OR REPLACE PROCEDURE CREATE_VIEW_FOR_SELECT_PRIVILEGE (
    user_name       IN VARCHAR2,
    schema_name     IN VARCHAR2,
    table_name      IN VARCHAR2,
    cols            IN VARCHAR2,
    wgo             IN VARCHAR2)
AS
    view_name_var   VARCHAR2(1000) := user_name || '_SELECT_ON_' || table_name;
    created_view_name_var VARCHAR2(1000) := schema_name || '.' || view_name_var;
    obj_name_var    VARCHAR(1000) := schema_name || '.' || table_name;
BEGIN
    DROP_VIEW_IF_EXISTS(schema_name, created_view_name_var);
    EXECUTE IMMEDIATE ('CREATE OR REPLACE VIEW ' || created_view_name_var ||
                    ' AS SELECT ' || cols ||
                    ' FROM ' || obj_name_var);
    REVOKE_PRIVILEGE_WITH_CHECK('SELECT', obj_name_var, user_name);
    EXECUTE IMMEDIATE ('GRANT SELECT ON ' || created_view_name_var || ' TO ' || user_name || wgo);
    COMMIT;
END;
/
CREATE OR REPLACE PROCEDURE REVOKE_SELECT_PRIVILEGE (
    user_name       IN VARCHAR2,
    schema_name     IN VARCHAR2,
    table_name      IN VARCHAR2)
AS
    obj_name_var    VARCHAR(1000) := schema_name || '.' || table_name;
BEGIN
    DROP_VIEW_IF_EXISTS(schema_name, user_name || '_SELECT_ON_' || table_name);
    REVOKE_PRIVILEGE_WITH_CHECK('SELECT', obj_name_var, user_name);
    COMMIT;
END;
/
