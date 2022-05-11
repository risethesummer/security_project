package users.dao;

import java.util.Date;

/**
 * users.dao
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 4:42 PM
 * Description: ...
 */
public record Staff (String id, String name, String sex, String idCard, Date dob,
                     String homeTown, String phone, String facilityID,
                     String role, String major) implements DBRecord{

}
