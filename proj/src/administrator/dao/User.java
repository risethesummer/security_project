package administrator.dao;

import java.util.Collection;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 5:19 PM
 * Description: ...
 */
public class User extends DBObject {
    private final Collection<DBObject> roles;
    private final Collection<Permission> permissions;

    public User(String name, DBObjectType type, Collection<DBObject> roles, Collection<Permission> permissions) {
        super(name, DBObjectType.USER);
        this.permissions = permissions;
        this.roles = roles;
    }

    public Collection<DBObject> getRoles() {
        return roles;
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }
}
