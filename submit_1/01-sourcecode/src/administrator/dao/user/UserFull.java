package administrator.dao.user;

import java.util.List;

/**
 * administrator.dao
 * Created by NhatLinh - 19127652
 * Date 3/26/2022 - 1:58 PM
 * Description: ...
 */
public class UserFull extends User {
    private final String password;

    public UserFull(String name, String password, List<String> roles)
    {
        super(name, roles);
        this.password = password;
    }

    public boolean isCommonUser()
    {
        return isCommonObj(name);
    }

    public String getPassword() {
        return password;
    }
}
