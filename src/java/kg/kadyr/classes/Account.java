package kg.kadyr.classes;

public class Account {
    String name;
    Card card;

    public Account(Card accountCard, String accountName) {
        card = accountCard;
        name = accountName;
    }

    public String getName() {
        return name;
    }

    public Card getCard() {
        return card;
    }
}
