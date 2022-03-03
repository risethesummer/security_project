package administrator.dbHandler;

import administrator.dao.User;

import java.util.List;

/**
 * administrator.dbHandler
 * Created by NhatLinh - 19127652
 * Date 3/1/2022 - 10:07 PM
 * Description: ...
 */
public interface IUserDBHandler {

    List<User> getUsers();

}
