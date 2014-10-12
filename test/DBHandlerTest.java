import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;

import static junit.framework.Assert.*;

public class DBHandlerTest {

    private DBHandler dbHandler;

    public DBHandler getDB() {
        return dbHandler;
    }

    @Before
    public void setUp() throws Exception {
        // Drop the table if it already exists.
        dbHandler = new DBHandler(Config.username, Config.password);
        if (getDB().getDb().tableExists(Config.table)) {
            getDB().getTable(Config.table).drop(getDB().getDb().getConnection());
        }

        testCopyFile();
    }

    public void testCopyFile() throws SQLException {
        getDB().copyFile(Config.filepath, Config.table);
        assertTrue("The requested table creation query should be successfully executed.",
                getDB().getDb().tableExists(Config.table));
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
    public void showTableTest() throws Exception {
        assertEquals("The generateCreatStatement() method should return a correct SQL statment",
                "CREATE TABLE " + Config.table + " (Navn INT(128), Adresse INT(128), Alder INT(3))",
                getDB().getTable(Config.table).generateCreateStatement());

        String[] insertStatements = getDB().getTable(Config.table).generateInsertValuesStatements();
        String[] expectedStatements = new String[]{
                "INSERT INTO " + Config.table + " VALUES ('Donald Duck', 'Uflaksveien 13', '60')",
                "INSERT INTO testTable VALUES ('Fetter Anton', 'Flakseveien 1', '50')",
                "INSERT INTO testTable VALUES ('Dolly Duck', 'Andebygrden 2', '55')"
        };

        for (int i = 0; i < expectedStatements.length; i++) {
            assertTrue("The generated statements should be valid",
                    expectedStatements[i].equalsIgnoreCase(insertStatements[i]));
        }
    }

    @Test
    public void copyFileFailTest() {
        boolean exceptionCaught = false;
        try {
            getDB().copyFile("invalidfile", Config.table);
        } catch (Exception e) {
            exceptionCaught = true;
        }
        assertTrue("An exception should be thrown by DBHandler.copyFile() and caught", exceptionCaught);
    }

    @Test
    public void truncateTableTest() throws Exception {
        getDB().getTable(Config.table).fillTable(true);
        assertEquals("After truncating and filling the table, it should contain exactly three rows.",
                3, getDB().getTable(Config.table).generateInsertValuesStatements().length);
    }

    @Test
    public void failReadFileTest() throws Exception {
        boolean fileNotFound = false;
        try {
            FileIO.readFileLines("thisfiledoesnotexist.txt");
        } catch (FileNotFoundException e) {
            fileNotFound = true;
        }

        assertTrue("The requested file should not be found.", fileNotFound);
    }

    @Test
    public void tableDoesntExistTest() throws SQLException {
        getDB().getTable(Config.table).drop(getDB().getDb().getConnection());
        assertFalse("Table should not exist when dropped.", getDB().getDb().tableExists(Config.table));
    }

    @Test
    public void clostTest() {
        getDB().close();
    }
}