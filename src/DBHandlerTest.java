import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class DBHandlerTest {

    private DBHandler dbHandler;

    public DBHandler getDB() {
        return dbHandler;
    }

    @Before
    public void setUp() throws Exception {
        dbHandler = new DBHandler(Config.username, Config.password);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDbConnection() throws Exception {
        assertFalse("A valid, open connection should be presented.", getDB().getDb().getConnection().isClosed());
    }

    @Test
    public void testAutoClosable() throws SQLException {
        Connection connectionPointer = null;
        try (Connection connection = getDB().getDb().getConnection()) {
            assertFalse("A connection should be established", connection.isClosed());
            connectionPointer = connection;
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        assertTrue("The establised connection should be closed after the try-with-resources block.",
                connectionPointer != null && connectionPointer.isClosed());
    }

    @Test
    public void testCopyFile() throws SQLException {
        DBHandler.copyFile(Config.filepath, Config.table);

        // Modified version of http://stackoverflow.com/questions/2942788/check-if-table-exists
        // Original author: Brian Agnew (http://stackoverflow.com/users/12960/brian-agnew)
        DatabaseMetaData meta = getDB().getDb().getConnection().getMetaData();
        ResultSet res = meta.getTables(null, null, null, new String[]{"TABLE"});

        boolean tableExists = false;
        while (res.next() && !tableExists) {
            tableExists = res.getString("TABLE_NAME").equalsIgnoreCase(Config.table);
        }

        assertTrue("The requested table creation query should be successfully executed.", tableExists);
    }

    @Test
    public void showTableTest() throws Exception {
        System.out.println(getDB().getTable(Config.table).generateCreateStatement());
    }
}