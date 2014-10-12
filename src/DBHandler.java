import java.sql.SQLException;

public class DBHandler implements AutoCloseable {
    public final ConnectToDB db;

    public DBHandler(String user, String password) {
        db = new ConnectToDB(Config.database, user, password);
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

    public static void copyFile(String filename, String tablename) {
        TableObject tableObject = new TableObject(filename, tablename);
        try {
            tableObject.createTable(true);
            tableObject.fillTable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TableObject getTable(String tableName) throws SQLException {
        return new TableObject(getDb().getConnection(), tableName);
    }
}