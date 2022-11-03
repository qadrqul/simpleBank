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
    final static Scanner scanner = new Scanner(System.in, "Cp866");
    public static void main() throws SQLException {
        System.out.println("""
                                
                | Выберите желаемое действие:
                |  1. Зарегистрироваться
                |  2. Войти в существующий аккаунт
                |  0. Выйти из системы""");

        System.out.print("> ");

        int userOperation = scanner.nextInt();
        switch (userOperation) {
            case 1 -> createAccount();
            case 2 -> loginAccount();
            case 0 -> {
                System.out.println("\n| До новых встреч!\n");
                Main.session.getConnection().close();
            }
            default -> {
                System.out.println("\n| Неизвестная операция");
                main();
            }
        }
    }
    private static void createAccount() throws SQLException {
        System.out.println("| Как вас зовут?");
        System.out.print("> ");
        String name = scanner.next();

        Card card = new Card(LuhnAlgorithm.generate());

        String sql = "INSERT INTO card (name, number, pin, balance) VALUES (" + "'" + name + "'" + ", " + card.getNumber() + ", " + card.getPin() + ", " + 0 + ")";
        Statement stmt = Main.session.getConnection().createStatement();
        stmt.execute(sql);

        System.out.printf("""
                                
                | %s, вы успешно зарегистрировались в системе!
                | Номер карты: %s
                | Пин-код: %s
                |
                | Для входа в аккаунт вам необходим номер карты и пин-код%n""", name, card.getNumber(), card.getPin());

        stmt.close();
        main();
    }
    private static void loginAccount() throws SQLException {
        System.out.println("| Введите номер карты:");
        System.out.print("> ");
        long cardNumber = scanner.nextLong();

        if (LuhnAlgorithm.numberLuhnAlgorithmCheck(cardNumber)) {
            System.out.println("\n| Номер карты не соответствует алгоритму Луна!\n");
            main();
            return;
        }

        System.out.println("| Введите пин-код карты:");
        System.out.print("> ");
        short cardPin = scanner.nextShort();

        Card card = new Card(cardNumber, cardPin);

        if (!card.isExists()) {
            System.out.println("\n| Неверный номер карты или пин-код!\n");
            main();

        } else {
            Statement stmt = Main.session.getConnection().createStatement();

            String sql = "SELECT name FROM card WHERE number = " + cardNumber;
            String name = stmt.executeQuery(sql).getString("name");

            sql = "SELECT balance FROM card WHERE number = " + cardNumber;
            long balance = stmt.executeQuery(sql).getLong("balance");

            card.setBalance(balance);
            Account account = new Account(card, name);
            Main.session.login(account);
            stmt.close();
            System.out.printf("\n| Добро пожаловать, %s!%n", account.getName());

            loggedIn();
        }
    }

    private static void loggedIn() throws SQLException {
        System.out.println("""
                                    
                | Выберите желаемое действие:
                |  1. Посмотреть баланс
                |  2. Добавить деньги на баланс
                |  3. Перевести деньги другому пользователю
                |  0. Выйти из аккаунта""");

        System.out.print("> ");
        int userOperation = scanner.nextInt();
        switch (userOperation) {
            case 1 -> balance();
            case 2 -> addBalance();
            case 3 -> transfer();
            case 0 -> {
                System.out.println("\n| Вы успешно вышли из аккаунт!\n");
                Main.session.unLogin();
                main();
            }
            default -> {
                System.out.println("\n| Неизвестная операция");
                loggedIn();
            }
        }
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
    private static void addBalance() throws SQLException {
        Statement stmt = Main.session.getConnection().createStatement();
        Account account = Main.session.getAccount();

        System.out.println("\n| Введите желаемое количество");
        System.out.print("> ");
        long count = scanner.nextLong();

        String sql = "SELECT balance FROM card WHERE number = " + account.getCard().getNumber();
        ResultSet rs = stmt.executeQuery(sql);
        long balance = rs.getLong("balance");
        balance += count;

        sql = "UPDATE card SET balance = " + balance + " WHERE number = " + account.getCard().getNumber();
        stmt.execute(sql);
        account.getCard().addBalance(count);

        System.out.printf("""
                                            
                | На ваше баланс успешно добавлено %s денег
                | Текущий баланс: %s%n""", count, balance);

        stmt.close();
        loggedIn();
    }
    private static void transfer() throws SQLException {
        Statement stmt = Main.session.getConnection().createStatement();
        Account account = Main.session.getAccount();

        System.out.println("\n| Введите номер карты пользователя, которому хотите перевести деньги");
        System.out.print("> ");
        long cardNumber = scanner.nextLong();

        if (LuhnAlgorithm.numberLuhnAlgorithmCheck(cardNumber)) {
            System.out.println("\n| Номер карты пользователя не соответствует алгоритму Луна!\n");
            loggedIn();
            return;

        } else if (!Card.isExists(cardNumber)) {
            System.out.println("\n| Пользователь с таким номером карты не найден!");
            loggedIn();
            return;
        }

        System.out.println("\n| Введите желаемое количество");
        System.out.print("> ");
        long count = scanner.nextLong();

        String sql = "SELECT balance FROM card WHERE number = " + account.getCard().getNumber();
        long balanceClient = stmt.executeQuery(sql).getLong("balance");

        if (balanceClient < count) {
            System.out.println("\n| Недостаточно денег на балансе!");
            loggedIn();
            return;
        }

        sql = "SELECT balance FROM card WHERE number = " + cardNumber;
        long balanceRecipient = stmt.executeQuery(sql).getLong("balance");

        sql = "SELECT name FROM card WHERE number = " + cardNumber;
        String nameRecipient = stmt.executeQuery(sql).getString("name");

        sql = "UPDATE card SET balance = " + (balanceClient - count) + " WHERE number = " + account.getCard().getNumber();
        stmt.execute(sql);

        sql = "UPDATE card SET balance = " + (balanceRecipient + count) + " WHERE number = " + cardNumber;
        stmt.execute(sql);
        account.getCard().addBalance(-count);

        System.out.printf("""
                                            
                | Пользователю %s успешно переведено %s денег
                | Текущий баланс: %s%n""", nameRecipient, count, balanceClient - count);

        stmt.close();
        loggedIn();

        // ура, я всё сделал
    }
}
