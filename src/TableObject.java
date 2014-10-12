import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class TableObject {
    private String tableName;
    private String[] headerNames;
    private String[] headerTypes;


    private String[] headerWidths;
    private ArrayList<String[]> rowData = new ArrayList<>();

    public TableObject(String filename, String tablename) {
        FileIO.assertExists(filename);
        this.tableName = tablename;

        String[] fileLines = new String[0];
        try {
            fileLines = FileIO.readFileLines(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        headerNames = fileLines[0].split(Config.filelineSplit);
        headerTypes = fileLines[1].split(Config.filelineSplit);
        headerWidths = fileLines[2].split(Config.filelineSplit);

        if (fileLines.length > 3) {
            for (int i = 3; i < fileLines.length; i++) {
                rowData.add(fileLines[i].split(Config.filelineSplit));
            }
        }
    }

    public TableObject(Connection connection, String tablename) throws SQLException {
        DBHandler dbHandler = new DBHandler();
        if (!dbHandler.getDb().tableExists(tablename)) {
            throw new RuntimeException("Table '" + tablename + "' does not exist!");
        }

        this.tableName = tablename;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM %s LIMIT 100000", tablename));

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        // Fetch header column names
        headerNames = new String[resultSetMetaData.getColumnCount()];
        headerTypes = new String[resultSetMetaData.getColumnCount()];
        headerWidths = new String[resultSetMetaData.getColumnCount()];
        for (int i = 1; i <= headerNames.length; i++) {
            headerNames[i - 1] = resultSetMetaData.getColumnLabel(i);
            headerTypes[i - 1] = resultSetMetaData.getColumnTypeName(i);
            headerWidths[i - 1] = String.valueOf(resultSetMetaData.getColumnDisplaySize(i));
        }

        while (resultSet.next()) {
            rowData.add(new String[resultSetMetaData.getColumnCount()]);
            for (int j = 1; j <= resultSetMetaData.getColumnCount(); j++) {
                rowData.get(rowData.size() - 1)[j - 1] = resultSet.getString(j);
            }
        }
    }

    public void drop(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DROP TABLE IF EXISTS " + getTableName());
    }

    public String getTableName() {
        return tableName;
    }

    public String generateCreateStatement() throws Exception {
        if (!(headerNames.length == headerTypes.length && headerTypes.length == headerWidths.length)) {
            throw new RuntimeException("Provided column data arrays do not match in length.");
        }

        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE " + getTableName() + " (");
        for (int i = 0; i < headerNames.length; i++) {
            stringBuilder
                    .append(headerNames[i])
                    .append(" ")
                    .append(headerTypes[i].equalsIgnoreCase("string") ? "VARCHAR" : "INT")
                    .append("(")
                    .append(headerWidths[i])
                    .append(")")
                    .append(i != headerNames.length - 1 ? ", " : ")");
        }

        return stringBuilder.toString();
    }

    public String[] generateInsertValuesStatements() {
        if (rowData.size() < 1) {
            throw new RuntimeException("No data to insert.");
        }

        String insertStatement = "INSERT INTO " + getTableName() + " VALUES (";
        ArrayList<String> resultQueries = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder(insertStatement);
        for (String[] aRowData : rowData) {
            Arrays.asList(aRowData).forEach(value -> stringBuilder.append("\'").append(value).append("\', "));
            String builtString = stringBuilder.toString();
            resultQueries.add(builtString.substring(0, builtString.length() - 2) + ")");
            stringBuilder.delete(0, builtString.length()).append(insertStatement);
        }

        return resultQueries.toArray(new String[resultQueries.size()]);
    }

    public void createTable(boolean deleteExisting) throws Exception {
        DBHandler dbHandler = new DBHandler(Config.username, Config.password);
        Statement statement = dbHandler.getDb().getConnection().createStatement();
        if (deleteExisting) {
            statement.executeUpdate("DROP TABLE IF EXISTS " + getTableName());
        }

        statement.executeUpdate(generateCreateStatement());
    }

    public void fillTable(boolean truncateFirst) throws Exception {
        DBHandler dbHandler = new DBHandler(Config.username, Config.password);
        if (!dbHandler.getDb().tableExists(getTableName())) {
            throw new RuntimeException("Table " + getTableName() + " does not exist!");
        }

        Statement statement = dbHandler.getDb().getConnection().createStatement();
        if (truncateFirst) {
            statement.executeUpdate(String.format("TRUNCATE TABLE " + getTableName()));
        }

        String[] updateQueries = generateInsertValuesStatements();
        for (String updateQuery : updateQueries) {
            statement.executeUpdate(updateQuery);
        }
    }
}
