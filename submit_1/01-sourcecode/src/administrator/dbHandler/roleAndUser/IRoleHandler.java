package administrator.dbHandler.roleAndUser;

import administrator.dbHandler.IDBAHandler;

import java.util.Collection;

/**
 * administrator.dbHandler.roleAndUser
 * Created by NhatLinh - 19127652
 * Date 3/30/2022 - 11:36 PM
 * Description: ...
 */
public interface IRoleHandler extends IDBAHandler {
    Collection<String> getInsideRole(String name);
    Collection<String> getOutsideRole(String name);
    boolean grant(String role, String user);
    boolean revoke(String role, String user);
}
