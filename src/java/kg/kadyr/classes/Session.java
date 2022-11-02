package kg.kadyr.classes;

import java.sql.*;

public class Session {
    Connection connection;
    boolean loggedIn = false;
    Account account = null;

    public Session(Connection sessionConnection) {
        connection = sessionConnection;
    }

    public Account getAccount() {
        return account;
    }

    public Connection getConnection() {
        return connection;
    }

    public void login(Account sessionAccount) {
        account = sessionAccount;
        loggedIn = true;
    }


    public void unLogin() {
        account = null;
        loggedIn = false;
    }
}
