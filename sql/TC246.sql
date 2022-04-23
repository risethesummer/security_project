
ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE;

-- Create user cho bang nhan vien
CREATE OR REPLACE PROCEDURE create_user_from_tb_nhanvien
AS n NUMBER;
BEGIN
    n := 0;
    FOR u IN (
        SELECT USERNAME
        FROM NHANVIEN
    )
    LOOP
        SELECT COUNT(*) INTO n FROM dba_users WHERE USERNAME = u.USERNAME;
        IF (n = 0) THEN
            EXECUTE IMMEDIATE ('CREATE USER '||u.USERNAME||' IDENTIFIED BY 1');
            EXECUTE IMMEDIATE ('GRANT CREATE SESSION TO '||u.USERNAME);
        END IF;
    END LOOP;
END;
/

-- TC#2;
CREATE OR REPLACE PROCEDURE grant_select(
    username VARCHAR2, 
    grantee VARCHAR2)
AS   
BEGIN
    FOR r IN (
        SELECT owner, table_name 
        FROM all_tables 
        WHERE owner = username
    )
    LOOP
        EXECUTE IMMEDIATE 
            'GRANT SELECT ON '||r.owner||'.'||r.table_name||' to ' || grantee;
    END LOOP;
END;
/
CREATE OR REPLACE PROCEDURE grant_role_to_user_from_nhanvien (
    roleIntable VARCHAR2,
    rolename VARCHAR2
    )
AS 
BEGIN
    FOR i IN (select USERNAME from nhanvien where vaitro = roleIntable)
    LOOP
        EXECUTE IMMEDIATE
            'GRANT '||rolename||' to '||i.USERNAME;
    END LOOP;
END;
/

EXEC create_user_from_tb_nhanvien;
CREATE ROLE RL_THANHTRA;
EXEC grant_select('QLKCB', 'RL_THANHTRA');
EXEC grant_role_to_user_from_nhanvien('Thanh tra', 'RL_THANHTRA');

-------------------------------------------------------------------------------------------------------------------------------
-- TC3
CREATE OR REPLACE FUNCTION VPD_HSBA (p_schema VARCHAR2, p_obj VARCHAR2)
RETURN VARCHAR2
AS
    p_username VARCHAR2(128);
    p_vai_tro NVARCHAR2(20);
    p_csyt VARCHAR2(4);
BEGIN
    IF (SYS_CONTEXT('userenv', 'ISDBA')) THEN 
        RETURN '';
    END IF;
    
    p_username := SYS_CONTEXT('userenv', 'SESSION_USER');
    SELECT VAITRO, CSYT INTO p_vai_tro, p_csyt FROM NHANVIEN WHERE NHANVIEN.USERNAME = p_username;
    IF (p_vai_tro = n'C? s? y t?') THEN
        RETURN 'CSYT = ' || p_csyt || ' AND TO_NUMBER(TO_CHAR(NGAY, ' || char(39) || 'DD' || char(39) || ')) > 4 AND TO_NUMBER(TO_CHAR(NGAY, ' || char(39) || 'DD' || char(39) || ')) < 28';
    END IF;
    RETURN '';
END;
/
BEGIN
    dbms_rls.add_policy (
    object_schema => 'QLKCB',
    object_name => 'HSBA',
    policy_name => 'QLKCB_HSBA',
    function_schema => 'VPD_HSBA',
    statement_types => 'select', 'insert', 'delete'
    );
END;


