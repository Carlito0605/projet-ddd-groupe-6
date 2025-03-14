package esgi.fyc.model.bonusStatus;

import esgi.fyc.model.money.Money;
import esgi.fyc.use_case.DomainException;

public class BonusStatus {
   private final Money bonusBalance;
   private final Money bonusWageringLeft;

   public BonusStatus(Money bonusBalance, Money bonusWageringLeft) {
      this.bonusBalance = bonusBalance;
      this.bonusWageringLeft = bonusWageringLeft;
   }

   public void verifyBonusConditions() {
      if (bonusBalance.isPositive() && bonusWageringLeft.isPositive())
         throw new DomainException("Bonus actif non complété : " + bonusWageringLeft + " à miser.");
   }

   public BonusStatus reduceWagering(Money amount) {
      Money newWageringLeft = bonusWageringLeft.subtract(amount).max(Money.ZERO);
      return new BonusStatus(bonusBalance, newWageringLeft);
   }

   public static BonusStatus noBonus() {
      return new BonusStatus(Money.ZERO, Money.ZERO);
   }

   public Money getBonusBalance() {
      return bonusBalance;
   }

   public Money getBonusWageringLeft() {
      return bonusWageringLeft;
   }
}