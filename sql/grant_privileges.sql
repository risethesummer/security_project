/* NGUYEN QUANG PHU - 19127507 */

/* ======================== INSERT ======================== */
------------- GRANT -------------
-- have_WGO = 1 => WITH GRANT OPTION
-- have_WGO = 0 => NO WITH GRANT OPTION
CREATE OR REPLACE PROCEDURE grant_insert_to_user (
    username IN NVARCHAR2,
    table_name IN NVARCHAR2,
    lst_cols IN NVARCHAR2,
    have_WGO IN INTEGER)
IS str NVARCHAR2(1000);
BEGIN
    str := 'GRANT INSERT ';
    IF (LENGTH(lst_cols) != 0) THEN
        str := CONCAT(str, '(' || lst_cols || ') ');
    END IF;
    str := CONCAT(str, 'ON ' || table_name || ' TO ' || username);
    IF have_WGO = 1 THEN
        str := CONCAT(str,' WITH GRANT OPTION');
    END IF;
    DBMS_OUTPUT.PUT_LINE(str);
    EXECUTE IMMEDIATE (str);   
	COMMIT;
END grant_insert_to_user;
/

/* ======================== UPDATE ======================== */
------------- GRANT -------------
-- have_WGO = 1 => WITH GRANT OPTION
-- have_WGO = 0 => NO WITH GRANT OPTION
CREATE OR REPLACE PROCEDURE grant_update_to_user (
    username IN NVARCHAR2,
    table_name IN NVARCHAR2,
    lst_cols IN NVARCHAR2,
    have_WGO IN INTEGER)
IS str NVARCHAR2(1000);
BEGIN
    str := 'GRANT UPDATE ';
    IF (LENGTH(lst_cols) != 0) THEN
        str := CONCAT(str, '(' || lst_cols || ') ');
    END IF;
    str := CONCAT(str, 'ON ' || table_name || ' TO ' || username);
    IF have_WGO = 1 THEN
        str := CONCAT(str,' WITH GRANT OPTION');
    END IF;
    DBMS_OUTPUT.PUT_LINE(str);
    EXECUTE IMMEDIATE (str);   
	COMMIT;
END grant_update_to_user;
/
-- ================== DEMO ==================
-- select * from table_test;
-- Phan quyen Insert tren cot voi WITH GRANT OPTION
--      EXEC grant_insert_to_user('C##TEST', 'table_test', 'username, pwd', 1);
-- Phan quyen Insert tren toan bang khong WITH GRANT OPTION
--      EXEC grant_insert_to_user('C##TEST', 'table_test', '', 0);



-- Cap quyen delete
-- have_WGO = 1 => WITH GRANT OPTION
-- have_WGO = 0 => NO WITH GRANT OPTION
CREATE OR REPLACE PROCEDURE grant_delete_to_user (
    username IN NVARCHAR2,
    table_name IN NVARCHAR2,
    have_WGO IN INTEGER)
IS str VARCHAR2(1000);
BEGIN 
    str := 'GRANT DELETE ON ' || table_name || ' TO ' || username;
    IF have_WGO = 1 THEN
        str := CONCAT(str,' WITH GRANT OPTION');
    END IF;
    DBMS_OUTPUT.PUT_LINE(str);
    EXECUTE IMMEDIATE (str);
        
	COMMIT;
END grant_delete_to_user;
/
-- Cap quyen select
-- have_WGO = 1 => WITH GRANT OPTION
-- have_WGO = 0 => NO WITH GRANT OPTION
CREATE OR REPLACE PROCEDURE grant_select_to_user (
    username IN NVARCHAR2,
    table_name IN NVARCHAR2,
    lst_column IN VARCHAR2,
    have_WGO IN INTEGER,
    view_name IN VARCHAR2)
IS str VARCHAR2(1000);
BEGIN
    str := 'CREATE OR REPLACE VIEW ' || view_name || ' AS SELECT ' || lst_column || ' FROM ' || table_name;
    DBMS_OUTPUT.PUT_LINE(str);
    EXECUTE IMMEDIATE (str);
    str := '';
    str := 'GRANT SELECT ON ' || view_name || ' TO ' || username;
    IF have_WGO = 1 THEN
        str := CONCAT(str,' WITH GRANT OPTION');
    END IF;
    DBMS_OUTPUT.PUT_LINE(str);
    EXECUTE IMMEDIATE (str);
        
	COMMIT;
END grant_select_to_user;
/
-- EXEC grant_select_to_user('C##TEST', 'table_test', 'username, pwd', 1, 'view_USER_PWD_TEST');
-------------------- REVOKE --------------------
/*==============================================*/
CREATE OR REPLACE PROCEDURE revoke_privileges_from_user (
    username IN NVARCHAR2,
    table_name IN NVARCHAR2,
    lst_privileges IN NVARCHAR2)
IS str VARCHAR2(1000);
BEGIN
    str := 'REVOKE ' || lst_privileges || ' ON ' || table_name || ' FROM ' || username;
    DBMS_OUTPUT.PUT_LINE(str);
    EXECUTE IMMEDIATE (str);
	COMMIT;
END revoke_privileges_from_user;
/
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
GRANT SELECT (id_user) ON table_test TO C##TEST;
select * from table_test;
EXEC grant_delete_to_user('C##TEST', 'table_test', 1);

EXEC grant_update_to_user('C##TEST', 'view_name', 'CREATE OR REPLACE VIEW view_name AS SELECT username FROM table_test WHERE id_user=1', 0);
*/