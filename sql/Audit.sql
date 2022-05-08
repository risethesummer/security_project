-- Kiem tra bat Unified Audit chua
SELECT value FROM v$option
WHERE parameter = 'Unified Auditing';

SELECT * FROM DBA_AUDIT_POLICIES;
show parameter audit;

-- Kich hoat Audit tren toan he thong
ALTER SYSTEM SET audit_trail = db SCOPE = SPFILE;  -- Bat audit_trail va record luu trong table SYS.AUD$

-- Xem log audit record;
select * from SYS.AUD$;

-- Audit system privilege, actions, role
CREATE AUDIT POLICY audit_syspriv_actions_role_pol
PRIVILEGES DROP ANY TABLE, CREATE ANY TABLE
ACTIONS ALL
ROLES C##NHANVIEN, C##THANHTRA, C##COSOYTE, C##NGHIENCUU;
-- Bat audit
AUDIT POLICY audit_syspriv_actions_role_pol;
-- Tat audit
NOAUDIT POLICY audit_syspriv_actions_role_pol;


-- Audit tren cot CHANDOAN va KETLUAN cua bang HSBA
BEGIN
dbms_fga.add_policy(object_schema=>'C##QLKCB',
                    object_name=>'HSBA',
                    policy_name=>'audit_hsba_pol',
                    audit_column => 'CHANDOAN, KETLUAN',
                    statement_types => 'SELECT, INSERT, UPDATE, DELETE');
END;

---- DROP
--BEGIN
--dbms_fga.drop_policy (
--    object_schema => 'C##QLKCB',
--    object_name => 'BENHNHAN',
--    policy_name => 'audit_benhnhan_pol');
--END;

-- Audit select, update tren cot CMND cua bang BENHNHAN
BEGIN
dbms_fga.add_policy(object_schema=>'C##QLKCB',
                    object_name=>'BENHNHAN',
                    policy_name=>'audit_cmnd_benhnhan_pol',
                    audit_column => 'CMND',
                    statement_types => 'SELECT, UPDATE');
END;

-- Audit select, update tren cot CMND cua bang NHANVIEN
BEGIN
dbms_fga.add_policy(object_schema=>'C##QLKCB',
                    object_name=>'NHANVIEN',
                    policy_name=>'audit_cmnd_benhnhan_pol',
                    audit_column => 'CMND',
                    statement_types => 'SELECT, UPDATE');
END;

-- Audit cac hanh dong thanh cong va khong thanh cong tren bang BENHNHAN
AUDIT SELECT, INSERT, DELETE, UPDATE ON C##QLKCB.BENHNHAN
BY ACCESS
WHENEVER SUCCESSFUL;

AUDIT SELECT, INSERT, DELETE, UPDATE ON C##QLKCB.BENHNHAN
BY ACCESS
WHENEVER NOT SUCCESSFUL;

select CMND from C##QLKCB.BENHNHAN;
SELECT * FROM audit_unified_policies;
SELECT * FROM audit_unified_enabled_policies;


select * from sys.fga_log$
---- Xoa cac ban ghi log audit:
--DBMS_AUDIT_MGMT.CLEAN_AUDIT_TRAIL (
--    AUDIT_TRAIL_TYPE => DBMS_AUDIT_MGMT.AUDIT_TRAIL_UNIFIED);