import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectToDB {
    private Connection connection;
    private MysqlDataSource dataSource = new MysqlDataSource();

    public Connection getConnection() throws SQLException {
        if (!(this.connection != null && !this.connection.isClosed())) {
            this.connection = this.dataSource.getConnection();
        }

        return dataSource.getConnection();
    }

    public ConnectToDB(String dbName, String username, String password) {
        dataSource.setDatabaseName(dbName);
        dataSource.setServerName(Config.hostname);
        dataSource.setUser(username);
        dataSource.setPassword(password);
    }
}