package administrator.dbHandler.table;

import administrator.dao.permissions.GeneralPermission;
import administrator.dao.table.property.References;
import administrator.dbHandler.IDBAHandler;

import java.util.List;

/**
 * administrator.dbHandler
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 11:39 PM
 * Description: ...
 */
public interface ITableHandler extends IDBAHandler {
    List<References> getTableReferences(String schemaIn, String dataType, int length);
    List<String> getSchemas();
    List<String> getAllSchemas();
    List<String> getTablesInSchema(String schema);
    GeneralPermission getPrivileges(String user, String schema, String table);
    boolean modifyPrivileges(String schema, String table, String user, GeneralPermission permission);
}
