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
ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE;


EXECUTE drop_user_if_exists('c##apdsgvkyp3s5v8y');
/
CREATE USER c##apdsgvkyp3s5v8y IDENTIFIED BY hnl;
/
ALTER USER c##apdsgvkyp3s5v8y QUOTA UNLIMITED ON USERS;
/
GRANT CREATE SESSION TO c##apdsgvkyp3s5v8y;
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


EXEC drop_user_if_exists('QLKCB');

CREATE USER QLKCB IDENTIFIED BY 123;
ALTER USER QLKCB quota 20M ON USERS;

GRANT ALL PRIVILEGES TO QLKCB;
GRANT SELECT ANY DICTIONARY TO QLKCB;
GRANT EXECUTE ON DBMS_CRYPTO to QLKCB;
GRANT EXECUTE ON c##apdsgvkyp3s5v8y.LAY_KEY TO QLKCB;
GRANT INSERT ON c##apdsgvkyp3s5v8y.D7711589BB9785CAAFFF31C1143E9 TO QLKCB;

CONNECT QLKCB/123;

-- Tao bang KHOA
CREATE TABLE QLKCB.KHOA (
    MAKHOA VARCHAR(4) PRIMARY KEY,
    TENKHOA NVARCHAR2(100) NOT NULL UNIQUE
);

-- Tao bang DICHVU
CREATE TABLE QLKCB.DICHVU (
    MADV VARCHAR2(5) PRIMARY KEY,
    TENDV NVARCHAR2(100) NOT NULL UNIQUE
);

-- Tao bang CSYT
CREATE TABLE QLKCB.CSYT (
    MACSYT VARCHAR2(4) PRIMARY KEY,
    TENCSYT NVARCHAR2(100) NOT NULL,
    DCCSYT NVARCHAR2(200) NOT NULL,
    SDTCSYT VARCHAR2(15) NOT NULL
    
);

-- Tao bang BENHNHAN
CREATE TABLE QLKCB.BENHNHAN (
    MABN VARCHAR2(7) PRIMARY KEY,
    TENBN NVARCHAR2(100) NOT NULL,
    MACSYT VARCHAR2(4) NOT NULL,
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

    CONSTRAINT FK_BENHNHAN_MACSYT_CSYT FOREIGN KEY (MACSYT) REFERENCES QLKCB.CSYT(MACSYT)
);

-- Tao bang NHANVIEN
CREATE TABLE QLKCB.NHANVIEN (
    MANV VARCHAR2(6) PRIMARY KEY,
    HOTEN NVARCHAR2(100) NOT NULL,
    PHAI NVARCHAR2(5) NOT NULL,
    CMND RAW(16) NOT NULL,
    NGAYSINH DATE NOT NULL,
    QUEQUAN NVARCHAR2(200) NOT NULL,
    SODT VARCHAR2(15) NOT NULL,
    MACSYT VARCHAR2(4) NOT NULL,
    VAITRO NVARCHAR2(20) NOT NULL,
    CHUYENKHOA VARCHAR2(4),
    USERNAME VARCHAR2(128) NOT NULL UNIQUE,

    CONSTRAINT FK_NHANVIEN_CSYT_CSYT FOREIGN KEY (MACSYT) REFERENCES QLKCB.CSYT(MACSYT),
    CONSTRAINT FK_NHANVIEN_CHUYENKHOA_KHOA FOREIGN KEY (CHUYENKHOA) REFERENCES QLKCB.KHOA(MAKHOA)
);

-- Tao bang HSBA
CREATE TABLE QLKCB.HSBA (
    MAHSBA VARCHAR2(8) PRIMARY KEY,
    MABN VARCHAR2(7) NOT NULL,
    NGAY DATE DEFAULT sysdate,
    CHANDOAN NVARCHAR2(200),
    MABS VARCHAR2(6) NOT NULL,
    MAKHOA VARCHAR2(4) NOT NULL,
    MACSYT VARCHAR2(4) NOT NULL,
    KETLUAN NVARCHAR2(200),
    
    CONSTRAINT FK_HSBA_MABN_BENHNHAN FOREIGN KEY (MABN) REFERENCES QLKCB.BENHNHAN(MABN),
    CONSTRAINT FK_HSBA_MABS_NHANVIEN FOREIGN KEY (MABS) REFERENCES QLKCB.NHANVIEN(MANV),
    CONSTRAINT FK_HSBA_MACSYT_CSYT FOREIGN KEY (MACSYT) REFERENCES QLKCB.CSYT(MACSYT),
    CONSTRAINT FK_HSBA_MAKHOA_KHOA FOREIGN KEY (MAKHOA) REFERENCES QLKCB.KHOA(MAKHOA)
);

-- Tao bang HSBA_DV 
CREATE TABLE QLKCB.HSBA_DV (
    MAHSBA VARCHAR2(8),
    MADV VARCHAR2(5),
    NGAY DATE default sysdate,
    MAKTV VARCHAR2(6) NOT NULL,
    KETQUA NVARCHAR2(200),
    
    CONSTRAINT PK_HSBADV PRIMARY KEY (MAHSBA, MADV, NGAY),
    CONSTRAINT FK_HSBADV_MAHSBA_HSBA FOREIGN KEY (MAHSBA) REFERENCES QLKCB.HSBA(MAHSBA),
    CONSTRAINT FK_HSBADV_MADV_DICHVU FOREIGN KEY (MADV) REFERENCES QLKCB.DICHVU(MADV),
    CONSTRAINT FK_HSBADV_MAKTV_NHANVIEN FOREIGN KEY (MAKTV) REFERENCES QLKCB.NHANVIEN(MANV)
);
/
CREATE OR REPLACE PROCEDURE QLKCB.THEM_NHANVIEN (
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
        stored_enc_ma := DBMS_CRYPTO.ENCRYPT(src => UTL_I18N.STRING_TO_RAW(MANV_IN, 'AL32UTF8'),
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
    INSERT INTO QLKCB.NHANVIEN
    VALUES (
        MANV_IN, HOTEN_IN, PHAI_IN, inserted_cmnd, NGAYSINH_IN, QUEQUAN_IN, SODT_IN, MACSYT_IN, VAITRO_IN, CHUYENKHOA_IN, USERNAME_IN
    );
END;
/


CREATE OR REPLACE PROCEDURE QLKCB.THEM_BENHNHAN (
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
    INSERT INTO QLKCB.BENHNHAN
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


INSERT INTO QLKCB.CSYT VALUES ('CS01', N'C? s? 1', 'Quan 1, TPHCM', '0901010101');
INSERT INTO QLKCB.CSYT VALUES ('CS02', N'C? s? 2', 'Quan 2, TPHCM', '0902020202');
INSERT INTO QLKCB.CSYT VALUES ('CS03', N'C? s? 3', 'Quan 3, TPHCM', '0903030303');
INSERT INTO QLKCB.CSYT VALUES ('CS04', N'C? s? 4', 'Quan 4, TPHCM', '0904040404');
INSERT INTO QLKCB.CSYT VALUES ('CS05', N'C? s? 5', 'Quan 5, TPHCM', '0905050505');
INSERT INTO QLKCB.CSYT VALUES ('CS06', N'C? s? 6', 'Quan 6, TPHCM', '0906060606');
INSERT INTO QLKCB.CSYT VALUES ('CS07', N'C? s? 7', 'Quan 7, TPHCM', '0907070707');
INSERT INTO QLKCB.CSYT VALUES ('CS08', N'C? s? 8', 'Quan 8, TPHCM', '0908080808');
INSERT INTO QLKCB.CSYT VALUES ('CS09', N'C? s? 9', 'Quan 9, TPHCM', '0909090909');

INSERT INTO QLKCB.KHOA VALUES ('K01', N'Khoa Th?n kinh');
INSERT INTO QLKCB.KHOA VALUES ('K02', N'Khoa Ch?nh h�nh');
INSERT INTO QLKCB.KHOA VALUES ('K03', N'Khoa Ung b??u');
INSERT INTO QLKCB.KHOA VALUES ('K04', N'Khoa Tr? em');
INSERT INTO QLKCB.KHOA VALUES ('K05', N'Khoa Tai, m?i h?ng');
INSERT INTO QLKCB.KHOA VALUES ('K06', N'Khoa M?t');
INSERT INTO QLKCB.KHOA VALUES ('K07', N'Khoa Ngo?i t?ng h?p');
INSERT INTO QLKCB.KHOA VALUES ('K08', N'Khoa Tim m?ch');

INSERT INTO QLKCB.DICHVU VALUES ('DV001', N'X�t nghi?m m�u');
INSERT INTO QLKCB.DICHVU VALUES ('DV002', N'Ch?p h�nh X quang');
INSERT INTO QLKCB.DICHVU VALUES ('DV003', N'Test Covid');
INSERT INTO QLKCB.DICHVU VALUES ('DV004', N'Si�u �m');
INSERT INTO QLKCB.DICHVU VALUES ('DV005', N'?o ?i?n n�o');
BEGIN
    QLKCB.THEM_NHANVIEN('NV0001', N'Nguy?n V?n A', 'Nam', '123455678', To_DATE('1990/02/02', 'yyyy/mm/dd'), 'TPHCM', '123456789', 'CS01', 'Thanh tra', '', 'NV0001');
    QLKCB.THEM_NHANVIEN('NV0002', N'H? H�m H??ng', N'N?', '414125353', To_DATE('1990/07/05', 'yyyy/mm/dd'), 'TPHCM', '123456789', 'CS02', 'Thanh tra', '', 'NV0002');
    QLKCB.THEM_NHANVIEN('NV0003', N'Tr?n V?n Tr�n', 'Nam', '746463452', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '123456789', 'CS03', 'Thanh tra', '', 'NV0003');
    QLKCB.THEM_NHANVIEN('NV0004', N'H� Th? Ngon', N'N?', '746515673', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '098765434', 'CS01', N'C? s? y t?', '', 'NV0004');
    QLKCB.THEM_NHANVIEN('NV0005', N'T??i V?n T?n', 'Nam', '631435566', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '098765233', 'CS02', N'C? s? y t?', '', 'NV0005');
    QLKCB.THEM_NHANVIEN('NV0006', N'H? Hi?n H?u L?m', N'N?', '255623536', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '098765233', 'CS03', N'C? s? y t?', '', 'NV0006');
    QLKCB.THEM_NHANVIEN('NV0007', N'Tr?n V?n C??ng', N'N?', '512532523', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '098765233', 'CS04', N'C? s? y t?', '', 'NV0007'); 
    QLKCB.THEM_NHANVIEN('NV0008', N'Nguy?n V?n B?n', N'Nam', '635626256', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS01', N'B�c s?', 'K01', 'NV0008');
    QLKCB.THEM_NHANVIEN('NV0009', N'Nguy?n V?n Ch?c', N'Nam', '734561244', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987673465', 'CS01', N'B�c s?', 'K02', 'NV0009');
    QLKCB.THEM_NHANVIEN('NV0010', N'Nguy?n Th? Mo', N'N?', '13144256', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS02', N'B�c s?', 'K01', 'NV0010');
    QLKCB.THEM_NHANVIEN('NV0011', N'Nguy?n L�i', N'Nam', '426241156', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS02', N'B�c s?', 'K02', 'NV0011');
    QLKCB.THEM_NHANVIEN('NV0012', N'Nguy?n V?n To', N'Nam', '637567212', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS02', N'B�c s?', 'K03', 'NV0012');
    QLKCB.THEM_NHANVIEN('NV0013', N'Nguy?n Nh?', N'Nam', '62512345', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS01', N'Nghi�n c?u', 'K01', 'NV0013');
    QLKCB.THEM_NHANVIEN('NV0014', N'H? Th? Y', N'N?', '7473623123', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS01', N'Nghi�n c?u', 'K02', 'NV0014');
    QLKCB.THEM_NHANVIEN('NV0015', N'Nguy?n V?n L�', N'Nam', '63568586', To_DATE('1990/03/02', 'yyyy/mm/dd'), 'TPHCM', '0987231343', 'CS02', N'Nghi�n c?u', 'K03', 'NV0015');
END;
/
BEGIN
    QLKCB.THEM_BENHNHAN('BN00001', N'L�m Ho�ng Ph�c', 'CS01', '12132244', To_DATE('2001/03/02', 'yyyy/mm/dd'), '', '', 'Quan 1', 'HCM', '', '', '', 'BN00001');
    QLKCB.THEM_BENHNHAN('BN00002', 'H? Nh?t Linh', 'CS01', '12132554', To_DATE('2001/05/02', 'yyyy/mm/dd'), '', '', 'Quan 3', 'HCM', '', '', '', 'BN00002');
    QLKCB.THEM_BENHNHAN('BN00003', 'Nguy?n ?au B?nh', 'CS02', '113224534', To_DATE('2001/10/02', 'yyyy/mm/dd'), '', '', 'Quan 1', 'HCM', '', '', '', 'BN00003');
    QLKCB.THEM_BENHNHAN('BN00004', 'Tr?n ?m Y?u', 'CS02', '121322644', To_DATE('2001/03/02', 'yyyy/mm/dd'), '', '', 'Quan 1', 'HCM', '', '', '', 'BN00004');
END;
/

INSERT INTO QLKCB.HSBA VALUES ('HS000001', 'BN00001', sysdate, '', 'NV0008', 'K01', 'CS01', '');
INSERT INTO QLKCB.HSBA VALUES ('HS000002', 'BN00002', sysdate, '', 'NV0009', 'K02', 'CS01', '');
INSERT INTO QLKCB.HSBA VALUES ('HS000003', 'BN00003', sysdate, '', 'NV0009', 'K02', 'CS01', '');
INSERT INTO QLKCB.HSBA VALUES ('HS000004', 'BN00004', sysdate, '', 'NV0013', 'K01', 'CS01', '');
INSERT INTO QLKCB.HSBA VALUES ('HS000005', 'BN00002', sysdate, '', 'NV0014', 'K02', 'CS01', '');

INSERT INTO QLKCB.HSBA_DV VALUES ('HS000001', 'DV001', sysdate, 'NV0014', '');
INSERT INTO QLKCB.HSBA_DV VALUES ('HS000002', 'DV002', sysdate, 'NV0015', '');


/
CREATE OR REPLACE FUNCTION QLKCB.LAY_CMND_NHANVIEN (
    MA_IN VARCHAR2, RAW_CMND RAW
)
RETURN VARCHAR2
IS
    dec_key RAW(48) := NULL;
    pad char(4) := 'KEYP';
    alg_grade       pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
BEGIN
    dec_key := c##apdsgvkyp3s5v8y.LAY_KEY(MA_IN, pad);
    IF dec_key IS NULL
    THEN
        RETURN NULL;
    ELSE
        RETURN UTL_RAW.CAST_TO_VARCHAR2(DBMS_CRYPTO.DECRYPT(src => RAW_CMND,
                                    TYP => alg_grade,
                                    key => dec_key));
    END IF;
END;
/
CREATE OR REPLACE FUNCTION QLKCB.LAY_CMND_BENHNHAN (
    MA_IN VARCHAR2, RAW_CMND RAW
)
RETURN VARCHAR2
IS
    dec_key RAW(48) := NULL;
    pad char(2) := 'KE';
    alg_grade       pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
BEGIN
    dec_key := c##apdsgvkyp3s5v8y.LAY_KEY(MA_IN, pad);
    IF dec_key IS NULL
    THEN
        RETURN NULL;
    ELSE
        RETURN UTL_RAW.CAST_TO_VARCHAR2(DBMS_CRYPTO.DECRYPT(src => RAW_CMND,
                                    TYP => alg_grade,
                                    key => dec_key));
    END IF;
END;
/

COMMIT;
