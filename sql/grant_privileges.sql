-- Cap quyen insert
-- have_WGO = 1 => WITH GRANT OPTION
-- have_WGO = 0 => NO WITH GRANT OPTION
CREATE OR REPLACE PROCEDURE grant_insert_to_user (
    username IN NVARCHAR2,
    table_name IN NVARCHAR2,
    have_WGO IN INTEGER)
IS str VARCHAR2(100);
BEGIN
	str := 'GRANT RESOURCE, CONNECT TO ' || username;

	EXECUTE IMMEDIATE (str);
    
    str := 'GRANT INSERT ON ' || table_name || ' TO ' || username;
    IF have_WGO = 1 THEN
        str := CONCAT(str,' WITH GRANT OPTION');
    END IF;
    
    EXECUTE IMMEDIATE (str);
        
	COMMIT;
END grant_insert_to_user;
/
-- Cap quyen delete
-- have_WGO = 1 => WITH GRANT OPTION
-- have_WGO = 0 => NO WITH GRANT OPTION
CREATE OR REPLACE PROCEDURE grant_delete_to_user (
    username IN NVARCHAR2,
    table_name IN NVARCHAR2,
    have_WGO IN INTEGER)
IS str VARCHAR2(100);
BEGIN
	str := 'GRANT RESOURCE, CONNECT TO ' || username;

	EXECUTE IMMEDIATE (str);
    
    str := 'GRANT DELETE ON ' || table_name || ' TO ' || username;
    IF have_WGO = 1 THEN
        str := CONCAT(str,' WITH GRANT OPTION');
    END IF;
    
    EXECUTE IMMEDIATE (str);
        
	COMMIT;
END grant_delete_to_user;
/

/*
CREATE USER C##TEST IDENTIFIED BY 123;
GRANT create session TO C##TEST;

EXEC grant_insert_to_user('C##TEST', 'table_test', 1);
EXEC grant_delete_to_user('C##TEST', 'table_test', 1);
*/