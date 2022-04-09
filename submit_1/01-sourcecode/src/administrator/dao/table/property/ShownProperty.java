package administrator.dao.table.property;

/**
 * administrator.dao.table.property
 * Created by NhatLinh - 19127652
 * Date 3/29/2022 - 1:32 PM
 * Description: ...
 */
public class ShownProperty extends Property {

    private final String shownType;

    public ShownProperty(String name, String type)
    {
        super(name, null, false, false, false, false, null);
        this.shownType = type;
    }

    public String getShown()
    {
        return String.format("%s (%s)", name, shownType);
    }

    @Override
    public String getColumnTypeSQL() {
        return "";
    }
}
