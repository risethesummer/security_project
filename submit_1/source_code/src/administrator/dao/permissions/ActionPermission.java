package administrator.dao.permissions;

/**
 * administrator.dao.permissions
 * Created by NhatLinh - 19127652
 * Date 4/2/2022 - 12:16 PM
 * Description: ...
 */
public class ActionPermission  {
    private final boolean granted;
    private final boolean wgo;

    public ActionPermission(boolean granted, boolean wgo) {
        this.granted = granted;
        this.wgo = wgo;
    }

    public boolean isGranted() {
        return granted;
    }

    public boolean isWgo() {
        return wgo;
    }
}
