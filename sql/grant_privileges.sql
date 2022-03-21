-- Cap quyen insert
-- have_WGO = 1 => WITH GRANT OPTION
-- have_WGO = 0 => NO WITH GRANT OPTION
CREATE OR REPLACE PROCEDURE grant_insert_to_user (
    username IN NVARCHAR2,
    table_name IN NVARCHAR2,
    have_WGO IN INTEGER)
IS str VARCHAR2(100);
BEGIN
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
    str := 'GRANT DELETE ON ' || table_name || ' TO ' || username;
    IF have_WGO = 1 THEN
        str := CONCAT(str,' WITH GRANT OPTION');
    END IF;
    
    EXECUTE IMMEDIATE (str);
        
	COMMIT;
END grant_delete_to_user;
/
-- Cap quyen select
-- have_WGO = 1 => WITH GRANT OPTION
-- have_WGO = 0 => NO WITH GRANT OPTION
-- cmd_line_create_view: lenh tao view. Vi du: 'CREATE TABLE OR REPLACE VIEW view_name AS SELECT * FROM table_name'
CREATE OR REPLACE PROCEDURE grant_select_to_user (
    username IN NVARCHAR2,
    view_name IN NVARCHAR2,
    cmd_line_create_view IN NVARCHAR2,
    have_WGO IN INTEGER)
IS str VARCHAR2(100);
BEGIN
    EXECUTE IMMEDIATE (cmd_line_create_view);
    
    str := 'GRANT SELECT ON ' || view_name || ' TO ' || username;
    IF have_WGO = 1 THEN
        str := CONCAT(str,' WITH GRANT OPTION');
    END IF;
    
    EXECUTE IMMEDIATE (str);
        
	COMMIT;
END grant_select_to_user;
/
-- Tuong tu procedure grant_select_to_user
CREATE OR REPLACE PROCEDURE grant_update_to_user (
    username IN NVARCHAR2,
    view_name IN NVARCHAR2,
    cmd_line_create_view IN NVARCHAR2,
    have_WGO IN INTEGER)
IS str VARCHAR2(100);
BEGIN
    EXECUTE IMMEDIATE (cmd_line_create_view);
    
    str := 'GRANT UPDATE ON ' || view_name || ' TO ' || username;
    IF have_WGO = 1 THEN
        str := CONCAT(str,' WITH GRANT OPTION');
    END IF;
    
    EXECUTE IMMEDIATE (str);
        
	COMMIT;
END grant_update_to_user;

/*
CREATE TABLE table_test (
    id_user NUMBER PRIMARY KEY,
    username NVARCHAR2(30),
    pwd NVARCHAR2(30)
);

INSERT INTO table_test VALUES (1, 'admin', 'admin'), (2, 'user', 'user'), (3, 'guest', 'guest');

CREATE USER C##TEST IDENTIFIED BY 123;
GRANT create session TO C##TEST;
GRANT RESOURCE, CONNECT TO C##TEST;

EXEC grant_insert_to_user('C##TEST', 'table_test', 1);
EXEC grant_delete_to_user('C##TEST', 'table_test', 1);
EXEC grant_select_to_user('C##TEST', 'view_name', 'CREATE OR REPLACE VIEW view_name AS SELECT username FROM table_test WHERE id_user=1', 0);
EXEC grant_update_to_user('C##TEST', 'view_name', 'CREATE OR REPLACE VIEW view_name AS SELECT username FROM table_test WHERE id_user=1', 0);
*/