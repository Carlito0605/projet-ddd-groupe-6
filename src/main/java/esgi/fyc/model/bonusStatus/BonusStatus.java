package esgi.fyc.model.bonusStatus;

import esgi.fyc.use_case.DomainException;

import java.math.BigDecimal;

public class BonusStatus {
   private final BigDecimal bonusBalance;
   private final BigDecimal bonusWageringLeft;

   public BonusStatus(BigDecimal bonusBalance, BigDecimal bonusWageringLeft) {
      this.bonusBalance = bonusBalance;
      this.bonusWageringLeft = bonusWageringLeft;
   }

   public void verifyBonusConditions() {
      if (bonusBalance.compareTo(BigDecimal.ZERO) > 0 && bonusWageringLeft.compareTo(BigDecimal.ZERO) > 0)
         throw new DomainException("Bonus actif non complété : " + bonusWageringLeft + "€ à miser.");
   }

   public BonusStatus reduceWagering(BigDecimal amount) {
      BigDecimal newWageringLeft = bonusWageringLeft.subtract(amount).max(BigDecimal.ZERO);
      return new BonusStatus(bonusBalance, newWageringLeft);
   }

   public static BonusStatus noBonus() {
      return new BonusStatus(BigDecimal.ZERO, BigDecimal.ZERO);
   }

   public BigDecimal getBonusBalance() {
      return bonusBalance;
   }

   public BigDecimal getBonusWageringLeft() {
      return bonusWageringLeft;
   }
}