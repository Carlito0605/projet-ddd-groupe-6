package esgi.fyc.model.kycStatus;

import esgi.fyc.use_case.DomainException;

import java.math.BigDecimal;

public class KycStatus {
   private final boolean isVerified;
   private static final BigDecimal KYC_THRESHOLD = BigDecimal.valueOf(2000);

   public KycStatus(boolean isVerified) {
      this.isVerified = isVerified;
   }

   public void verify(BigDecimal amount) {
      if (amount.compareTo(KYC_THRESHOLD) > 0 && !isVerified)
         throw new DomainException("Retrait supérieur à 2000€, vérification KYC requise.");
   }

   public boolean isVerified() {
      return isVerified;
   }

   public static KycStatus unverified() {
      return new KycStatus(false);
   }
}