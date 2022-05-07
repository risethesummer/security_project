
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

ALTER SESSION SET container = XEPDB1;
CREATE TABLE C##QLKCB.THONGBAO (NOIDUNG VARCHAR2(500) NOT NULL, NGAYGIO DATE DEFAULT SYSDATE, DIADIEM VARCHAR2(200));
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
CREATE OR REPLACE PROCEDURE C##QLKCB.CHINHSUA_NHANVIEN_CMND (
    MA_IN VARCHAR2, CMND VARCHAR2
)
IS
    dec_key RAW(48) := NULL;
    raw_cmnd RAW(16) := NULL;
    pad char(4) := 'KEYP';
    alg_grade       pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
BEGIN
    dec_key := c##apdsgvkyp3s5v8y.LAY_KEY(MA_IN, pad);
    IF dec_key IS NOT NULL
    THEN
        raw_cmnd := DBMS_CRYPTO.ENCRYPT(src => UTL_RAW.CAST_TO_RAW(CMND),
                                    TYP => alg_grade,
                                    key => dec_key);
        UPDATE C##QLKCB.NHANVIEN SET CMND = raw_cmnd WHERE MANV = MA_IN;
    END IF;
END;
/
CREATE OR REPLACE PROCEDURE C##QLKCB.CHINHSUA_BENHNHAN_CMND (
    MA_IN VARCHAR2, CMND VARCHAR2
)
IS
    dec_key RAW(48) := NULL;
    raw_cmnd RAW(16) := NULL;
    pad char(2) := 'KE';
    alg_grade       pls_integer := DBMS_CRYPTO.ENCRYPT_AES256
                                   + DBMS_CRYPTO.CHAIN_CBC
                                   + DBMS_CRYPTO.PAD_PKCS5;
BEGIN
    dec_key := c##apdsgvkyp3s5v8y.LAY_KEY(MA_IN, pad);
    IF dec_key IS NOT NULL
    THEN
        
        raw_cmnd := DBMS_CRYPTO.ENCRYPT(src => UTL_RAW.CAST_TO_RAW(CMND),
                                    TYP => alg_grade,
                                    key => dec_key);
        UPDATE C##QLKCB.BENHNHAN SET CMND = raw_cmnd WHERE MABN = MA_IN;
    END IF;
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
SELECT * FROM C##QLKCB.HSBA;
/
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000001', 'DV001', sysdate, 'NV0008', N'Bình thường');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000002', 'DV003', sysdate, 'NV0009', N'Âm tính');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000002', 'DV002', sysdate, 'NV0009', N'Thành mũi biến dạng');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000003', 'DV002', sysdate, 'NV0012', N'Phát hiện tế bào ung thư');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000004', 'DV005', sysdate, 'NV0010', N'Tiến triển tốt');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000005', 'DV001', sysdate, 'NV0014', N'Đường huyết cao');
INSERT INTO C##QLKCB.HSBA_DV VALUES ('HS000005', 'DV004', sysdate, 'NV0015', N'Bình thường');
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

COMMIT;

-------------------------------------------------------------
-- TC#1
--View cho bệnh nhân
CREATE OR REPLACE VIEW C##QLKCB.BENH_NHAN_XEM_BENH_NHAN AS
SELECT bn.*
FROM C##QLKCB.BENHNHAN bn
WHERE bn.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER');
/
CREATE ROLE C##BENHNHAN;
/
GRANT CREATE SESSION TO C##BENHNHAN;
/
GRANT SELECT ON C##QLKCB.BENH_NHAN_XEM_BENH_NHAN TO C##BENHNHAN;
/
GRANT EXECUTE ON C##QLKCB.LAY_CMND_BENHNHAN TO C##BENHNHAN;
/
GRANT EXECUTE ON C##QLKCB.CHINHSUA_BENHNHAN_CMND TO C##BENHNHAN;
/
GRANT UPDATE(
    TENBN,
    CMND,
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
SELECT nv.*
FROM NHANVIEN nv
WHERE nv.USERNAME = SYS_CONTEXT('USERENV', 'SESSION_USER');
/
CREATE ROLE C##NHANVIEN;
/
GRANT SELECT ON C##QLKCB.NHAN_VIEN_XEM_NHAN_VIEN TO C##NHANVIEN;
/
GRANT CREATE SESSION TO C##NHANVIEN;
/
GRANT UPDATE(
    HOTEN,
    PHAI,
    CMND,
    NGAYSINH,
    QUEQUAN,
    SODT
) ON C##QLKCB.NHAN_VIEN_XEM_NHAN_VIEN TO C##NHANVIEN;
/
GRANT EXECUTE ON C##QLKCB.CHINHSUA_NHANVIEN_CMND TO C##NHANVIEN;
/
GRANT EXECUTE ON C##QLKCB.LAY_CMND_NHANVIEN TO C##NHANVIEN;
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
			EXECUTE IMMEDIATE ('GRANT C##NHANVIEN TO ' || u.USERNAME);
            --EXECUTE IMMEDIATE ('GRANT CREATE SESSION TO '||u.USERNAME);
        END IF;
    END LOOP;
END;
/
EXEC SYS.create_user_for_table_nhan_vien;
/
-------------------------------------------------------------
-- TC#2;
--Cấp quyền SELECT trên tất cả các bảng cho thanh tra
CREATE OR REPLACE PROCEDURE SYS.grant_select(
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
------------
--Thanh tra
CREATE ROLE C##THANHTRA;
/
EXEC SYS.grant_select('C##QLKCB', 'C##THANHTRA');
/
EXEC SYS.grant_role_to_user_from_nhan_vien(N'Thanh tra', 'C##THANHTRA');
/
GRANT EXECUTE ON C##QLKCB.LAY_CMND_BENHNHAN TO C##THANHTRA;
/
-------------
--Cơ sở y tế
CREATE ROLE C##COSOYTE;
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
SELECT bn.*
FROM BENHNHAN bn
	JOIN HSBA hsba ON hsba.MABN = bn.MABN
	JOIN NHANVIEN nv ON hsba.MABS = nv.MANV
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
GRANT EXECUTE ON C##QLKCB.LAY_CMND_BENHNHAN TO C##BACSI;
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
		RETURN 'TRUE';
	END IF;

    SELECT MANV, VAITRO, MACSYT, CHUYENKHOA
    INTO ma_nv, vai_tro, ma_csyt, ma_khoa
    FROM C##QLKCB.NHANVIEN nv
    WHERE nv.USERNAME = currentUser;
    
    IF ma_nv IS NULL
    THEN
        RETURN 'FALSE';
    END IF;
    
	IF vai_tro = N'Thanh tra'
	THEN 
		RETURN 'TRUE';
	END IF;
    --“Thanh tra”, “Cơ sở y tế”, “Y sĩ/bác sĩ”, “Nghiên cứu”.
    IF vai_tro = N'Cơ sở y tế'
    THEN
		RETURN ('MACSYT = ' || ma_csyt || ' AND TO_NUMBER(TO_CHAR(NGAY, ''DD'')) > 4 AND TO_NUMBER(TO_CHAR(NGAY, ''DD'')) < 28');
    END IF;
    
    IF vai_tro = N'Y/Bác sĩ'
    THEN
        RETURN ('MABS = ' || ma_nv);
    END IF;
    
    IF vai_tro = N'Nghiên cứu'
    THEN
        RETURN ('MACSYT = ' || ma_csyt || ' AND MAKHOA = ' || ma_khoa);
    END IF;
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
		RETURN 'TRUE';
	END IF;
	
    SELECT MANV, VAITRO, MACSYT, CHUYENKHOA
    INTO ma_nv, vai_tro, ma_csyt, ma_khoa
    FROM C##QLKCB.NHANVIEN nv
    WHERE nv.USERNAME = currentUser;
    
    IF ma_nv IS NULL
    THEN
        RETURN 'FALSE';
    END IF;
    
	IF vai_tro = N'Thanh tra'
	THEN 
		RETURN 'TRUE';
	END IF;
	
    --“Thanh tra”, “Cơ sở y tế”, “Y sĩ/bác sĩ”, “Nghiên cứu”.
    IF vai_tro = N'Cơ sở y tế'
    THEN
		RETURN ('IF EXISTS (SELECT hsba.MAHSBA FROM HSBA hsba WHERE hsba.MAHSBA = HSBA_DV.MAHSBA AND hsba.MACSYT = ' || ma_csyt ||
				' AND TO_NUMBER(TO_CHAR(HSBA_DV.NGAY, ''DD'')) > 4 AND TO_NUMBER(TO_CHAR(HSBA_DV.NGAY, ''DD'')) < 28)');
        
    END IF;
    
    IF vai_tro = N'Y/Bác sĩ'
    THEN
        RETURN ('IF EXISTS (SELECT hsba.MAHSBA FROM HSBA hsba WHERE hsba.MAHSBA = HSBA_DV.MAHSBA AND hsba.MABS = ' || ma_nv || ')');
    END IF;
    
    IF vai_tro = N'Nghiên cứu'
    THEN
		RETURN ('IF EXISTS (SELECT hsba.MAHSBA FROM HSBA hsba WHERE hsba.MAHSBA = HSBA_DV.MAHSBA AND hsba.MACSYT = ' || ma_csyt ||
		' AND hsba.MAKHOA = ' || ma_khoa || ')');
    END IF;
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
END;
SELECT * FROM dba_policies;


--OLS
EXEC LBACSYS.CONFIGURE_OLS;
EXEC LBACSYS.OLS_ENFORCEMENT.ENABLE_OLS;
GRANT INHERIT PRIVILEGES ON USER SYS TO LBACSYS;
ALTER USER LBACSYS ACCOUNT UNLOCK IDENTIFIED BY password;
ALTER SESSION SET container = XEPDB1;

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
		
	--Tạo level cho nhân viên
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
		label_value    => 'GDSYT');
	--Giám đốc csyt nội, ngoại ở trung tâm
	sa_label_admin.create_label
		(policy_name    => 'emp_ols_pol',
		label_tag      => 330,
		label_value    => 'GDSYT:noi,ngoai:tt');
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