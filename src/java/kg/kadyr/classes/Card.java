package kg.kadyr.classes;
import kg.kadyr.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Card {
    private static final Random random = new Random();

    long cardNumber;
    short cardPin;
    long balance;

    public Card(long number) {
        cardNumber = number;
        balance = 0;

        StringBuilder pinString = new StringBuilder();

        for (int i = 1; i <= 4; i++) {
            if (i == 1) {
                pinString.append(1 + random.nextInt(9));
                continue;
            }
            pinString.append(random.nextInt(10));
        }

        cardPin = Short.parseShort(pinString.toString());
    }
    public Card(long number, short pin) {
        cardNumber = number;
        balance = 0;
        cardPin = pin;
    }

    public long getNumber() {
        return cardNumber;
    }
    public short getPin() {
        return cardPin;
    }
    public void addBalance(long count) {
        balance += count;
    }
    public void setBalance(long count) {
        balance = (int) count;
    }
    public boolean isExists() throws SQLException {
        Statement stmt = Main.session.getConnection().createStatement();

        String sql = "SELECT * FROM card";
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            if (rs.getString("number").equals(String.valueOf(cardNumber))) {
                if (rs.getString("pin").equals(String.valueOf(cardPin))) {
                    stmt.close();
                    return true;
                } else {
                    stmt.close();
                    return false;
                }
            }
        }
        stmt.close();
        return false;
    }
    public static boolean isExists(long cardNumber) throws SQLException {
        Statement stmt = Main.session.getConnection().createStatement();

        String sql = "SELECT * FROM card";
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            if (rs.getString("number").equals(String.valueOf(cardNumber))) {
                stmt.close();
                return true;
            }
        }
        stmt.close();
        return false;
    }
}
