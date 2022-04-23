
ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE;

-------------------------------------------------------------
-- TC#1
-- Create user cho bang nhan vien
CREATE OR REPLACE PROCEDURE QLKCB.create_user_from_tb_nhanvien
AS n NUMBER;
BEGIN
    n := 0;
    FOR u IN (
        SELECT USERNAME
        FROM QLKCB.NHANVIEN
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

-- Create user cho bang benh nhan
CREATE OR REPLACE PROCEDURE QLKCB.create_user_from_tb_benhnhan
AS n NUMBER;
BEGIN
    n := 0;
    FOR u IN (
        SELECT USERNAME
        FROM QLKCB.BENHNHAN
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

EXEC QLKCB.create_user_from_tb_nhanvien;
EXEC QLKCB.create_user_from_tb_benhnhan;

-------------------------------------------------------------
-- TC#2;
CREATE OR REPLACE PROCEDURE QLKCB.grant_select(
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
CREATE OR REPLACE PROCEDURE QLKCB.grant_role_to_user_from_nhanvien (
    roleIntable VARCHAR2,
    rolename VARCHAR2
    )
AS 
BEGIN
    FOR i IN (select USERNAME from QLKCB.nhanvien where vaitro = roleIntable)
    LOOP
        EXECUTE IMMEDIATE
            'GRANT '||rolename||' to '||i.USERNAME;
    END LOOP;
END;
/

CREATE ROLE RL_THANHTRA;
EXEC QLKCB.grant_select('QLKCB', 'RL_THANHTRA');
EXEC QLKCB.grant_role_to_user_from_nhanvien('Thanh tra', 'RL_THANHTRA');

-------------------------------------------------------------
-- TC#3
CREATE OR REPLACE VIEW TC3_HSBA AS
SELECT HSBA.*
FROM HSBA, NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND nv.VAITRO = 'Co so y te'
AND HSBA.MACSYT = nv.CSYT
AND TO_NUMBER(TO_CHAR(HSBA.NGAY, 'DD')) > 4
AND TO_NUMBER(TO_CHAR(HSBA.NGAY, 'DD')) < 28;


CREATE OR REPLACE VIEW TC3_HSBA_DV AS
SELECT HSBA_DV.*
FROM HSBA, HSBA_DV, NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND nv.VAITRO = 'Co so y te'
AND HSBA.MACSYT = nv.CSYT
AND HSBA.MAHSBA = HSBA_DV.MAHSBA
AND TO_NUMBER(TO_CHAR(HSBA_DV.NGAY, 'DD')) > 4
AND TO_NUMBER(TO_CHAR(HSBA_DV.NGAY, 'DD')) < 28;


CREATE ROLE RL_COSOYTE;
GRANT SELECT ON TC3_HSBA TO RL_COSOYTE;
GRANT SELECT ON TC3_HSBA_DV TO RL_COSOYTE;

-------------------------------------------------------------
-- TC#4
CREATE OR REPLACE VIEW TC4_HSBA AS
SELECT HSBA.* 
FROM HSBA, NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND nv.VAITRO = 'Y si/bac si'
AND HSBA.MABS = nv.MANV;

CREATE OR REPLACE VIEW TC4_HSBA_DV AS
SELECT HSBA_DV.* 
FROM HSBA_DV, HSBA, NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND nv.VAITRO = 'Y si/bac si'
AND HSBA.MABS = nv.MANV
AND HSBA.MAHSBA = HSBA_DV.MAHSBA;


CREATE OR REPLACE VIEW TC4_BENHNHAN AS
SELECT BENHNHAN.*
FROM HSBA, BENHNHAN, NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND nv.VAITRO = 'Y si/bac si'
AND HSBA.MABS = nv.MANV
AND BENHNHAN.MABN = HSBA.MABN;


CREATE ROLE RL_BACSI;
GRANT SELECT ON TC4_HSBA TO RL_BACSI;
GRANT SELECT ON TC4_HSBA_DV TO RL_BACSI;
GRANT SELECT ON TC4_BENHNHAN TO RL_BACSI;

-------------------------------------------------------------
-- TC#5
CREATE OR REPLACE VIEW TC5_HSBA AS
SELECT HSBA.*
FROM HSBA, NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND nv.VAITRO = 'Nghien cuu'
AND HSBA.MACSYT = nv.CSYT
AND HSBA.MAKHOA = nv.CHUYENKHOA;


CREATE OR REPLACE VIEW TC5_HSBA_DV AS
SELECT HSBA_DV.*
FROM HSBA, HSBA_DV, NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND nv.VAITRO = 'Nghien cuu'
AND HSBA.MACSYT = nv.CSYT
AND HSBA.MAKHOA = nv.CHUYENKHOA
AND HSBA.MAHSBA = HSBA_DV.MAHSBA;

CREATE ROLE RL_NGHIENCUU;
GRANT SELECT ON TC5_HSBA TO RL_NGHIENCUU;
GRANT SELECT ON TC5_HSBA_DV TO RL_NGHIENCUU;

-------------------------------------------------------------
-- TC#6
CREATE OR REPLACE VIEW TC6_NHANVIEN AS
SELECT nv.*
FROM NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND nv.VAITRO != 'Thanh tra';

CREATE OR REPLACE VIEW TC6_BENHNHAN AS
SELECT bn.*
FROM BENHNHAN bn
WHERE bn.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER');

CREATE ROLE RL_BENHNHAN;
GRANT SELECT ON TC6_BENHNHAN TO RL_BENHNHAN;
GRANT UPDATE(
    TENBN,
    MACSYT,
    CMND,
    NGAYSINH,
    SONHA,
    TENDUONG,
    QUANHUYEN,
    TINHTP,
    TIENSUBENH,
    TIENSUBENHGD,
    DIUNGTHUOC,
    USERNAME
) ON TC6_BENHNHAN TO RL_BENHNHAN;

CREATE ROLE RL_NHANVIEN;
GRANT SELECT ON TC6_NHANVIEN TO RL_NHANVIEN;
GRANT UPDATE(
    HOTEN,
    PHAI,
    CMND,
    NGAYSINH,
    QUEQUAN,
    SODT,
    CSYT,
    VAITRO,
    CHUYENKHOA,
    USERNAME
) ON TC6_NHANVIEN TO RL_NHANVIEN;
