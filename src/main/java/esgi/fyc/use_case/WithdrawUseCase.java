package esgi.fyc.use_case;

import esgi.fyc.model.player.Player;
import esgi.fyc.model.player.PlayerRepository;

import java.math.BigDecimal;
import java.time.LocalDate;


class WithdrawUseCase {
   private static final BigDecimal DAILY_LIMIT = BigDecimal.valueOf(1000);
   private static final BigDecimal MONTHLY_LIMIT = BigDecimal.valueOf(5000);
   private static final BigDecimal VERIFICATION_THRESHOLD = BigDecimal.valueOf(2000);

   private PlayerRepository playerRepository;

   public void execute(String playerId, BigDecimal amount, LocalDate date) {
      Player player = playerRepository.find(playerId);

      if (player.isSuspended()) {
         throw new DomainException("Le compte du joueur est suspendu : "
                                   + player.getSuspensionReason());
      }

      if (player.getBalance().compareTo(amount) < 0) {
         throw new DomainException("Solde insuffisant pour effectuer ce retrait.");
      }

      BigDecimal dailyTotal = player.getDailyWithdrawal(date);
      if (dailyTotal.add(amount).compareTo(DAILY_LIMIT) > 0) {
         throw new DomainException("Limite journalière dépassée : max " + DAILY_LIMIT + "€");
      }

      String yearMonth = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
      BigDecimal monthlyTotal = player.getMonthlyWithdrawal(yearMonth);
      if (monthlyTotal.add(amount).compareTo(MONTHLY_LIMIT) > 0) {
         throw new DomainException("Limite mensuelle dépassée : max " + MONTHLY_LIMIT + "€");
      }

      if (amount.compareTo(VERIFICATION_THRESHOLD) > 0 && !player.isKycVerified()) {
         throw new DomainException("Retrait supérieur à 2000€, vérification KYC requise.");
      }

      if (player.getBonusBalance().compareTo(BigDecimal.ZERO) > 0 &&
          player.getBonusWageringLeft().compareTo(BigDecimal.ZERO) > 0) {
         throw new DomainException("Impossible de retirer : bonus actif non complété ("
                                   + player.getBonusWageringLeft() + "€ à miser).");
      }

      player.subtractFromBalance(amount);
      player.recordWithdrawal(amount, date);
   }
}