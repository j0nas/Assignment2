import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectToDB {
    private Connection connection;
    private MysqlDataSource dataSource = new MysqlDataSource();

    public ConnectToDB(String hostname, String dbName, String username, String password) {
        dataSource.setDatabaseName(dbName);
        dataSource.setServerName(hostname);
        dataSource.setUser(username);
        dataSource.setPassword(password);
    }

    public Connection getConnection() throws SQLException {
        if (!(this.connection != null && !this.connection.isClosed())) {
            this.connection = this.dataSource.getConnection();
        }

        return this.connection;
    }

    // Modified version of http://stackoverflow.com/questions/2942788/check-if-table-exists
    // Original author: Brian Agnew (http://stackoverflow.com/users/12960/brian-agnew)
    public boolean tableExists(String tablename) throws SQLException {
        DatabaseMetaData databaseMetaData = getConnection().getMetaData();
        ResultSet tables = databaseMetaData.getTables(null, null, tablename, null);
        return tables.next();
    }
}