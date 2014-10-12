import java.sql.SQLException;

public class DBHandler implements AutoCloseable {
    public final ConnectToDB db;
    private TableObject tableObject;

    public DBHandler(String user, String password) {
        db = new ConnectToDB(Config.hostname, Config.database, user, password);
    }

    public ConnectToDB getDb() {
        return db;
    }

    public void close() {
        try {
            getDb().getConnection().close();
        } catch (SQLException ignored) {
        }
    }

    public void copyFile(String filename, String tablename) {
        this.tableObject = new TableObject(filename, tablename);
        try {
            this.tableObject.createTable(true);
            this.tableObject.fillTable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TableObject getTable(String tableName) throws SQLException {
        return new TableObject(getDb().getConnection(), tableName);
    }
}