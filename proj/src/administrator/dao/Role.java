package administrator.dao;

import java.util.Collection;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 5:39 PM
 * Description: ...
 */
public class Role extends DBObject {

    private final Collection<Permission> permissions;

    public Role(String name, DBObjectType type, Collection<DBObject> roles, Collection<Permission> permissions) {
        super(name, DBObjectType.ROLE);
        this.permissions = permissions;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }
}
