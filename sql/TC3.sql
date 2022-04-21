-- test một nhân viên
CREATE OR REPLACE PROCEDURE drop_user_if_exists (user_name VARCHAR2)
AS n NUMBER;
BEGIN
    n := 0;
    SELECT COUNT(*) INTO n FROM dba_users WHERE USERNAME = UPPER(user_name);
    IF (n != 0) THEN
        DBMS_OUTPUT.PUT_LINE('User ' || user_name || ' da ton tai'); 
        EXECUTE IMMEDIATE ('DROP USER '|| user_name || ' CASCADE');
    END IF;
END;
/

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

EXEC drop_user_if_exists('C##QLKCB_TC3');

CREATE USER C##QLKCB_TC3 IDENTIFIED BY 123456;
ALTER USER C##QLKCB_TC3 quota 20M ON USERS;

-- tạo 2 view cho HSBA và HSBA_DV
CONNECT C##QLKCB/123456;

CREATE OR REPLACE VIEW HSBA_TC3 AS
SELECT HSBA.*
FROM HSBA, NHANVIEN nv,
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND HSBA.CSYT = nv.CSYT
AND TO_NUMBER(TO_CHAR(HSBA.NGAY, 'DD')) > 4
AND TO_NUMBER(TO_CHAR(HSBA.NGAY, 'DD')) < 28;


CREATE OR REPLACE VIEW HSBA_DV_TC3 AS
SELECT HSBA_DV.*
FROM HSBA, HSBA_DV, NHANVIEN nv,
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER')
AND HSBA.CSYT = nv.CSYT
AND HSBA.MAHSBA = HSBA_DV.MAHSBA
AND TO_NUMBER(TO_CHAR(HSBA_DV.NGAY, 'DD')) > 4
AND TO_NUMBER(TO_CHAR(HSBA_DV.NGAY, 'DD')) < 28;


-- grant quyền cho user test
EXEC drop_role('C##TC3');
CREATE ROLE C##TC3;

GRANT SELECT, INSERT ON HSBA_TC3 TO C##TC3;
GRANT SELECT, INSERT ON HSBA_DV_TC3 TO C##TC3;

GRANT C##TC3 TO C##QLKCB_TC3;

-- tạo dữ liệu
INSERT INTO CSYT VALUES (
    'CS00',
    'Cơ sở y tế tỉnh Cần Thơ',
    'Cần Thơ',
    '123123123123',
);

INSERT INTO NHANVIEN VALUES (
    'NV0000',
    'Nhan viên',
    'Nam',
    '12312312312',
    TO_DATE('17/11/1981', 'DD/MM/YYYY'),
    'Ho Chi Minh',
    '1231231232'
    '1231231232'
    'CS00',
    'Thanh tra',
    'Khoa atbm',
    'C##QLKCB_TC3',
);

INSERT INTO BENHNHAN VALUES (
    'BN00000',
    'Nguyen Van A',
    'CS00',
    '123123123123',
    '12312312312',
    TO_DATE('17/11/1981', 'DD/MM/YYYY'),
    '18',
    'nguyen binh khiem',
    'quan 3',
    'ho chi minh',
    '',
    '',
    '',
    'C##QLKCB_TC3_BN',
);

INSERT INTO KHOA VALUES (
    'K000',
    'khoa atbm',
);

INSERT INTO DICHVU VALUES (
    'DV000',
    'Dịch vụ atbm',
);

-- test
CONNECT C##QLKCB_TC3/123456;

INSERT INTO HSBA_TC3 VALUES (
    'HS000000',
    'BN00000',
    TO_DATE('20/04/2022', 'DD/MM/YYYY'),
    '',
    'NV0000',
    'K000',
    'CS00',
    '',
);
SELECT * FROM HSBA_TC3;


INSERT INTO HSBA_DV_TC3 VALUES (
    'HSDV0000',
    'DV000',
    TO_DATE('20/04/2022', 'DD/MM/YYYY'),
    'NV0000',
    ''
);
SELECT * FROM HSBA_DV_TC3;

DELETE FROM HSBA_TC3
WHERE MAHSBA = 'HS000000';
SELECT * FROM HSBA_TC3;

DELETE FROM HSBA_DV_TC3
WHERE MAHSBA = 'HSDV0000';
SELECT * FROM HSBA_DV_TC3;