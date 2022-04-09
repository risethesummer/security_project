package administrator.dao.role;

import administrator.dao.DBObject;
import administrator.dao.DBObjectType;
import administrator.dao.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 2/22/2022 - 5:39 PM
 * Description: ...
 */
public class Role extends DBObject {
    public List<String> getUsers() {
        return users;
    }

    private final List<String> users;

    public Role(String name, List<String> users) {
        super(name, DBObjectType.ROLE);
        this.users = users;
    }

    public Role(String name) {
        super(name, DBObjectType.ROLE);
        users = null;
    }

    public boolean isCommonRole()
    {
        return isCommonObj(name);
    }

    @Override
    public String getShown() {
        if (users == null || users.isEmpty())
            return "No user";
        return users.get(0);
    }
}
