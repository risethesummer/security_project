package users.dao;

import java.util.Date;

/**
 * users.dao
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 5:53 PM
 * Description: ...
 */

public record Document(String id, String patientID, Date date, String diagnose, String dortorID,
                       String departmentID, String facilityID, String conclusion,
                       Iterable<DocumentService> services) implements DBRecord {
}
