package esgi.fyc.model.money;

import java.util.Objects;

public class Money {
    private int amount;
    private Currency currency;

    public Money(int amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money ZERO = new Money(0, Currency.EUR);

    public int getAmount() {
        return amount;
    }

    public Money add(Money money) {
        return new Money(amount + money.amount, currency);
    }

    public Money subtract(Money money) {
        return new Money(amount - money.amount, currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return money.amount == amount && money.currency.equals(currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }

    public boolean isUpperThan(Money money) {
        return amount > money.amount;
    }

    public boolean isLowerThan(Money money) {
        return amount < money.amount;
    }

    public boolean isPositive() {
        return amount > 0;
    }

    public Money max(Money money) {
        return isUpperThan(money) ? this : money;
    }
}
