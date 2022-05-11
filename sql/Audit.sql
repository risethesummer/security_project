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