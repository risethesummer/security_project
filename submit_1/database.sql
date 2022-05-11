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
CREATE OR REPLACE PROCEDURE SYS.drop_user_if_exists (user_name VARCHAR2)
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
EXECUTE SYS.drop_user_if_exists('c##apdsgvkyp3s5v8y');
/
CREATE USER c##apdsgvkyp3s5v8y IDENTIFIED BY hnl;
/
ALTER USER c##apdsgvkyp3s5v8y QUOTA UNLIMITED ON USERS;
/
GRANT CREATE TABLE TO c##apdsgvkyp3s5v8y;
/
GRANT CREATE ANY PROCEDURE TO c##apdsgvkyp3s5v8y;
/
GRANT EXECUTE ON DBMS_CRYPTO to c##apdsgvkyp3s5v8y;
/
CREATE TABLE c##apdsgvkyp3s5v8y.D7711589BB9785CAAFFF31C1143E9 (
    D3B75866A3178   RAW(16) NOT NULL,
    B261E86AE5213   RAW(48) NOT NULL
);
/
CREATE OR REPLACE FUNCTION c##apdsgvkyp3s5v8y.LAY_MA (
    enc_ma IN RAW, enc_key IN RAW)
RETURN RAW
IS
    l_mod_cbc pls_integer := DBMS_CRYPTO.ENCRYPT_AES128
                           + DBMS_CRYPTO.CHAIN_CBC
                           + DBMS_CRYPTO.PAD_PKCS5;
BEGIN 
    RETURN DBMS_CRYPTO.DECRYPT(src => enc_ma,
                                TYP => l_mod_cbc,
                                key => enc_key);  
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END;
/


CREATE OR REPLACE FUNCTION c##apdsgvkyp3s5v8y.LAY_KEY (
    ma IN CHAR, pad IN CHAR)
RETURN RAW
IS

    dec_key RAW(16) := UTL_RAW.CAST_TO_RAW(ma || UTL_RAW.CAST_TO_VARCHAR2(UTL_RAW.REVERSE(UTL_RAW.CAST_TO_RAW(ma))) || pad);
    l_mod_cbc pls_integer := DBMS_CRYPTO.ENCRYPT_AES128
       + DBMS_CRYPTO.CHAIN_CBC
       + DBMS_CRYPTO.PAD_PKCS5;
    raw_ma  RAW(16);
BEGIN
    FOR enc_row IN (SELECT D3B75866A3178, B261E86AE5213 FROM c##apdsgvkyp3s5v8y.D7711589BB9785CAAFFF31C1143E9)
    LOOP
        raw_ma := LAY_MA(enc_row.D3B75866A3178, dec_key);
        IF raw_ma IS NOT NULL AND UTL_RAW.CAST_TO_VARCHAR2(raw_ma) = ma 
        THEN
            RETURN DBMS_CRYPTO.DECRYPT(src => enc_row.B261E86AE5213,
                                            TYP => l_mod_cbc,
                                            key => dec_key);
        END IF;
    END LOOP;
    RETURN NULL;
END;
/
EXEC drop_user_if_exists('C##QLKCB');

CREATE USER C##QLKCB IDENTIFIED BY QLKCB;
ALTER USER C##QLKCB quota 20M ON USERS;
GRANT CREATE TABLE TO C##QLKCB;
GRANT SELECT ANY DICTIONARY TO C##QLKCB;
GRANT EXECUTE ON DBMS_CRYPTO to C##QLKCB;
GRANT EXECUTE ON c##apdsgvkyp3s5v8y.LAY_KEY TO C##QLKCB;
GRANT INSERT ON c##apdsgvkyp3s5v8y.D7711589BB9785CAAFFF31C1143E9 TO C##QLKCB;

-- Tao bang KHOA
CREATE TABLE C##QLKCB.KHOA (
    MAKHOA CHAR(4) PRIMARY KEY,
    TENKHOA NVARCHAR2(100) NOT NULL UNIQUE
);

-- Tao bang DICHVU
CREATE TABLE C##QLKCB.DICHVU (
    MADV CHAR(5) PRIMARY KEY,
    TENDV NVARCHAR2(100) NOT NULL UNIQUE
);

-- Tao bang CSYT
CREATE TABLE C##QLKCB.CSYT (
    MACSYT CHAR(4) PRIMARY KEY,
    TENCSYT NVARCHAR2(100) NOT NULL,
    DCCSYT NVARCHAR2(200) NOT NULL,
    SDTCSYT VARCHAR2(15) NOT NULL
    
);

-- Tao bang BENHNHAN
CREATE TABLE C##QLKCB.BENHNHAN (
    MABN CHAR(7) PRIMARY KEY,
    TENBN NVARCHAR2(100) NOT NULL,
    MACSYT CHAR(4) NOT NULL,
    CMND RAW(16) NOT NULL,
    NGAYSINH DATE NOT NULL,
    SONHA VARCHAR2(20),
    TENDUONG NVARCHAR2(100),
    QUANHUYEN NVARCHAR2(100) NOT NULL,
    TINHTP NVARCHAR2(100) NOT NULL,
    TIENSUBENH NVARCHAR2(200),
    TIENSUBENHGD NVARCHAR2(200),
    DIUNGTHUOC NVARCHAR2(200),
    USERNAME VARCHAR2(128) NOT NULL UNIQUE,
    CONSTRAINT FK_BENHNHAN_MACSYT_CSYT FOREIGN KEY (MACSYT) REFERENCES C##QLKCB.CSYT(MACSYT)
);

-- Tao bang NHANVIEN
CREATE TABLE C##QLKCB.NHANVIEN (
    MANV CHAR(6) PRIMARY KEY,
    HOTEN NVARCHAR2(100) NOT NULL,
    PHAI NVARCHAR2(5) NOT NULL,
    CMND RAW(16) NOT NULL,
    NGAYSINH DATE NOT NULL,
    QUEQUAN NVARCHAR2(200) NOT NULL,
    SODT VARCHAR2(15) NOT NULL,
    MACSYT CHAR(4),
    VAITRO NVARCHAR2(20) NOT NULL,
    CHUYENKHOA CHAR(4),
    USERNAME VARCHAR2(128) NOT NULL UNIQUE,
    CONSTRAINT FK_NHANVIEN_CSYT_CSYT FOREIGN KEY (MACSYT) REFERENCES C##QLKCB.CSYT(MACSYT),
    CONSTRAINT FK_NHANVIEN_CHUYENKHOA_KHOA FOREIGN KEY (CHUYENKHOA) REFERENCES C##QLKCB.KHOA(MAKHOA)
);

-- Tao bang HSBA
CREATE TABLE C##QLKCB.HSBA (
    MAHSBA CHAR(8) PRIMARY KEY,
    MABN CHAR(7) NOT NULL,
    NGAY DATE DEFAULT SYSDATE,
    CHANDOAN NVARCHAR2(200),
    MABS CHAR(6) NOT NULL,
    MAKHOA CHAR(4) NOT NULL,
    MACSYT CHAR(4) NOT NULL,
    KETLUAN NVARCHAR2(200),
    CONSTRAINT FK_HSBA_MABN_BENHNHAN FOREIGN KEY (MABN) REFERENCES C##QLKCB.BENHNHAN(MABN),
    CONSTRAINT FK_HSBA_MABS_NHANVIEN FOREIGN KEY (MABS) REFERENCES C##QLKCB.NHANVIEN(MANV),
    CONSTRAINT FK_HSBA_MACSYT_CSYT FOREIGN KEY (MACSYT) REFERENCES C##QLKCB.CSYT(MACSYT),
    CONSTRAINT FK_HSBA_MAKHOA_KHOA FOREIGN KEY (MAKHOA) REFERENCES C##QLKCB.KHOA(MAKHOA)
);
/
-- Tao bang HSBA_DV 
CREATE TABLE C##QLKCB.HSBA_DV (
    MAHSBA CHAR(8),
    MADV CHAR(5),
    NGAY DATE DEFAULT SYSDATE,
    MAKTV CHAR(6) NOT NULL,
    KETQUA NVARCHAR2(200),
    PRIMARY KEY (MAHSBA, MADV, NGAY),
    CONSTRAINT PK_HSBADV PRIMARY KEY (MAHSBA, MADV, NGAY),
    CONSTRAINT FK_HSBADV_MAHSBA_HSBA FOREIGN KEY (MAHSBA) REFERENCES C##QLKCB.HSBA(MAHSBA),
    CONSTRAINT FK_HSBADV_MADV_DICHVU FOREIGN KEY (MADV) REFERENCES C##QLKCB.DICHVU(MADV),
    CONSTRAINT FK_HSBADV_MAKTV_NHANVIEN FOREIGN KEY (MAKTV) REFERENCES C##QLKCB.NHANVIEN(MANV) ON DELETE CASCADE
);
/
ALTER SESSION SET container = XEPDB1;
CREATE TABLE C##QLKCB.THONGBAO (NOIDUNG NVARCHAR2(500) NOT NULL, NGAYGIO TIMESTAMP DEFAULT SYSDATE, DIADIEM VARCHAR2(200) NOT NULL);
ALTER SESSION SET container = CDB$ROOT;

/
CREATE OR REPLACE PROCEDURE C##QLKCB.THEM_NHANVIEN (
    MANV_IN VARCHAR2,
    HOTEN_IN NVARCHAR2,
    PHAI_IN NVARCHAR2,
    CMND_IN VARCHAR2,
    NGAYSINH_IN DATE,
    QUEQUAN_IN NVARCHAR2,
    SODT_IN VARCHAR2,
    MACSYT_IN VARCHAR2,
    VAITRO_IN NVARCHAR2,
    CHUYENKHOA_IN VARCHAR2,
    USERNAME_IN VARCHAR2
)
IS
    stored_enc_ma RAW(16);
    stored_enc_key RAW(48);
    enc_ma_key RAW(16);
    enc_key RAW(32);
    inserted_cmnd RAW(16) := NULL;
    pad char(4) := 'KEYP';
    l_mod_cbc pls_integer := DBMS_CRYPTO.ENCRYPT_AES128
                            + DBMS_CRYPTO.CHAIN_CBC
                            + DBMS_CRYPTO.PAD_PKCS5;
    alg_grade pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
BEGIN
    enc_key := c##apdsgvkyp3s5v8y.LAY_KEY(MANV_IN, pad);
    IF enc_key IS NULL
    THEN
        enc_key := DBMS_CRYPTO.RANDOMBYTES(32);
        enc_ma_key := UTL_RAW.CAST_TO_RAW(MANV_IN || UTL_RAW.CAST_TO_VARCHAR2(UTL_RAW.REVERSE(UTL_RAW.CAST_TO_RAW(MANV_IN))) || pad); 
        stored_enc_ma := DBMS_CRYPTO.ENCRYPT(src => UTL_RAW.CAST_TO_RAW(MANV_IN),
                                                    TYP => l_mod_cbc,
                                                    key => enc_ma_key); 
        stored_enc_key := DBMS_CRYPTO.ENCRYPT(src => enc_key,
                                                TYP => l_mod_cbc,
                                                key => enc_ma_key);
        INSERT INTO c##apdsgvkyp3s5v8y.D7711589BB9785CAAFFF31C1143E9 (D3B75866A3178, B261E86AE5213) VALUES (stored_enc_ma, stored_enc_key);
    END IF;
    
    inserted_cmnd := DBMS_CRYPTO.ENCRYPT(src => UTL_RAW.CAST_TO_RAW(CMND_IN),
                                            TYP => alg_grade,
                                            key => enc_key);
    INSERT INTO C##QLKCB.NHANVIEN
    VALUES (
        MANV_IN, HOTEN_IN, PHAI_IN, inserted_cmnd, NGAYSINH_IN, QUEQUAN_IN, SODT_IN, MACSYT_IN, VAITRO_IN, CHUYENKHOA_IN, USERNAME_IN
    );
END;
/

CREATE OR REPLACE PROCEDURE C##QLKCB.THEM_BENHNHAN (
    MABN_IN VARCHAR2,
    TENBN_IN NVARCHAR2,
    MACSYT_IN VARCHAR2,
    CMND_IN VARCHAR2,
    NGAYSINH_IN DATE,
    SONHA_IN VARCHAR2,
    TENDUONG_IN NVARCHAR2,
    QUANHUYEN_IN NVARCHAR2,
    TINHTP_IN NVARCHAR2,
    TIENSUBENH_IN NVARCHAR2,
    TIENSUBENHGD_IN NVARCHAR2,
    DIUNGTHUOC_IN NVARCHAR2,
    USERNAME_IN VARCHAR2
)
IS
    stored_enc_ma RAW(16);
    stored_enc_key RAW(48);
    enc_ma_key RAW(16);
    enc_key RAW(32);
    pad char(2) := 'KE';
    inserted_cmnd RAW(16) := NULL;
    l_mod_cbc pls_integer := DBMS_CRYPTO.ENCRYPT_AES128
                            + DBMS_CRYPTO.CHAIN_CBC
                            + DBMS_CRYPTO.PAD_PKCS5;
    alg_grade pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
BEGIN
    enc_key := c##apdsgvkyp3s5v8y.LAY_KEY(MABN_IN, pad);
    IF enc_key IS NULL
    THEN
        enc_key := DBMS_CRYPTO.RANDOMBYTES(32);
        enc_ma_key := UTL_RAW.CAST_TO_RAW(MABN_IN || UTL_RAW.CAST_TO_VARCHAR2(UTL_RAW.REVERSE(UTL_RAW.CAST_TO_RAW(MABN_IN))) || pad); 
        stored_enc_ma := DBMS_CRYPTO.ENCRYPT(src => UTL_RAW.CAST_TO_RAW(MABN_IN),
                                                    TYP => l_mod_cbc,
                                                    key => enc_ma_key); 
        stored_enc_key := DBMS_CRYPTO.ENCRYPT(src => enc_key,
                                                TYP => l_mod_cbc,
                                                key => enc_ma_key);
        INSERT INTO c##apdsgvkyp3s5v8y.D7711589BB9785CAAFFF31C1143E9 (D3B75866A3178, B261E86AE5213) VALUES (stored_enc_ma, stored_enc_key);
    END IF;
    
    inserted_cmnd := DBMS_CRYPTO.ENCRYPT(src => UTL_RAW.CAST_TO_RAW(CMND_IN),
                                            TYP => alg_grade,
                                            key => enc_key);
    INSERT INTO C##QLKCB.BENHNHAN
    VALUES (
        MABN_IN,
        TENBN_IN,
        MACSYT_IN,
        inserted_cmnd,
        NGAYSINH_IN,
        SONHA_IN,
        TENDUONG_IN,
        QUANHUYEN_IN,
        TINHTP_IN,
        TIENSUBENH_IN,
        TIENSUBENHGD_IN,
        DIUNGTHUOC_IN,
        USERNAME_IN
    );
END;
/
INSERT INTO C##QLKCB.CSYT VALUES ('CS01', N'Cơ sở 1', 'Quan 1, TPHCM', '0901010101');
INSERT INTO C##QLKCB.CSYT VALUES ('CS02', N'Cơ sở 2', 'Quan 2, TPHCM', '0902020202');
INSERT INTO C##QLKCB.CSYT VALUES ('CS03', N'Cơ sở 3', 'Quan 3, TPHCM', '0903030303');
INSERT INTO C##QLKCB.CSYT VALUES ('CS04', N'Cơ sở 4', 'Quan 4, TPHCM', '0904040404');
INSERT INTO C##QLKCB.CSYT VALUES ('CS05', N'Cơ sở 5', 'Quan 5, TPHCM', '0905050505');
INSERT INTO C##QLKCB.CSYT VALUES ('CS06', N'Cơ sở 6', 'Quan 6, TPHCM', '0906060606');
INSERT INTO C##QLKCB.CSYT VALUES ('CS07', N'Cơ sở 7', 'Quan 7, TPHCM', '0907070707');
INSERT INTO C##QLKCB.CSYT VALUES ('CS08', N'Cơ sở 8', 'Quan 8, TPHCM', '0908080808');
INSERT INTO C##QLKCB.CSYT VALUES ('CS09', N'Cơ sở 9', 'Quan 9, TPHCM', '0909090909');

INSERT INTO C##QLKCB.KHOA VALUES ('K000', N'Khoa thần kinh');
INSERT INTO C##QLKCB.KHOA VALUES ('K001', N'Khoa chỉnh hình');
INSERT INTO C##QLKCB.KHOA VALUES ('K002', N'Khoa ung bướu');
INSERT INTO C##QLKCB.KHOA VALUES ('K003', N'Khoa trẻ em');
INSERT INTO C##QLKCB.KHOA VALUES ('K004', N'Khoa tai mũi họng');
INSERT INTO C##QLKCB.KHOA VALUES ('K005', N'Khoa mắt');
INSERT INTO C##QLKCB.KHOA VALUES ('K006', N'Khoa ngoại tổng hợp');
INSERT INTO C##QLKCB.KHOA VALUES ('K007', N'Khoa tim mạch');

INSERT INTO C##QLKCB.DICHVU VALUES ('DV001', N'Xét nghiệm máu');
INSERT INTO C##QLKCB.DICHVU VALUES ('DV002', N'Chụp hình X quang');
INSERT INTO C##QLKCB.DICHVU VALUES ('DV003', N'Test Covid');
INSERT INTO C##QLKCB.DICHVU VALUES ('DV004', N'Siêu âm');
INSERT INTO C##QLKCB.DICHVU VALUES ('DV005', N'Vật lý trị liệu');
/
BEGIN
    C##QLKCB.THEM_NHANVIEN('NV0001', N'Nguyễn Văn A', 'Nam', '12345678', To_DATE('1990/02/02', 'yyyy/mm/dd'), 'TPHCM', '123456789', 'CS01', 'Thanh tra', '', 'C##NV0001');
    C##QLKCB.THEM_NHANVIEN('NV0002', N'Hồ Hoàng Minh', N'Nữ', '414125353', To_DATE('1990/07/05', 'yyyy/mm/dd'), 'TPHCM', '123456789', 'CS02', 'Thanh tra', '', 'C##NV0002');
    C##QLKCB.THEM_NHANVIEN('NV0003', N'Trần Văn Trịnh', 'Nam', '746463452', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '123456789', 'CS03', 'Thanh tra', '', 'C##NV0003');
    C##QLKCB.THEM_NHANVIEN('NV0004', N'Hồ Thị Nữ', N'Nữ', '746515673', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '098765434', 'CS01', N'Cơ sở y tế', '', 'C##NV0004');
    C##QLKCB.THEM_NHANVIEN('NV0005', N'Trần Văn Tiến', 'Nam', '631435566', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '098765233', 'CS02', N'Cơ sở y tế', '', 'C##NV0005');
    C##QLKCB.THEM_NHANVIEN('NV0006', N'Hồ Hoài Ngọc', N'Nữ', '255623536', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '098765233', 'CS03', N'Cơ sở y tế', '', 'C##NV0006');
    C##QLKCB.THEM_NHANVIEN('NV0007', N'Trần Thị Uyên', N'Nữ', '512532523', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '098765233', 'CS04', N'Cơ sở y tế', '', 'C##NV0007'); 
    C##QLKCB.THEM_NHANVIEN('NV0008', N'Nguyễn Văn Bền', N'Nam', '635626256', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS01', N'Y/Bác sĩ', 'K001', 'C##NV0008');
    C##QLKCB.THEM_NHANVIEN('NV0009', N'Nguy?n Văn Chước', N'Nam', '734561244', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987673465', 'CS01', N'Y/Bác sĩ', 'K002', 'C##NV0009');
    C##QLKCB.THEM_NHANVIEN('NV0010', N'Nguyễn Thị Mo', N'Nữ', '13144256', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS02', N'Y/Bác sĩ', 'K004', 'C##NV0010');
    C##QLKCB.THEM_NHANVIEN('NV0011', N'Nguyễn Lợi', N'Nam', '426241156', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS02', N'Y/Bác sĩ', 'K003', 'C##NV0011');
    C##QLKCB.THEM_NHANVIEN('NV0012', N'Nguyễn Văn Tô', N'Nam', '637567212', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS02', N'Y/Bác sĩ', 'K002', 'C##NV0012');
    C##QLKCB.THEM_NHANVIEN('NV0013', N'Trương Nhàn', N'Nam', '62512345', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS01', N'Nghiên cứu', 'K001', 'C##NV0013');
    C##QLKCB.THEM_NHANVIEN('NV0014', N'Hồ Thiết Ý', N'Nữ', '7473623123', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS01', N'Nghiên cứu', 'K001', 'C##NV0014');
    C##QLKCB.THEM_NHANVIEN('NV0015', N'Nguyễn Văn Linh', N'Nam', '63568586', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS02', N'Nghiên cứu', 'K005', 'C##NV0015');
END;
/
SELECT * FROM C##QLKCB.NHANVIEN;
/
BEGIN
    C##QLKCB.THEM_BENHNHAN('BN00001', N'Lâm Hoàng Phúc', 'CS01', '12132244', To_DATE('2001/03/02', 'yyyy/mm/dd'), '125', N'Mai Chí Thọ', N'Quận 1', 'TPHCM', N'Hở van tiêm', N'Ung thư', N'Thuốc kháng sinh', 'C##BN00001');
    C##QLKCB.THEM_BENHNHAN('BN00002', 'Hồ Nhật Linh', 'CS01', '12132554', To_DATE('2001/05/02', 'yyyy/mm/dd'), '672', N'Nguyễn Huệ', N'Quận 3', 'TPHCM', N'Viêm mũi dị ứng', N'Tiểu đường', N'Vitamin dạng tiêm', 'C##BN00002');
    C##QLKCB.THEM_BENHNHAN('BN00003', 'Nguyễn Bắc Bình', 'CS02', '113224534', To_DATE('2001/10/02', 'yyyy/mm/dd'), '278', N'Trần Phước', N'Cao Lãnh', N'Đồng Tháp', N'Đau nửa dầu', N'Huyết áp cao', '', 'C##BN00003');
    C##QLKCB.THEM_BENHNHAN('BN00004', 'Cáo Mao Đào', 'CS02', '121322644', To_DATE('2001/03/02', 'yyyy/mm/dd'), '123', N'Tôn Đức Thắng', N'Quận 1', 'TPHCM', N'Mề đay', N'Viêm khớp', 'Insulin', 'C##BN00004');
END;
/
SELECT * FROM C##QLKCB.BENHNHAN;
/
INSERT INTO C##QLKCB.HSBA VALUES ('HS000001', 'BN00001', sysdate, N'HIV/AIDS', 'NV0008', 'K001', 'CS01', N'Âm tính');
INSERT INTO C##QLKCB.HSBA VALUES ('HS000002', 'BN00002', sysdate, N'Bệnh viêm mũi', 'NV0009', 'K001', 'CS01', 'Bệnh nhân bị viêm mũi nặng');
INSERT INTO C##QLKCB.HSBA VALUES ('HS000003', 'BN00003', sysdate, N'Đau nửa đầu', 'NV0010', 'K004', 'CS02', N'Ung thư não');
INSERT INTO C##QLKCB.HSBA VALUES ('HS000004', 'BN00004', sysdate, N'Bệnh gout', 'NV0011', 'K003', 'CS02', N'Bị gout nhẹ');
INSERT INTO C##QLKCB.HSBA VALUES ('HS000005', 'BN00001', sysdate, N'Tiểu đường', 'NV0012', 'K002', 'CS02', N'Tiểu đường trong thời kì mang thai');
/
SELECT * FROM C##QLKCB.HSBA;
/
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000001', 'DV001', sysdate, 'NV0008', N'Bình thường');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000002', 'DV003', sysdate, 'NV0009', N'Âm tính');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000002', 'DV002', sysdate, 'NV0009', N'Thành mũi biến dạng');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000003', 'DV002', sysdate, 'NV0012', N'Phát hiện tế bào ung thư');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000004', 'DV005', sysdate, 'NV0010', N'Tiến triển tốt');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000005', 'DV001', sysdate, 'NV0014', N'Đường huyết cao');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000005', 'DV004', sysdate, 'NV0015', N'Bình thường');
/
SELECT * FROM C##QLKCB.HSBA_DV;
/
CREATE OR REPLACE FUNCTION C##QLKCB.LAY_CMND_NHANVIEN (
    MA_IN VARCHAR2
)
RETURN VARCHAR2
IS
    dec_key RAW(48) := NULL;
    pad char(4) := 'KEYP';
    RAW_CMND RAW(16);
    alg_grade       pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
BEGIN
    dec_key := c##apdsgvkyp3s5v8y.LAY_KEY(MA_IN, pad);
    IF dec_key IS NULL
    THEN
        RETURN NULL;
    ELSE
        SELECT CMND
        INTO RAW_CMND
        FROM NHANVIEN
        WHERE MANV = MA_IN;
        RETURN UTL_RAW.CAST_TO_VARCHAR2(DBMS_CRYPTO.DECRYPT(src => RAW_CMND,
                                    TYP => alg_grade,
                                    key => dec_key));
    END IF;
END;
/
CREATE OR REPLACE FUNCTION C##QLKCB.LAY_CMND_BENHNHAN (
    MA_IN VARCHAR2
)
RETURN VARCHAR2
IS
    dec_key RAW(48) := NULL;
    pad char(2) := 'KE';
    RAW_CMND RAW(16);
    alg_grade       pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
BEGIN
    dec_key := c##apdsgvkyp3s5v8y.LAY_KEY(MA_IN, pad);
    IF dec_key IS NULL
    THEN
        RETURN NULL;
    ELSE
        SELECT CMND
        INTO RAW_CMND
        FROM BENHNHAN
        WHERE MABN = MA_IN;
        RETURN UTL_RAW.CAST_TO_VARCHAR2(DBMS_CRYPTO.DECRYPT(src => RAW_CMND,
                                    TYP => alg_grade,
                                    key => dec_key));
    END IF;
END;
/
-------------------------------------------------------------
CREATE OR REPLACE VIEW C##QLKCB.BENH_NHAN_XEM_BENH_NHAN AS
SELECT 
    bn.MABN AS MABN,
    bn.TENBN AS TENBN,
    bn.MACSYT AS MACSYT,
    C##QLKCB.LAY_CMND_BENHNHAN(bn.MABN) AS CMND,
    bn.NGAYSINH AS NGAYSINH,
    bn.SONHA AS SONHA,
    bn.TENDUONG AS TENDUONG,
    bn.QUANHUYEN AS QUANHUYEN,
    bn.TINHTP AS TINHTP,
    bn.TIENSUBENH AS TIENSUBENH,
    bn.TIENSUBENHGD AS TIENSUBENHGD,
    bn.DIUNGTHUOC AS DIUNGTHUOC
FROM C##QLKCB.BENHNHAN bn
WHERE bn.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER');
/
CREATE ROLE C##BENHNHAN;
/
GRANT CREATE SESSION TO C##BENHNHAN;
/
GRANT SELECT ON C##QLKCB.BENH_NHAN_XEM_BENH_NHAN TO C##BENHNHAN;
/
CREATE OR REPLACE PROCEDURE C##QLKCB.CHINHSUA_BENHNHAN_CMND (
    CMND VARCHAR2
)
IS
    dec_key RAW(48) := NULL;
    raw_cmnd RAW(16) := NULL;
    pad char(2) := 'KE';
    ma_bn CHAR(7);
    alg_grade       pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
BEGIN
    SELECT MABN
    INTO ma_bn
    FROM C##QLKCB.BENH_NHAN_XEM_BENH_NHAN;
    dec_key := c##apdsgvkyp3s5v8y.LAY_KEY(ma_bn, pad);
    IF dec_key IS NOT NULL
    THEN
        
        raw_cmnd := DBMS_CRYPTO.ENCRYPT(src => UTL_RAW.CAST_TO_RAW(CMND),
                                    TYP => alg_grade,
                                    key => dec_key);
        UPDATE C##QLKCB.BENHNHAN SET CMND = raw_cmnd WHERE MABN = ma_bn;
    END IF;
END;
/
GRANT EXECUTE ON C##QLKCB.CHINHSUA_BENHNHAN_CMND TO C##BENHNHAN;
/
GRANT UPDATE(
    TENBN,
    NGAYSINH,
    SONHA,
    TENDUONG,
    QUANHUYEN,
    TINHTP
) ON C##QLKCB.BENH_NHAN_XEM_BENH_NHAN TO C##BENHNHAN;
/
CREATE OR REPLACE PROCEDURE SYS.create_user_for_table_benh_nhan
AS n NUMBER;
BEGIN
    n := 0;
    FOR u IN (
        SELECT USERNAME
        FROM C##QLKCB.BENHNHAN
    )
    LOOP
        SELECT COUNT(*) INTO n FROM dba_users WHERE USERNAME = u.USERNAME;
        IF (n = 0) THEN
            EXECUTE IMMEDIATE ('CREATE USER '||u.USERNAME||' IDENTIFIED BY ' || u.USERNAME);
        END IF;
        EXECUTE IMMEDIATE ('GRANT C##BENHNHAN TO ' || u.USERNAME);
    END LOOP;
END;
/
EXEC SYS.create_user_for_table_benh_nhan;

-------------------------------------
------------------------------------
----NHAN VIEN-----------------------
-----------------------------------
/
CREATE OR REPLACE PROCEDURE SYS.grant_role_to_user_from_nhan_vien (
    roleIntable VARCHAR2,
    rolename VARCHAR2
    )
AS 
BEGIN
    FOR i IN (select USERNAME from C##QLKCB.nhanvien where vaitro = roleIntable)
    LOOP
        EXECUTE IMMEDIATE ('GRANT '||rolename||' to '||i.USERNAME);
    END LOOP;
END;
/

--View cho nhân viên
CREATE OR REPLACE VIEW C##QLKCB.NHAN_VIEN_XEM_NHAN_VIEN AS
SELECT 
    nv.MANV AS MANV,
    nv.HOTEN AS HOTEN,
    nv.PHAI AS PHAI,
    C##QLKCB.LAY_CMND_NHANVIEN(nv.MANV) AS CMND,
    nv.NGAYSINH AS NGAYSINH,
    nv.QUEQUAN AS QUEQUAN,
    nv.SODT AS SODT,
    nv.MACSYT AS MACSYT,
    nv.VAITRO AS VAITRO,
    nv.CHUYENKHOA AS CHUYENKHOA
FROM C##QLKCB.NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER');
/
CREATE ROLE C##NHANVIEN;
/
GRANT SELECT ON C##QLKCB.NHAN_VIEN_XEM_NHAN_VIEN TO C##NHANVIEN;
/
GRANT CREATE SESSION TO C##NHANVIEN;
/
GRANT SET CONTAINER to C##NHANVIEN container=all;
/
GRANT UPDATE(
    HOTEN,
    PHAI,
    NGAYSINH,
    QUEQUAN,
    SODT
) ON C##QLKCB.NHAN_VIEN_XEM_NHAN_VIEN TO C##NHANVIEN;
/
CREATE OR REPLACE PROCEDURE C##QLKCB.CHINHSUA_NHANVIEN_CMND (
    CMND VARCHAR2
)
IS
    dec_key RAW(48) := NULL;
    raw_cmnd RAW(16) := NULL;
    pad char(4) := 'KEYP';
    alg_grade       pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
    ma_nv CHAR(6);
BEGIN
    SELECT MANV
    INTO ma_nv
    FROM C##QLKCB.NHAN_VIEN_XEM_NHAN_VIEN;
    dec_key := c##apdsgvkyp3s5v8y.LAY_KEY(ma_nv, pad);
    IF dec_key IS NOT NULL
    THEN
        raw_cmnd := DBMS_CRYPTO.ENCRYPT(src => UTL_RAW.CAST_TO_RAW(CMND),
                                    TYP => alg_grade,
                                    key => dec_key);
        UPDATE C##QLKCB.NHANVIEN SET CMND = raw_cmnd WHERE MANV = ma_nv;
    END IF;
END;
/
GRANT EXECUTE ON C##QLKCB.CHINHSUA_NHANVIEN_CMND TO C##NHANVIEN;
/
CREATE OR REPLACE PROCEDURE SYS.create_user_for_table_nhan_vien
AS n NUMBER;
BEGIN
    n := 0;
    FOR u IN (
        SELECT USERNAME
        FROM C##QLKCB.NHANVIEN
    )
    LOOP
        SELECT COUNT(*) INTO n FROM dba_users WHERE USERNAME = u.USERNAME;
        IF (n = 0) THEN
            EXECUTE IMMEDIATE ('CREATE USER '||u.USERNAME||' IDENTIFIED BY ' || u.USERNAME);
            --EXECUTE IMMEDIATE ('GRANT CREATE SESSION TO '||u.USERNAME);
        END IF;
        EXECUTE IMMEDIATE ('GRANT C##NHANVIEN TO ' || u.USERNAME);
    END LOOP;
END;
/
EXEC SYS.create_user_for_table_nhan_vien;
/
-------------------------------------------------------------
--Thanh tra
CREATE ROLE C##THANHTRA;
/
EXEC SYS.grant_select('C##QLKCB', 'C##THANHTRA');
/
EXEC SYS.grant_role_to_user_from_nhan_vien(N'Thanh tra', 'C##THANHTRA');
/
CREATE OR REPLACE VIEW C##QLKCB.THANH_TRA_XEM_BENH_NHAN AS
SELECT 
    bn.MABN AS MABN,
    bn.TENBN AS TENBN,
    bn.MACSYT AS MACSYT,
    C##QLKCB.LAY_CMND_BENHNHAN(bn.MABN) AS CMND,
    bn.NGAYSINH AS NGAYSINH,
    bn.SONHA AS SONHA,
    bn.TENDUONG AS TENDUONG,
    bn.QUANHUYEN AS QUANHUYEN,
    bn.TINHTP AS TINHTP,
    bn.TIENSUBENH AS TIENSUBENH,
    bn.TIENSUBENHGD AS TIENSUBENHGD,
    bn.DIUNGTHUOC AS DIUNGTHUOC
FROM C##QLKCB.BENHNHAN bn;
/
CREATE OR REPLACE VIEW C##QLKCB.THANH_TRA_XEM_NHAN_VIEN AS
SELECT 
    nv.MANV AS MANV,
    nv.HOTEN AS HOTEN,
    nv.PHAI AS PHAI,
    C##QLKCB.LAY_CMND_NHANVIEN(nv.MANV) AS CMND,
    nv.NGAYSINH AS NGAYSINH,
    nv.QUEQUAN AS QUEQUAN,
    nv.SODT AS SODT,
    nv.MACSYT AS MACSYT,
    nv.VAITRO AS VAITRO,
    nv.CHUYENKHOA AS CHUYENKHOA
FROM C##QLKCB.NHANVIEN nv;
/
GRANT SELECT ON C##QLKCB.THANH_TRA_XEM_NHAN_VIEN TO C##THANHTRA;
/
GRANT SELECT ON C##QLKCB.THANH_TRA_XEM_BENH_NHAN TO C##THANHTRA;
/
GRANT SELECT ON C##QLKCB.HSBA TO C##THANHTRA;
/
GRANT SELECT ON C##QLKCB.HSBA_DV TO C##THANHTRA;
/
GRANT SELECT ON C##QLKCB.CSYT TO C##THANHTRA;
/
-------------
--Cơ sở y tế
CREATE ROLE C##COSOYTE;
/
CREATE OR REPLACE VIEW C##QLKCB.DICH_VU_IDS
AS
SELECT dv.MADV
FROM C##QLKCB.DICHVU dv;
/
CREATE OR REPLACE VIEW C##QLKCB.NHAN_VIEN_IDS
AS
SELECT nv.MANV
FROM C##QLKCB.NHANVIEN nv;
/
CREATE OR REPLACE VIEW C##QLKCB.BENH_NHAN_IDS
AS
SELECT bn.MABN
FROM C##QLKCB.BENHNHAN bn;
/
CREATE OR REPLACE VIEW C##QLKCB.BAC_SI_IDS
AS
SELECT nv.MANV
FROM C##QLKCB.NHANVIEN nv
WHERE nv.VAITRO = N'Y/Bác sĩ';
/
CREATE OR REPLACE VIEW C##QLKCB.KHOA_IDS
AS
SELECT k.MAKHOA
FROM C##QLKCB.KHOA k;
/
CREATE OR REPLACE VIEW C##QLKCB.CSYT_IDS
AS
SELECT cs.MACSYT
FROM C##QLKCB.CSYT cs;
/
GRANT SELECT ON C##QLKCB.DICH_VU_IDS TO C##COSOYTE;
/
GRANT SELECT ON C##QLKCB.NHAN_VIEN_IDS TO C##COSOYTE;
/
GRANT SELECT ON C##QLKCB.BENH_NHAN_IDS TO C##COSOYTE;
/
GRANT SELECT ON C##QLKCB.BAC_SI_IDS TO C##COSOYTE;
/
GRANT SELECT ON C##QLKCB.KHOA_IDS TO C##COSOYTE;
/
GRANT SELECT ON C##QLKCB.CSYT_IDS TO C##COSOYTE;
/
GRANT SELECT, DELETE, INSERT ON C##QLKCB.HSBA TO C##COSOYTE;
/
GRANT SELECT, DELETE, INSERT ON C##QLKCB.HSBA_DV TO C##COSOYTE;
/
EXEC SYS.grant_role_to_user_from_nhan_vien(N'Cơ sở y tế', 'C##COSOYTE');
/
----------
--Bác sĩ
CREATE OR REPLACE VIEW C##QLKCB.VIEW_BAC_SI_XEM_BENH_NHAN AS
SELECT
    bn.MABN,
    bn.TENBN,
    bn.MACSYT,
    C##QLKCB.LAY_CMND_BENHNHAN(bn.MABN) AS CMND,
    bn.NGAYSINH,
    bn.SONHA,
    bn.TENDUONG,
    bn.QUANHUYEN,
    bn.TINHTP,
    bn.TIENSUBENH,
    bn.TIENSUBENHGD,
    bn.DIUNGTHUOC
FROM C##QLKCB.BENHNHAN bn
	JOIN C##QLKCB.HSBA hsba ON hsba.MABN = bn.MABN
	JOIN C##QLKCB.NHANVIEN nv ON hsba.MABS = nv.MANV
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER');
/
CREATE ROLE C##BACSI;
/
GRANT SELECT ON C##QLKCB.HSBA TO C##BACSI;
/
GRANT SELECT ON C##QLKCB.HSBA_DV TO C##BACSI;
/
GRANT SELECT ON C##QLKCB.VIEW_BAC_SI_XEM_BENH_NHAN TO C##BACSI;
/
EXEC SYS.grant_role_to_user_from_nhan_vien(N'Y/Bác sĩ', 'C##BACSI');
/
-------------
--Nghiên cứu
CREATE ROLE C##NGHIENCUU;
/
GRANT SELECT ON C##QLKCB.HSBA TO C##NGHIENCUU;
/
GRANT SELECT ON C##QLKCB.HSBA_DV TO C##NGHIENCUU;
/
EXEC SYS.grant_role_to_user_from_nhan_vien(N'Nghiên cứu', 'C##NGHIENCUU');
/
--Vị từ VPD cho bảng HSBA
--GRANT EXECUTE ON C##QLKCB.LAY_VI_TU_TREN_HSBA TO C##NHANVIEN;
--GRANT EXECUTE ON C##QLKCB.LAY_VI_TU_TREN_HSBA_DV TO C##NHANVIEN;
CREATE OR REPLACE FUNCTION C##QLKCB.LAY_VI_TU_TREN_HSBA (
    schema_name     IN VARCHAR2 DEFAULT NULL,
    object_name     IN VARCHAR2 DEFAULT NULL)
RETURN VARCHAR2
AS
    currentUser  VARCHAR(128) := SYS_CONTEXT('userenv', 'SESSION_USER');
    ma_nv        CHAR(6);
    vai_tro      NVARCHAR2(20);
    ma_csyt      CHAR(4);
    ma_khoa      CHAR(4);
BEGIN
	IF SYS_CONTEXT('userenv', 'ISDBA') = 'TRUE'
	THEN
		RETURN '1 = 1';
	END IF;

    SELECT MANV, VAITRO, MACSYT, CHUYENKHOA
    INTO ma_nv, vai_tro, ma_csyt, ma_khoa
    FROM C##QLKCB.NHANVIEN nv
    WHERE nv.USERNAME = currentUser;
    
    IF ma_nv IS NULL
    THEN
        RETURN '1 = 0';
    END IF;
    
	IF vai_tro = N'Thanh tra'
	THEN 
		RETURN '1 = 1';
	END IF;
    --“Thanh tra”, “Cơ sở y tế”, “Y sĩ/bác sĩ”, “Nghiên cứu”.
    IF vai_tro = N'Cơ sở y tế'
    THEN
		RETURN ('MACSYT = ''' || ma_csyt || ''' AND TO_NUMBER(TO_CHAR(NGAY, ''DD'')) > 4 AND TO_NUMBER(TO_CHAR(NGAY, ''DD'')) < 28 ' || 
        'AND to_char(NGAY, ''mm'') = to_char(sysdate, ''mm'') AND to_char(NGAY, ''yyyy'') = to_char(sysdate, ''yyyy'')');
    END IF;
    
    IF vai_tro = N'Y/Bác sĩ'
    THEN
        RETURN ('MABS = ''' || ma_nv || '''');
    END IF;
    
    IF vai_tro = N'Nghiên cứu'
    THEN
        RETURN ('MACSYT = ''' || ma_csyt || ''' AND MAKHOA = ''' || ma_khoa || '''');
    END IF;
    
    RETURN '1 = 0';
END;
/
--Vị từ VPD cho bảng HSBA_DV
CREATE OR REPLACE FUNCTION C##QLKCB.LAY_VI_TU_TREN_HSBA_DV (
    schema_name     IN VARCHAR2 DEFAULT NULL,
    object_name     IN VARCHAR2 DEFAULT NULL)
RETURN VARCHAR2
AS
    currentUser  VARCHAR(128) := SYS_CONTEXT('userenv', 'SESSION_USER');
    ma_nv        CHAR(6);
    vai_tro      NVARCHAR2(20);
    ma_csyt      CHAR(4);
    ma_khoa      CHAR(4);
	ma_hsba 	 CHAR(8);
BEGIN

	IF SYS_CONTEXT('userenv', 'ISDBA') = 'TRUE'
	THEN
		RETURN '1 = 1';
	END IF;
	
    SELECT MANV, VAITRO, MACSYT, CHUYENKHOA
    INTO ma_nv, vai_tro, ma_csyt, ma_khoa
    FROM C##QLKCB.NHANVIEN nv
    WHERE nv.USERNAME = currentUser;
    
    IF ma_nv IS NULL
    THEN
        RETURN '1 = 0';
    END IF;
    
	IF vai_tro = N'Thanh tra'
	THEN 
		RETURN '1 = 1';
	END IF;
	
    --“Thanh tra”, “Cơ sở y tế”, “Y sĩ/bác sĩ”, “Nghiên cứu”.
    IF vai_tro = N'Cơ sở y tế'
    THEN
		RETURN ('to_char(NGAY, ''mm'') = to_char(sysdate, ''mm'') AND to_char(NGAY, ''yyyy'') = to_char(sysdate, ''yyyy'') ' ||
                'AND TO_NUMBER(TO_CHAR(HSBA_DV.NGAY, ''DD'')) > 4 AND TO_NUMBER(TO_CHAR(HSBA_DV.NGAY, ''DD'')) < 28 ' ||
                'AND EXISTS (SELECT hsba.MAHSBA FROM C##QLKCB.HSBA hsba WHERE hsba.MAHSBA = C##QLKCB.HSBA_DV.MAHSBA AND hsba.MACSYT = ''' || ma_csyt || ''')');
        
    END IF;
    
    IF vai_tro = N'Y/Bác sĩ'
    THEN
        RETURN ('EXISTS (SELECT hsba.MAHSBA FROM C##QLKCB.HSBA hsba WHERE hsba.MAHSBA = C##QLKCB.HSBA_DV.MAHSBA AND hsba.MABS = ''' || ma_nv || ''')');
    END IF;
    
    IF vai_tro = N'Nghiên cứu'
    THEN
		RETURN ('EXISTS (SELECT hsba.MAHSBA FROM C##QLKCB.HSBA hsba WHERE hsba.MAHSBA = C##QLKCB.HSBA_DV.MAHSBA AND hsba.MACSYT = ''' || ma_csyt ||
		''' AND hsba.MAKHOA = ''' || ma_khoa || ''')');
    END IF;
    
    RETURN '1 = 0';
END;
/
BEGIN
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'C##QLKCB',
        object_name     => 'HSBA',
        policy_name     => 'HSBA_NV',
        function_schema => 'C##QLKCB',
        policy_function => 'LAY_VI_TU_TREN_HSBA',
        statement_types => 'SELECT,INSERT,DELETE',
        update_check    => TRUE);
    DBMS_RLS.ADD_POLICY(
        object_schema   => 'C##QLKCB',
        object_name     => 'HSBA_DV',
        policy_name     => 'HSBA_DV_NV',
        function_schema => 'C##QLKCB',
        policy_function => 'LAY_VI_TU_TREN_HSBA_DV',
        statement_types => 'SELECT,INSERT,DELETE',
        update_check    => TRUE);
    COMMIT;
END;
/
CREATE OR REPLACE PROCEDURE SYS.LAY_VAI_TRO (
    vai_tro     OUT NVARCHAR2)
IS
    dem_bn      INT := 0;
    currentUser VARCHAR(128) := SYS_CONTEXT('userenv', 'SESSION_USER');
BEGIN
    IF SYS_CONTEXT('userenv', 'ISDBA') = 'TRUE'
	THEN
		vai_tro := 'SYS';
        RETURN;
	END IF;
    
    SELECT COUNT(bn.MABN)
    INTO dem_bn
    FROM C##QLKCB.BENH_NHAN_XEM_BENH_NHAN bn;
    
    IF dem_bn > 0
    THEN
        vai_tro := 'BN';
        RETURN;
    END IF;
    
    SELECT nv.VAITRO
    INTO vai_tro
    FROM C##QLKCB.NHAN_VIEN_XEM_NHAN_VIEN nv;
    
    IF vai_tro IS NOT NULL
    THEN
        RETURN;
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        vai_tro := NULL;
END;
/
GRANT EXECUTE ON SYS.LAY_VAI_TRO TO C##BENHNHAN;
/
GRANT EXECUTE ON SYS.LAY_VAI_TRO TO C##NHANVIEN;

/
--SELECT * FROM dba_policies;
--OLS
EXEC LBACSYS.CONFIGURE_OLS;
EXEC LBACSYS.OLS_ENFORCEMENT.ENABLE_OLS;
GRANT INHERIT PRIVILEGES ON USER SYS TO LBACSYS;
ALTER USER LBACSYS ACCOUNT UNLOCK IDENTIFIED BY password;
ALTER SESSION SET container = XEPDB1;
GRANT SELECT ON C##QLKCB.THONGBAO TO C##NHANVIEN;
/
--SELECT * FROM C##QLKCB.THONGBAO;
--SELECT * FROM ALL_SA_TABLE_POLICIES;
--SELECT * FROM ALL_SA_USERS;
--SELECT * FROM DBA_SA_LABELS;
--SELECT * FROM DBA_SA_LEVELS;
BEGIN
	--Tạo chính sách
	SA_SYSDBA.CREATE_POLICY (
	policy_name      => 'emp_ols_pol',
	column_name      => 'ols_col');
	--Tạo level
	--Tạo level cho giám đốc sở
	sa_components.create_level
		(policy_name    => 'emp_ols_pol',
		long_name      => 'Giam doc so',
		short_name     => 'GDS',
		level_num      => 9000);
	--Tạo level cho giám đốc cơ sở y tế
	sa_components.create_level
		(policy_name    => 'emp_ols_pol',
		long_name      => 'Giam doc co so y te',
		short_name     => 'GDCSYT',
		level_num      => 8000);
	--Tạo level cho y bác sĩ
	sa_components.create_level
		(policy_name    => 'emp_ols_pol',
		long_name      => 'Y bac si',
		short_name     => 'YBS',
		level_num      => 7000);
		
	--Tạo compartments
	sa_components.create_compartment
		(policy_name    => 'emp_ols_pol',
		long_name      => 'Điều trị ngoại trú',
		short_name     => 'ngoai',
		comp_num       => 1000);
	sa_components.create_compartment
		(policy_name    => 'emp_ols_pol',
		 long_name      => 'Điều trị nội trú',
		short_name     => 'noi',
		comp_num       => 100);
	sa_components.create_compartment
		(policy_name    => 'emp_ols_pol',
		long_name      => 'Điều trị chuyên sâu',
		short_name     => 'sau',
		comp_num       => 10);  
	
	--Tạo groups
	sa_components.CREATE_GROUP   
		(policy_name    => 'emp_ols_pol',
		long_name      => 'Trung tâm',
		short_name     => 'tt',
		group_num      => 100);
	sa_components.CREATE_GROUP  
		(policy_name    => 'emp_ols_pol',
		long_name      => 'Cận trung tâm',
		short_name     => 'ctt',
		group_num      => 110);
	sa_components.CREATE_GROUP 
		(policy_name    => 'emp_ols_pol',
		long_name      => 'Ngoại thành',
		short_name     => 'nt',
		group_num      => 120);
		
	--Mọi y bác sĩ
	sa_label_admin.create_label
		(policy_name    => 'emp_ols_pol',
		label_tag      => 300,
		label_value    => 'YBS');
	--Y bác sĩ nội ngoại ở cận trung tâm, trung tâm
	sa_label_admin.create_label
		(policy_name    => 'emp_ols_pol',
		label_tag      => 305,
		label_value    => 'YBS:noi,ngoai:ctt,tt');
	--Y bác sĩ sâu, nội ở trung tâm
	sa_label_admin.create_label
		(policy_name    => 'emp_ols_pol',
		label_tag      => 310,
		label_value    => 'YBS:noi,sau:tt');
	--Mọi giám đốc csyt
	sa_label_admin.create_label
		(policy_name    => 'emp_ols_pol',
		label_tag      => 320,
		label_value    => 'GDCSYT');
	--Giám đốc csyt nội, ngoại ở trung tâm
	sa_label_admin.create_label
		(policy_name    => 'emp_ols_pol',
		label_tag      => 330,
		label_value    => 'GDCSYT:noi,ngoai:tt');
	--Mọi giám đốc sở
	sa_label_admin.create_label
		(policy_name    => 'emp_ols_pol',
		label_tag      => 340,
		label_value    => 'GDS');
	
	--Ứng dụng policy lên TABLE
	sa_policy_admin.apply_table_policy
		(policy_name    => 'emp_ols_pol',
		schema_name    => 'C##QLKCB',
		table_name     => 'THONGBAO',
		table_options  => 'WRITE_CONTROL,READ_CONTROL');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
END;    

/
INSERT INTO C##QLKCB.THONGBAO
VALUES ('A notification to YBS', SYSTIMESTAMP, 'Department 1', CHAR_TO_LABEL('emp_ols_pol', 'YBS'));

INSERT INTO C##QLKCB.THONGBAO
VALUES ('A notification to YBS:noi,ngoai:ctt,tt', SYSTIMESTAMP, 'Department 1', CHAR_TO_LABEL('emp_ols_pol', 'YBS:noi,ngoai:ctt,tt'));

INSERT INTO C##QLKCB.THONGBAO
VALUES ('A notification to YBS:noi,sau:tt', SYSTIMESTAMP, 'Department 1', CHAR_TO_LABEL('emp_ols_pol', 'YBS:noi,sau:tt'));

INSERT INTO C##QLKCB.THONGBAO
VALUES ('A notification GDCSYT:noi,ngoai:tt', SYSTIMESTAMP, 'Department 1', CHAR_TO_LABEL('emp_ols_pol', 'GDCSYT:noi,ngoai:tt'));

INSERT INTO C##QLKCB.THONGBAO
VALUES ('A notification to GDCSYT', SYSTIMESTAMP, 'Department 3', CHAR_TO_LABEL('emp_ols_pol', 'GDCSYT'));

INSERT INTO C##QLKCB.THONGBAO
VALUES ('A notification to GDS', SYSTIMESTAMP, 'Department 1', CHAR_TO_LABEL('emp_ols_pol', 'GDS'));
/
CREATE OR REPLACE PROCEDURE SYS.SET_USER_LABEL_COMPONENTS (
	user_name_in	VARCHAR2,
	level_in		VARCHAR2,
    compartment_in  VARCHAR2 DEFAULT NULL,
    group_in        VARCHAR2 DEFAULT NULL)
AS
BEGIN
    SA_USER_ADMIN.SET_LEVELS (
        policy_name     => 'emp_ols_pol',
        user_name       => user_name_in,
        max_level       => level_in);
        
    IF compartment_in IS NOT NULL
    THEN
        SA_USER_ADMIN.SET_COMPARTMENTS (
            policy_name     => 'emp_ols_pol',
            user_name       => user_name_in,
            read_comps      => compartment_in);
    END IF;
    
    IF group_in IS NOT NULL
    THEN
        SA_USER_ADMIN.SET_GROUPS (
            policy_name     => 'emp_ols_pol',
            user_name       => user_name_in,
            read_groups     => group_in);
    END IF;
END;
/
BEGIN
    SYS.SET_USER_LABEL_COMPONENTS('C##NHANVIEN', 'YBS');
END;
BEGIN
    SYS.SET_USER_LABEL_COMPONENTS('C##NV0008', 'YBS');
    SYS.SET_USER_LABEL_COMPONENTS('C##NV0009', 'YBS', 'noi,ngoai', 'ctt');
    SYS.SET_USER_LABEL_COMPONENTS('C##NV0010', 'YBS', 'noi,ngoai,sau', 'tt');
    SYS.SET_USER_LABEL_COMPONENTS('C##NV0004', 'GDCSYT', 'noi', 'tt');
    SYS.SET_USER_LABEL_COMPONENTS('C##NV0005', 'GDCSYT', 'noi,ngoai', 'nt');
    SYS.SET_USER_LABEL_COMPONENTS('C##NV0006', 'GDCSYT', 'noi,ngoai,sau', 'ctt,tt');
    SYS.SET_USER_LABEL_COMPONENTS('C##NV0001', 'GDS', NULL, 'nt');
    SYS.SET_USER_LABEL_COMPONENTS('C##NV0002', 'GDS', 'ngoai,noi,sau', 'tt');
END;
/
CREATE OR REPLACE PROCEDURE SYS.SET_USER_LABEL (
	user_name_in	VARCHAR2,
	label_in		VARCHAR2)
AS
BEGIN
	sa_user_admin.set_user_labels
		(policy_name    => 'emp_ols_pol',
		user_name       => user_name_in,
		max_read_label 	=> label_in);
END;
/
CREATE OR REPLACE PROCEDURE SYS.SET_READ_WRITE_USER_LABEL (
	user_name_in		VARCHAR2,
	max_read_label_in	VARCHAR2,
	min_write_label_in	VARCHAR2,
	max_write_label_in	VARCHAR2)
AS
BEGIN
	sa_user_admin.set_user_labels
		(policy_name    => 'emp_ols_pol',
		user_name       => user_name_in,
		max_read_label  => max_read_label_in,
		min_write_label => min_write_label_in,
		max_write_label => max_write_label_in);
END;
/
SELECT * FROM DBA_AUDIT_POLICIES;
SELECT * FROM DBA_OBJ_AUDIT_OPTS;
show parameter audit;

-- Kich hoat Audit tren toan he thong
ALTER SYSTEM SET audit_sys_operations=True SCOPE = SPFILE;
ALTER SYSTEM SET audit_trail=db,extended SCOPE=SPFILE; -- Bat audit_trail va record luu trong table SYS.AUD$
--Audit session
AUDIT SESSION;
-- Audit cac hanh dong thanh cong va khong thanh cong tren bang BENHNHAN
AUDIT INSERT, DELETE ON C##QLKCB.BENHNHAN BY ACCESS;
AUDIT INSERT, DELETE ON C##QLKCB.NHANVIEN BY ACCESS;
AUDIT INSERT, DELETE, UPDATE ON C##QLKCB.HSBA BY ACCESS;
AUDIT INSERT, DELETE, UPDATE ON C##QLKCB.HSBA_DV BY ACCESS;
AUDIT INSERT, UPDATE, DELETE ON c##apdsgvkyp3s5v8y.D7711589BB9785CAAFFF31C1143E9 BY ACCESS;
AUDIT SELECT ON c##apdsgvkyp3s5v8y.D7711589BB9785CAAFFF31C1143E9 BY ACCESS WHENEVER NOT SUCCESSFUL;
AUDIT SELECT ON all_source;

-- Audit bang nhan vien
BEGIN
    dbms_fga.add_policy(object_schema   => 'C##QLKCB',
                        object_name     => 'NHANVIEN',
                        policy_name     => 'audit_nhanvien_another_pol',
                        audit_condition => 'USERNAME != USER',
                        statement_types => 'SELECT,UPDATE');
                        
    dbms_fga.add_policy(object_schema   => 'C##QLKCB',
                        object_name     => 'NHANVIEN',
                        policy_name     => 'audit_nhanvien_pol',
                        audit_column    => 'CMND,VAITRO,USERNAME',
                        statement_types => 'UPDATE');
                        
    dbms_fga.add_policy(object_schema   => 'C##QLKCB',
                        object_name     => 'BENHNHAN',
                        policy_name     => 'audit_benhnhan_another_pol',
                        audit_condition => 'USERNAME != USER',
                        statement_types => 'SELECT,UPDATE');
                        
    dbms_fga.add_policy(object_schema   => 'C##QLKCB',
                        object_name     => 'BENHNHAN',
                        policy_name     => 'audit_benhnhan_pol',
                        audit_column    => 'CMND,USERNAME',
                        statement_types => 'UPDATE');
END;

-- Xem log audit record;
select * from DBA_COMMON_AUDIT_TRAIL; --both
select * from DBA_AUDIT_TRAIL; --standard
select * from DBA_FGA_AUDIT_TRAIL; --fga