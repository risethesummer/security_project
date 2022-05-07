package users.dbHandler;

import users.dao.DBRecord;
import users.dao.Document;

import java.util.Collection;

/**
 * users.dbHandler
 * Created by NhatLinh - 19127652
 * Date 5/5/2022 - 2:25 PM
 * Description: ...
 */
public abstract class DBUserHandler {
    protected final String userName;
    protected final String password;

    public DBUserHandler(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    public Iterable<DBRecord> getPatients()
    {

    }

    public Iterable<DBRecord> getStaffs()
    {

    }

    public Iterable<DBRecord> getFacilities()
    {

    }

    public Collection<String> getServiceIDs()
    {

    }

    public Collection<String> getStaffIDs()
    {

    }

    public Iterable<DBRecord> getDocuments()
    {

    }

    public boolean createDocument(Document document)
    {

    }
}