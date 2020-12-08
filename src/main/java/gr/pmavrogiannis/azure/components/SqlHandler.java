package gr.pmavrogiannis.azure.components;

import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;

public class SqlHandler {

    private String server = "";
    private String database = "";
    private String login = "";
    private String password = "";
    private String connectionUrl = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
            + "hostNameInCertificate=*.database.windows.net;loginTimeout=30;", this.server, this.database, this.login, this.password);

    public String getServer() {
        return server;
    }

    public String getDatabase() {
        return database;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SqlHandler(String server, String database, String login, String password) {
        this.server = server;
        this.database = database;
        this.login = login;
        this.password = password;
        connectionUrl = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;"
                + "hostNameInCertificate=*.database.windows.net;loginTimeout=30;", this.server, this.database, this.login, this.password);
    }

    // Behaviour


    public void insertInto(String query) {
        executeQueryWithoutResults(query);
    }

    public void copyDatabaseFrom(String sourceSqlServer, String sourceDatabase, String elasticPool) {
        String query = String.format("CREATE DATABASE %s AS COPY OF %s.%s (SERVICE_OBJECTIVE = ELASTIC_POOL( name = %s ));",
                sourceDatabase + "_copy",
                sourceSqlServer.split(".database.windows.net")[0],
                sourceDatabase,
                elasticPool
        );
        executeQueryWithoutResults(query);
    }

    public void copyDatabaseFrom(SqlHandler source) {
        String query = String.format("CREATE DATABASE %s AS COPY OF %s.%s;",
                source.getDatabase() + "_copy",
                source.getServer().split(".database.windows.net")[0],
                source.getDatabase()
        );
        executeQueryWithoutResults(query);
    }

    public void executeStoredProcedure(String spName, String... params) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        StringBuilder queryBuilder = new StringBuilder("");
        String query = "";

        queryBuilder.append(String.format("exec %s ", spName));

        for (String param : params) {
            queryBuilder.append(String.format("N'%s', ", param));
        }
        query = queryBuilder.substring(0, queryBuilder.length() - 2);
        executeQueryWithResults(query);
    }

    public void renameDatabase(SqlHandler databaseToBeRenamed, String newDatabaseName) {
        String query = String.format("ALTER DATABASE %s MODIFY NAME = %s", databaseToBeRenamed.getDatabase(), newDatabaseName);
        executeQueryWithoutResults(query);
    }

    public void dropDatabase(String databaseName) {
        String query = String.format("drop database %s", databaseName);
        executeQueryWithoutResults(query);
    }

    public void dropUser(String userName) {
        String query = String.format("drop user %s", userName);
        executeQueryWithoutResults(query);
    }

    public void createUser(String userName, String fromLogin) {
        String query = String.format("create user %s from login %s", userName, fromLogin);
        executeQueryWithoutResults(query);
    }

    public ArrayList<String> executeQueryWithResults(String query) {
        ArrayList<String> results = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(connectionUrl);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                results.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(resultSet);
            DbUtils.closeQuietly(statement);
            DbUtils.closeQuietly(connection);
            return results;
        }
    }

    public void executeQueryWithoutResults(String query) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(connectionUrl);
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(connection);
        }
    }

}
