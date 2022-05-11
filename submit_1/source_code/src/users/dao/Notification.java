package users.dao;

import java.time.LocalDateTime;

/**
 * users.dao
 * Created by NhatLinh - 19127652
 * Date 5/8/2022 - 11:22 AM
 * Description: ...
 */
public record Notification (String content, LocalDateTime dateTime, String location, String label) implements DBRecord {
}
