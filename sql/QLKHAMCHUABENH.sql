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
EXEC drop_user_if_exists('C##QLKCB');

CREATE USER C##QLKCB IDENTIFIED BY 123456;
ALTER USER C##QLKCB quota 20M ON USERS;

GRANT CREATE SESSION TO C##QLKCB;
GRANT CONNECT, RESOURCE TO C##QLKCB;
GRANT SELECT ANY DICTIONARY TO C##QLKCB;
GRANT CREATE TABLE TO C##QLKCB;
GRANT CREATE ANY PROCEDURE, ALTER ANY PROCEDURE, EXECUTE ANY PROCEDURE, DROP ANY PROCEDURE TO C##QLKCB;
GRANT GRANT ANY PRIVILEGE TO C##QLKCB;
GRANT CREATE ANY TRIGGER TO C##QLKCB;
GRANT CREATE ANY VIEW TO C##QLKCB;
GRANT GRANT ANY ROLE TO C##QLKCB;
GRANT GRANT ANY OBJECT PRIVILEGE TO C##QLKCB;

-- Connect vao user C##PH2 de tao bang thay vi tao bang co ten C##PH2.HSBA
CONNECT C##QLKCB/123456;

-- Tao bang KHOA
CREATE TABLE KHOA (
    MAKHOA VARCHAR(10) PRIMARY KEY,
    TENKHOA NVARCHAR2(100) NOT NULL UNIQUE
);

-- Tao bang DICHVU
CREATE TABLE DICHVU (
    MADV VARCHAR2(5) PRIMARY KEY,
    TENDV NVARCHAR2(100) NOT NULL UNIQUE
);

-- Tao bang CSYT
CREATE TABLE CSYT (
    MACSYT VARCHAR2(4) PRIMARY KEY,
    TENCSYT NVARCHAR2(100) NOT NULL,
    DCCSYT NVARCHAR2(200) NOT NULL,
    SDTCSYT VARCHAR2(15) NOT NULL
);

-- Tao bang BENHNHAN
CREATE TABLE BENHNHAN (
    MABN VARCHAR2(7) PRIMARY KEY,
    TENBN NVARCHAR2(100) NOT NULL,
    MACSYT VARCHAR2(4) NOT NULL,
    CMND VARCHAR2(15) UNIQUE,
    NGAYSINH DATE NOT NULL,
    SONHA VARCHAR2(20),
    TENDUONG NVARCHAR2(100),
    QUANHUYEN NVARCHAR2(100) NOT NULL,
    TINHTP NVARCHAR2(100) NOT NULL,
    TIENSUBENH NVARCHAR2(200),
    TIENSUBENHGD NVARCHAR2(200),
    DIUNGTHUOC NVARCHAR2(200),
    USERNAME VARCHAR2(128) NOT NULL,

    CONSTRAINT FK_BENHNHAN_MACSYT_CSYT FOREIGN KEY (MACSYT) REFERENCES CSYT(MACSYT)
);

-- Tao bang NHANVIEN
CREATE TABLE NHANVIEN (
    MANV VARCHAR2(6) PRIMARY KEY,
    HOTEN NVARCHAR2(100) NOT NULL,
    PHAI NVARCHAR2(5) NOT NULL,
    CMND VARCHAR2(15) NOT NULL UNIQUE,
    NGAYSINH DATE NOT NULL,
    QUEQUAN NVARCHAR2(200) NOT NULL,
    SODT VARCHAR2(15) NOT NULL,
    CSYT VARCHAR2(4) NOT NULL,
    VAITRO NVARCHAR2(20) NOT NULL,
    CHUYENKHOA NVARCHAR2(100),
    USERNAME VARCHAR2(128) NOT NULL,

    CONSTRAINT FK_NHANVIEN_CSYT_CSYT FOREIGN KEY (CSYT) REFERENCES CSYT(MACSYT)
);

-- Tao bang HSBA
CREATE TABLE HSBA (
    MAHSBA VARCHAR2(8) PRIMARY KEY,
    MABN VARCHAR2(7) NOT NULL,
    NGAY DATE NOT NULL,
    CHANDOAN NVARCHAR2(200),
    MABS VARCHAR2(6) NOT NULL,
    MAKHOA VARCHAR2(10) NOT NULL,
    MACSYT VARCHAR2(4) NOT NULL,
    KETLUAN NVARCHAR2(200),
    
    CONSTRAINT FK_HSBA_MABN_BENHNHAN FOREIGN KEY (MABN) REFERENCES BENHNHAN(MABN),
    CONSTRAINT FK_HSBA_MABS_NHANVIEN FOREIGN KEY (MABS) REFERENCES NHANVIEN(MANV),
    CONSTRAINT FK_HSBA_MACSYT_CSYT FOREIGN KEY (MACSYT) REFERENCES CSYT(MACSYT),
    CONSTRAINT FK_HSBA_MAKHOA_KHOA FOREIGN KEY (MAKHOA) REFERENCES KHOA(MAKHOA)
);

-- Tao bang HSBA_DV 
CREATE TABLE HSBA_DV (
    MAHSBA VARCHAR2(8),
    MADV VARCHAR2(5),
    NGAY DATE,
    MAKTV VARCHAR2(6) NOT NULL,
    KETQUA NVARCHAR2(200),
    
    CONSTRAINT PK_HSBADV PRIMARY KEY (MAHSBA, MADV, NGAY),
    CONSTRAINT FK_HSBADV_MAHSBA_HSBA FOREIGN KEY (MAHSBA) REFERENCES HSBA(MAHSBA),
    CONSTRAINT FK_HSBADV_MADV_DICHVU FOREIGN KEY (MADV) REFERENCES DICHVU(MADV),
    CONSTRAINT FK_HSBADV_MAKTV_NHANVIEN FOREIGN KEY (MAKTV) REFERENCES NHANVIEN(MANV)
);


COMMIT;

