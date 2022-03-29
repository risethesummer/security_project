package administrator.dbHandler.table;

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
    List<References> getReferences(String dataType, int length);
}
