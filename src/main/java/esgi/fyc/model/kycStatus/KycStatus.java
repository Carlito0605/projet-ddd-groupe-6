package esgi.fyc.model.kycStatus;

import esgi.fyc.model.money.Currency;
import esgi.fyc.model.money.Money;
import esgi.fyc.use_case.DomainException;

public class KycStatus {
   private final boolean isVerified;
   private static final Money KYC_THRESHOLD = new Money(500, Currency.EUR);

   public KycStatus(boolean isVerified) {
      this.isVerified = isVerified;
   }

   public void verify(Money amount) {
      if (amount.isUpperThan(KYC_THRESHOLD) && !isVerified)
         throw new DomainException("Retrait supérieur à " + KYC_THRESHOLD + ", vérification KYC requise.");
   }

   public boolean isVerified() {
      return isVerified;
   }

   public static KycStatus unverified() {
      return new KycStatus(false);
   }
}