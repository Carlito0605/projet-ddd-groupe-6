package esgi.fyc.model.kycStatus;

import esgi.fyc.exception.UnverifiedKycException;
import esgi.fyc.model.money.Currency;
import esgi.fyc.model.money.Money;

public class KycStatus {
   private final boolean isVerified;
   private static final Money KYC_THRESHOLD = new Money(500, Currency.EUR);

   public KycStatus(boolean isVerified) {
      this.isVerified = isVerified;
   }

   public void verify(Money amount) {
      if (amount.isUpperThan(KYC_THRESHOLD) && !isVerified)
         throw new UnverifiedKycException(KYC_THRESHOLD);
   }

   public boolean isVerified() {
      return isVerified;
   }

   public static KycStatus unverified() {
      return new KycStatus(false);
   }
}