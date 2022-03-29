package administrator.dao.user;

import administrator.dao.DBObject;
import administrator.dao.DBObjectType;
import java.util.ArrayList;
import java.util.List;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 5:19 PM
 * Description: ...
 */
public class User extends DBObject {
    public List<String> getRoles() {
        return roles;
    }

    private final List<String> roles;

    public User(String name, List<String> roles) {
        super(name, DBObjectType.USER);
        this.roles = roles;
    }

    public User(String name) {
        super(name, DBObjectType.USER);
        roles = null;
    }

    @Override
    public String getShown() {
        if (roles == null || roles.isEmpty())
            return "No role";
        return roles.get(0);
    }
}
