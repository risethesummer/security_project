package users.dao;

import java.util.Date;

/**
 * users.dao
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 1:28 PM
 * Description: ...
 */
public record Patient(String id, String facilityID, String name, String idCard, Date dob, String number,
                      String street, String district, String city, String anamnesis, String familyAnamnesis,
                      String allergyDrugs) implements DBRecord {
}