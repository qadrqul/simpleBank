package kg.kadyr.menu;

import kg.kadyr.classes.Account;
import kg.kadyr.utils.LuhnAlgorithm;
import kg.kadyr.Main;
import kg.kadyr.classes.Card;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class UserMenu {
    final static Scanner scanner = new Scanner(System.in, "Cp866")
    public static void main() throws SQLException {
    }

    private static void balance() throws SQLException {
        Statement stmt = Main.session.getConnection().createStatement();
        Account account = Main.session.getAccount();
        String sql = "SELECT balance FROM card WHERE number = " + account.getCard().getNumber();

        long balance = stmt.executeQuery(sql).getLong("balance");
        account.getCard().setBalance(balance);

        System.out.println("\n| Текущий баланс: " + balance);

        stmt.close();
        loggedIn();
    }
}
