package esgi.fyc.model.money;

public class Money {
    private int amount;
    private Currency currency;

      public Money(int amount, Currency currency) {
         this.amount = amount;
         this.currency = currency;
      }

      public Money add(Money money) {
         if (!currency.equals(money.currency))
            throw new IllegalArgumentException("Les devises ne sont pas identiques.");
         return new Money(amount + money.amount, currency);
      }

      public Money subtract(Money money) {
         if (!currency.equals(money.currency))
            throw new IllegalArgumentException("Les devises ne sont pas identiques.");
         return new Money(amount - money.amount, currency);
      }

      @Override
      public boolean equals(Object obj) {
         return super.equals(obj);
      }

      @Override
      public int hashCode() {
         return super.hashCode();
      }

}
